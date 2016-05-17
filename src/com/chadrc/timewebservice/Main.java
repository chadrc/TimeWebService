package com.chadrc.timewebservice;

import com.sun.istack.internal.NotNull;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Main {

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
            server.createContext("/", new DefaultHttpHandler());
            server.createContext("/time", new DateTimeHttpHandler(new SimpleDateFormat("HH:mm:ss z")));
            server.createContext("/date", new DateTimeHttpHandler(new SimpleDateFormat("E M-d-y")));
            server.createContext("/datetime", new DateTimeHttpHandler(new SimpleDateFormat("E M-d-y HH:mm:ss z")));
            server.createContext("/custom", new CustomDateTimeHttpHandler());
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

    private static class CustomDateTimeHttpHandler extends DateTimeHttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) {
            ArrayList<String> uriParams = splitTrimRequestURI(httpExchange.getRequestURI());
            try {
                if (uriParams.size() > 1) {
                    format = new SimpleDateFormat(uriParams.get(1));
                    if (uriParams.size() > 2) {
                        sendResponse(uriParams.get(2), httpExchange);
                    } else {
                        sendResponse("html", httpExchange);
                    }
                } else {
                    sendResponse("invalid", httpExchange);
                }
            } catch (IllegalArgumentException e) {
                sendResponse("invalid", httpExchange);
            }
        }
    }

    private static class DateTimeHttpHandler implements HttpHandler {
        SimpleDateFormat format;

        DateTimeHttpHandler() {
            format = new SimpleDateFormat();
        }

        DateTimeHttpHandler(@NotNull SimpleDateFormat format) {
            this.format = format;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            ArrayList<String> uriParams = splitTrimRequestURI(httpExchange.getRequestURI());
            if (uriParams.size() > 1) {
                sendResponse(uriParams.get(1), httpExchange);
            } else {
                sendResponse("html", httpExchange);
            }
        }

        ArrayList<String> splitTrimRequestURI(URI uri) {
            String[] uriSplit = uri.toString().trim().split("/");
            ArrayList<String> uriParams = new ArrayList<>();
            for (String s : uriSplit) {
                if (!s.isEmpty()) {
                    uriParams.add(s);
                }
            }
            return uriParams;
        }

        void sendResponse(String responseFormat, HttpExchange httpExchange) {
            String dateString = format.format(new Date());
            if (dateString.isEmpty()) {
                responseFormat = "invalid";
            }
            String response;
            switch (responseFormat) {
                case "json":
                    response = "{ 'datetime' : '" + dateString + "' }";
                    break;

                case "xml":
                    response = "<datetime>" +
                            "<value>" + dateString + "</value>" +
                            "</datetime>";
                    break;

                case "plain":
                    response = dateString;
                    break;

                case "html":
                    response = "<p>" + dateString + "</p>";
                    break;

                default:
                    response = "<p>Invalid Format Request.</p>";
                    break;
            }
            try (OutputStream outputStream = httpExchange.getResponseBody()){
                httpExchange.sendResponseHeaders(200, response.length());
                outputStream.write(response.getBytes());
                outputStream.close();
            } catch (Exception e) {
                System.out.println("Failed to send response: " + e);
            }
        }
    }
}
