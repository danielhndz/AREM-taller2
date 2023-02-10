package edu.escuelaing.arem.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.escuelaing.arem.utils.RequestProcessor;

public class HttpServer {

    private static final Logger LOGGER = Logger
            .getLogger(HttpServer.class.getName());
    private static final int PORT = 35000;
    private static HttpServer instance;
    private boolean running;

    private HttpServer() {
    }

    public static HttpServer getInstance() {
        if (instance == null) {
            instance = new HttpServer();
        }
        return instance;
    }

    @SuppressWarnings({ "java:S2189", "java:S2589" })
    public void run() {
        Socket clientSocket = null;
        ServerSocket serverSocket = null;
        running = true;
        while (running) {
            try {
                serverSocket = new ServerSocket(PORT);
                LOGGER.log(Level.INFO, "Server listen on port {0}", PORT);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Could not listen on port {0}.", PORT);
                System.exit(1);
            }
            try {
                clientSocket = serverSocket.accept();
                LOGGER.log(Level.INFO, "Client socket accepted ...");
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Accept failed.");
                System.exit(1);
            }
            try {
                RequestProcessor.run(clientSocket);
                serverSocket.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Close server failed.");
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public void stop() {
        if (running) {
            running = false;
        }
    }

}
