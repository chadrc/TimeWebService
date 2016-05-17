package com.chadrc.timewebservice;

import com.sun.net.httpserver.HttpExchange;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Chad on 5/17/2016.
 */
class CustomDateTimeHttpHandler extends DateTimeHttpHandler {

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
