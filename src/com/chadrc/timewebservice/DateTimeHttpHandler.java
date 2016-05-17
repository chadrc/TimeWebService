package com.chadrc.timewebservice;

import com.sun.istack.internal.NotNull;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Chad on 5/17/2016.
 */
class DateTimeHttpHandler implements HttpHandler {
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
        try (OutputStream outputStream = httpExchange.getResponseBody()) {
            httpExchange.sendResponseHeaders(200, response.length());
            outputStream.write(response.getBytes());
            outputStream.close();
        } catch (Exception e) {
            System.out.println("Failed to send response: " + e);
        }
    }
}
