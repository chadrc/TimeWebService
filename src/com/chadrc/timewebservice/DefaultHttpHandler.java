package com.chadrc.timewebservice;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Created by Chad on 5/17/2016.
 */
public class DefaultHttpHandler implements HttpHandler {
    private String indexText;

    DefaultHttpHandler() {
        indexText = "<p>Date-Time Service</p>";
        File file = new File("index.html");
        try (FileInputStream stream = new FileInputStream(file)) {
            byte[] buffer = new byte[(int)file.length()];
            stream.read(buffer);
            indexText = new String(buffer, StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            System.out.println("Failed to load index.html. File not found.");
        } catch (IOException e) {
            System.out.println("Failed to load index.html.");
        }
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(200, indexText.length());
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(indexText.getBytes());
        outputStream.close();
    }
}
