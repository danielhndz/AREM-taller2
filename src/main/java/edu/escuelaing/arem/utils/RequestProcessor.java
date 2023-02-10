package edu.escuelaing.arem.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.escuelaing.arem.apis.OMDbAPI;
import edu.escuelaing.arem.cache.Cache;
import edu.escuelaing.arem.server.HttpServer;
import edu.escuelaing.arem.services.MovieSearchService;
import edu.escuelaing.arem.services.RestService;
import edu.escuelaing.arem.services.img.IcoService;
import edu.escuelaing.arem.services.img.JpgService;
import edu.escuelaing.arem.services.img.PngService;
import edu.escuelaing.arem.services.text.CssService;
import edu.escuelaing.arem.services.text.HtmlService;
import edu.escuelaing.arem.services.text.JsService;
import edu.escuelaing.arem.services.text.JsonService;
import edu.escuelaing.arem.services.text.PlainService;

public class RequestProcessor {

    private static final Logger LOGGER = Logger.getLogger(RequestProcessor.class.getName());
    private static final String INDEX_PAGE = "/index.html";

    private RequestProcessor() {
    }

    public static void run(Socket clientSocket) throws IOException {
        String inputLine;
        StringBuilder request = new StringBuilder();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        boolean firstLine = true;
        while ((inputLine = in.readLine()) != null) {
            if (firstLine) {
                process(clientSocket, inputLine, request);
                firstLine = false;
            } else {
                request.append("\n\t" + inputLine);
            }
            if (!in.ready()) {
                break;
            }
        }
        LOGGER.log(Level.INFO,
                "\n\tServer side\n\tReceived: {0}\n", request);
        in.close();
        clientSocket.close();
    }

    @SuppressWarnings({ "java:S1075" })
    private static void process(
            Socket clientSocket, String inputLine,
            StringBuilder request) throws IOException {
        String path;
        String method = parseMethod(inputLine);
        request.append(inputLine);
        if (method != null) {
            path = inputLine.replace(method + " ", "");
        } else {
            path = inputLine;
        }
        path = path.replace(" HTTP/1.0", "")
                .replace(" HTTP/1.1", "");
        if (path.startsWith("/")) {
            LOGGER.log(Level.INFO, "\n\tPath: {0}\n", path);
            switch (path) {
                case "/":
                    path = INDEX_PAGE;
                    break;
                case "/favicon.ico":
                    path = "/favicon/favicon.ico";
                    break;
                default:
                    break;
            }
        }
        if (path.toLowerCase().startsWith("/exit")) {
            exit(clientSocket);
        } else if (path.startsWith("/api/movies")) {
            moviesAPI(clientSocket, path);
        } else {
            processFile(clientSocket, path);
        }
    }

    private static void processFile(Socket clientSocket, String path) throws IOException {
        String ext = getExtension(path);
        RestService restService = getRestServiceByExtension(ext);
        if (ext.equals("png") || ext.equals("jpg") || ext.equals("ico")) {
            DataOutputStream out = new DataOutputStream(
                    clientSocket.getOutputStream());
            out.writeBytes(restService.getHeader(path));
            out.write(
                    Base64.getDecoder().decode(restService.getResponse(path)));
            out.close();
        } else {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
            out.write(
                    restService.getHeader(path)
                            + restService.getResponse(path));
            out.close();
        }
    }

    private static String getExtension(String path) {
        if (path.endsWith(".png")) {
            return "png";
        } else if (path.endsWith(".jpg")) {
            return "jpg";
        } else if (path.endsWith(".ico")) {
            return "ico";
        } else if (path.endsWith(".html")) {
            return "html";
        } else if (path.endsWith(".css")) {
            return "css";
        } else if (path.endsWith(".js")) {
            return "js";
        } else if (path.endsWith(".json")) {
            return "json";
        } else {
            return "plain";
        }
    }

    private static RestService getRestServiceByExtension(String ext) {
        switch (ext) {
            case "png":
                return new PngService();
            case "jpg":
                return new JpgService();
            case "ico":
                return new IcoService();
            case "html":
                return new HtmlService();
            case "css":
                return new CssService();
            case "js":
                return new JsService();
            case "json":
                return new JsonService();
            default:
                return new PlainService();
        }
    }

    private static void moviesAPI(Socket clientSocket, String path) throws IOException {
        PrintWriter in = new PrintWriter(clientSocket.getOutputStream(), true);
        if (path.startsWith("/api/movies/restart_all")) {
            Cache.getInstance().clear();
            in.println("\n\tClient side\n\tCache cleared ...");
            OMDbAPI.getInstance().resetRequestsToOMDbAPI();
            in.println("\n\tClient side" +
                    "\n\tOMDbAPI request counter restarted ...");
            in.close();
        } else if (path.startsWith("/api/movies/reqs_OMDbAPI")) {
            in.println(OMDbAPI.getInstance().getRequestsToOMDbAPI());
            in.close();
        } else if (path.startsWith("/api/movies/cache_size")) {
            in.println(Cache.getInstance().size());
            in.close();
        } else {
            RestService restService = new MovieSearchService();
            in.println(
                    restService.getHeader(path)
                            + restService.getResponse(path));
            in.close();
        }
    }

    private static void exit(Socket clientSocket) {
        try (PrintWriter in = new PrintWriter(
                clientSocket.getOutputStream(), true)) {
            StringBuilder response = new StringBuilder("HTTP/1.1 200 OK\r\n")
                    .append("Content-Type: text/plain\r\n\r\n")
                    .append("\tClient side\n\tStopping server ...");
            in.println(response);
            HttpServer.getInstance().stop();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error stopping server.");
            e.printStackTrace();
        }
    }

    private static String parseMethod(String inputLine) {
        if (inputLine.startsWith("GET")) {
            return "GET";
        } else if (inputLine.startsWith("POST")) {
            return "POST";
        }
        return null;
    }
}
