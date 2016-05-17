package com.chadrc.timewebservice;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Date;

public class Main {

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
            server.createContext("/time", new TimeHttpHandler());
            server.setExecutor(null);
            server.start();
        } catch(UnknownHostException e) {
            System.out.println("Could not create server. Failed to get local host.");
        } catch(IOException e) {
            System.out.println("Could not create server. Failed to connect to socket 8000.");
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
