package com.example.omegleclone.chat;

import lombok.Getter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatRoom<T> implements Runnable {
    private final BlockingQueue<T> messages = new LinkedBlockingQueue<>();

    @Getter
    private final String roomId;

    private final AtomicBoolean running = new AtomicBoolean(true);

    public ChatRoom(String roomId) {
        this.roomId = roomId;
    }

    public void addMessage(T message) {
        if (running.get()) {
            messages.offer(message);
        }
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                T message = messages.poll();
                if (message != null) {
                    processMessage(message);
                }
                Thread.sleep(100); // Small delay to prevent CPU spinning
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                stop();
                break;
            }
        }
    }

    private void processMessage(T message) {
        if (message != null) {
            System.out.println("Processing message in room " + roomId + ": " + message);
        }
    }

    public void stop() {
        running.set(false);
        messages.clear();
    }
}