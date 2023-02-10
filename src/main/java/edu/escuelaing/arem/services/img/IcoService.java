package edu.escuelaing.arem.services.img;

import edu.escuelaing.arem.services.RestService;
import edu.escuelaing.arem.utils.FilesReader;

public class IcoService implements RestService {

    @Override
    public String getHeader(String path) {
        return new StringBuilder("HTTP/1.1 200 OK\r\n")
                .append("Content-Type: image/ico\r\n\r\n")
                .toString();
    }

    @Override
    public String getResponse(String path) {
        return FilesReader.img(path);
    }

}
