package org.Meme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting server...");

        try {
            ServerSocket serverSocket = new ServerSocket(4221);
            serverSocket.setReuseAddress(true);

            Socket s = serverSocket.accept();
            System.out.println("Accepted new connection");
            BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            StringBuilder requestBuilder = new StringBuilder();
            String line;
            while (!(line = reader.readLine()).isEmpty()) {
                requestBuilder.append(line).append("\r\n");
            }

            String request = requestBuilder.toString();
            System.out.println("Request:\n" + request);

            String path = parseRequest(request);
            OutputStream output = s.getOutputStream();


            if (path.equals("/")) {
                output.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
            } else if (path.equals("/hello")) {
                String htmlFile = readFile("/Users/yogeshshekhawat/codecrafters-http-server-java/src/main/java/index.html");
                String response = "HTTP/1.1 200 OK\r\n"
                        +"Content-Type: text/html\r\n Content-Length: " + htmlFile.length() + "\r\n\r\n" + htmlFile;
                output.write(response.getBytes());
            } else {
                output.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
            }
            output.flush();

            s.close();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    public static String parseRequest(String request) {
        String[] lines = request.split("\r\n");
        String firstLine = lines[0];
        String[] parts = firstLine.split(" ");
        if (parts.length < 2) return "/";
        return parts[1]; // path
    }


    public static String  readFile(String path){
        try {
            Path filePath = Paths.get(path);
            return Files.readAllLines(filePath).stream().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            return "";
        }
    }
}
