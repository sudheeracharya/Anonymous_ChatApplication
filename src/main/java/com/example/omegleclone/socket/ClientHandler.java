package com.example.omegleclone.socket;

import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private PrintWriter out;
    private BufferedReader in;
    @Setter
    private ClientHandler pair;

    public ClientHandler(Socket socket) {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                if (pair != null) {
                    pair.out.println(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
