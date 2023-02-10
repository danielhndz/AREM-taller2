package edu.escuelaing.arem.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class FilesReader {

    private static final String RESOURCES_DIR = "src/main/resources";
    private static final String NOT_FOUND_PAGE = "/404/404.html";

    private FilesReader() {
    }

    public static String img(String path) {
        byte[] content = null;
        try {
            content = Files.readAllBytes(
                    Path.of("", (RESOURCES_DIR + path)
                            .replace("/", System.getProperty("file.separator"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(content);
    }

    public static String text(String path) {
        String line;
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(
                Paths.get("", (RESOURCES_DIR + path)
                        .replace("/", System.getProperty("file.separator"))).toFile()))) {
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            text(NOT_FOUND_PAGE);
        }
        return response.toString();
    }
}
