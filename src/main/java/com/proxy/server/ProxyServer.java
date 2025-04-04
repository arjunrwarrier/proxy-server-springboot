package com.proxy.server;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

class ProxyServer {
    private static final int SERVER_PORT = 9091;

    public static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Proxy Server started on port " + SERVER_PORT);
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept(); // Accept connection
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                    handleClient(clientSocket);
                } catch (SocketException e) {
                    System.err.println("Client disconnected, waiting for new connection...");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true)
        ) {
            while (true) {
                try {
                    String requestLine = in.readLine();
                    if (requestLine == null) break;

                    System.out.println("Received request: " + requestLine);
                    String[] parts = requestLine.split(" ", 2);
                    if (parts.length < 2) continue;

                    String method = parts[0];
                    String url = parts[1];

                    String response = fetchUrl(url, method, in);
                    out.println(response);
                    out.flush();
                } catch (SocketException e) {
                    System.err.println("Connection reset by client. Closing connection.");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static String fetchUrl(String urlString, String method, BufferedReader in) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("User-Agent", "ProxyServer");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
            return response.toString();
        } catch (Exception e) {
            return "Error fetching URL: " + e.getMessage();
        }
    }
}