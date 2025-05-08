package com.example.omegleclone.socket;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final Queue<ClientHandler> waitingUsers = new LinkedList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Chat server started...");

            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    ClientHandler client = new ClientHandler(socket);

                    synchronized (waitingUsers) {
                        if (waitingUsers.isEmpty()) {
                            waitingUsers.add(client);
                            new Thread(client).start();
                            System.out.println("User added to queue, waiting for a pair...");
                        } else {
                            ClientHandler pair = waitingUsers.poll();

                            if (pair != null) {
                                client.setPair(pair);
                                pair.setPair(client);

                                new Thread(client).start();
                                new Thread(pair).start();
                                System.out.println("Two users paired!");
                            } else {
                                System.out.println("Pairing failed, re-adding user to queue.");
                                waitingUsers.add(client);
                            }
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error accepting connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Server socket failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
