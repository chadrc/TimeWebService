package com.chadrc.timewebservice;

import com.sun.istack.internal.NotNull;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
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
            server.createContext("/time", new TimeHttpHandler(new SimpleDateFormat("HH:mm:ss z")));
            server.createContext("/date", new TimeHttpHandler(new SimpleDateFormat("E M-d-y")));
            server.createContext("/datetime", new TimeHttpHandler(new SimpleDateFormat("E M-d-y HH:mm:ss z")));
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

    private static class TimeHttpHandler implements HttpHandler {
        SimpleDateFormat format;

        TimeHttpHandler(@NotNull SimpleDateFormat format) {
            this.format = format;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String dateString = format.format(new Date());
            String response;
            String[] uriSplit = httpExchange.getRequestURI().toString().trim().split("/");
            ArrayList<String> uriParams = new ArrayList<>();
            for (String s :
                    uriSplit) {
                if (!s.isEmpty()) {
                    uriParams.add(s);
                }
            }
            if (uriParams.size() > 1) {
                switch (uriParams.get(1)) {
                    case "json":
                        response = "{ 'time' : '" + dateString + "' }";
                        break;

                    case "xml":
                        response = "<time>" +
                                "<value>" + dateString + "</value>" +
                                "</time>";
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
            } else {
                response = "<p>" + dateString + "</p>";
            }
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream outputStream = httpExchange.getResponseBody();
            outputStream.write(response.getBytes());
            outputStream.close();
        }
    }
}
