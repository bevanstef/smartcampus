package com.smartcampus;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    // This defines where your server will run
    public static final String BASE_URI = "http://localhost:8080/api/v1/";

    public static HttpServer startServer() {
        // Tells the server to scan your project for all your endpoints and exception
        // mappers
        final ResourceConfig rc = new ResourceConfig().packages("com.smartcampus");

        // Creates and starts the server
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) {
        try {
            final HttpServer server = startServer();
            System.out.println("Smart Campus API started successfully!");
            System.out.println("Endpoints are available at: " + BASE_URI);
            System.out.println("Press Ctrl+C in the terminal to stop the server...");

            // Keeps the server running indefinitely
            Thread.currentThread().join();
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}