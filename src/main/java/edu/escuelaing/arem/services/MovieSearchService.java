package edu.escuelaing.arem.services;

import edu.escuelaing.arem.apis.OMDbAPI;

public class MovieSearchService implements RestService {

    @Override
    public String getHeader(String path) {
        return new StringBuilder("HTTP/1.1 200 OK\r\n")
                .append("Content-Type: application/json\r\n\r\n")
                .toString();
    }

    @Override
    public String getResponse(String path) {
        if (path.startsWith("/api/movies?t=")) {
            return OMDbAPI
                    .getInstance()
                    .requestByTitle(path.replace("/api/movies?t=", ""));
        }
        return "Title not valid";
    }

}
