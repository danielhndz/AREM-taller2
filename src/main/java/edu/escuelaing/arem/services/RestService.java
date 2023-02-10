package edu.escuelaing.arem.services;

public interface RestService {

    static final String RESOURCES_DIR = "src/main/res";

    public String getHeader(String path);

    public String getResponse(String path);
}
