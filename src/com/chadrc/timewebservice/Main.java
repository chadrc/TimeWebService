package com.chadrc.timewebservice;

import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;

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
}
