package edu.escuelaing.arem.services.text;

import edu.escuelaing.arem.services.RestService;
import edu.escuelaing.arem.utils.FilesReader;

public class HtmlService implements RestService {

    @Override
    public String getHeader(String path) {
        return new StringBuilder("HTTP/1.1 200 OK\r\n")
                .append("Content-Type: text/html\r\n\r\n")
                .toString();
    }

    @Override
    public String getResponse(String path) {
        return FilesReader.text(path);
    }

}
