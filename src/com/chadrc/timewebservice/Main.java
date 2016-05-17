package com.chadrc.timewebservice;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class Main {

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
            server.createContext("/", new DefaultHttpHandler());
            server.createContext("/time", new TimeHttpHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("Server Started");
        } catch(UnknownHostException e) {
            System.out.println("Could not create server. Failed to get local host.");
        } catch(IOException e) {
            System.out.println("Could not create server. Failed to connect to socket 8000.");
        }
    }

    private static class DefaultHttpHandler implements HttpHandler {
        private String indexText;

        public DefaultHttpHandler() {
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

    private static class TimeHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String timeString = "<p>" + new Date().toString() + "</p>";
            for (String k :
                    httpExchange.getRequestHeaders().keySet()) {
                System.out.println(k + ":" + httpExchange.getRequestHeaders().get(k));
            }
            httpExchange.sendResponseHeaders(200, timeString.length());
            OutputStream outputStream = httpExchange.getResponseBody();
            outputStream.write(timeString.getBytes());
            outputStream.close();
        }
    }
}
