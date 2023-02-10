package edu.escuelaing.arem;

import edu.escuelaing.arem.server.HttpServer;

public class Launcher {
    public static void main(String[] args) {
        HttpServer server = HttpServer.getInstance();
        server.run();
    }
}
