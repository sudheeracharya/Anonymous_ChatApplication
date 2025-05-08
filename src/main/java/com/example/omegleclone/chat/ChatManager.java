// File: src/main/java/com/example/omegleclone/chat/ChatManager.java

package com.example.omegleclone.chat;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChatManager {
    private final Map<String, Queue<Long>> waitingUsers = new ConcurrentHashMap<>();
    private final Map<Long, Long> activeChats = new ConcurrentHashMap<>();
    private final Map<Long, Date> lastActivity = new ConcurrentHashMap<>();
    private final Set<Long> onlineUsers = Collections.synchronizedSet(new HashSet<>());
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public ChatManager() {
        waitingUsers.put("default", new LinkedList<>());
        scheduler.scheduleAtFixedRate(this::cleanupInactiveUsers, 5, 5, TimeUnit.MINUTES);
        Runtime.getRuntime().addShutdownHook(new Thread(this::cleanup));
    }

    public void findMatch(Long userId) {
        log.info("Finding match for user: {}", userId);
        updateLastActivity(userId);
        userConnected(userId);
        String interestKey = "default";

        synchronized (waitingUsers) {
            Queue<Long> queue = waitingUsers.get(interestKey);

            // Remove user from any existing chat
            removeFromExistingChat(userId);

            // Remove user from waiting queue if already there
            queue.remove(userId);

            // Try to find a match from the waiting queue
            Long matchedUserId = null;
            Iterator<Long> iterator = queue.iterator();

            while (iterator.hasNext()) {
                Long potentialMatch = iterator.next();
                if (isValidMatch(userId, potentialMatch)) {
                    matchedUserId = potentialMatch;
                    iterator.remove();
                    break;
                }
            }

            if (matchedUserId != null) {
                // Match found - connect the users
                matchUsers(userId, matchedUserId);
                log.info("Matched users {} and {}", userId, matchedUserId);
            } else {
                // No match found - add to waiting queue
                queue.add(userId);
                log.info("Added user {} to waiting queue. Queue size: {}", userId, queue.size());

                // Schedule timeout for finding a match
                scheduler.schedule(() -> {
                    synchronized (waitingUsers) {
                        Queue<Long> currentQueue = waitingUsers.get(interestKey);
                        if (currentQueue != null && currentQueue.contains(userId) && !activeChats.containsKey(userId)) {
                            currentQueue.remove(userId);
                            sendSearchFailedNotification(userId);
                            log.info("Search timeout for user: {}", userId);
                        }
                    }
                }, 30, TimeUnit.SECONDS);
            }
        }
    }

    private void matchUsers(Long user1, Long user2) {
        // Set up active chat for both users
        activeChats.put(user1, user2);
        activeChats.put(user2, user1);

        // Send match notifications to both users
        sendMatchNotification(user1, user2);
        sendMatchNotification(user2, user1);

        log.info("Successfully matched users: {} and {}", user1, user2);
    }

    private void sendMatchNotification(Long recipient, Long partner) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "MATCH");
        message.put("senderId", partner);
        message.put("recipientId", recipient);
        message.put("content", "Connected to a stranger");
        message.put("timestamp", new Date().toString());

        try {
            messagingTemplate.convertAndSendToUser(
                    recipient.toString(),
                    "/queue/messages",
                    message
            );
            log.debug("Sent match notification to user: {}", recipient);
        } catch (Exception e) {
            log.error("Error sending match notification to user: {}", recipient, e);
        }
    }

    private boolean isValidMatch(Long user1, Long user2) {
        return !user1.equals(user2) &&
                !activeChats.containsKey(user1) &&
                !activeChats.containsKey(user2) &&
                isUserActive(user1) &&
                isUserActive(user2) &&
                onlineUsers.contains(user1) &&
                onlineUsers.contains(user2);
    }

    public void userConnected(Long userId) {
        onlineUsers.add(userId);
        updateLastActivity(userId);
        log.info("User {} connected. Total online users: {}", userId, onlineUsers.size());
    }

    public void removeUser(Long userId) {
        updateLastActivity(userId);
        removeFromExistingChat(userId);
        removeFromWaitingQueues(userId);
        onlineUsers.remove(userId);
        log.info("User {} removed. Total online users: {}", userId, onlineUsers.size());
    }

    private void removeFromExistingChat(Long userId) {
        if (activeChats.containsKey(userId)) {
            Long partnerId = activeChats.remove(userId);
            if (partnerId != null) {
                activeChats.remove(partnerId);
                notifyPartnerDisconnection(userId, partnerId);
                log.debug("Removed chat between users: {} and {}", userId, partnerId);
            }
        }
    }

    private void notifyPartnerDisconnection(Long userId, Long partnerId) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "LEAVE");
        message.put("senderId", userId);
        message.put("recipientId", partnerId);
        message.put("content", "Stranger has disconnected");
        message.put("timestamp", new Date().toString());

        try {
            messagingTemplate.convertAndSendToUser(
                    partnerId.toString(),
                    "/queue/messages",
                    message
            );
        } catch (Exception e) {
            log.error("Error notifying partner disconnection: {}", e.getMessage());
        }
    }

    public void forwardMessage(Long senderId, Long recipientId, String content, String type) {
        updateLastActivity(senderId);
        log.info("Forwarding message from {} to {}: {}", senderId, recipientId, content);

        if (isValidChat(senderId, recipientId)) {
            Map<String, Object> message = new HashMap<>();
            message.put("type", type);
            message.put("senderId", senderId);
            message.put("recipientId", recipientId);
            message.put("content", content);
            message.put("timestamp", new Date().toString());

            try {
                messagingTemplate.convertAndSendToUser(
                        recipientId.toString(),
                        "/queue/messages",
                        message
                );
                log.debug("Message forwarded from {} to {}: {}", senderId, recipientId, content);
            } catch (Exception e) {
                log.error("Error forwarding message: {}", e.getMessage());
            }
        } else {
            log.warn("Invalid chat or users not active: {} -> {}", senderId, recipientId);
        }
    }

    public void forwardTypingIndicator(Long senderId, Long recipientId) {
        if (isValidChat(senderId, recipientId)) {
            Map<String, Object> message = new HashMap<>();
            message.put("type", "TYPING");
            message.put("senderId", senderId);
            message.put("recipientId", recipientId);
            message.put("timestamp", new Date().toString());

            try {
                messagingTemplate.convertAndSendToUser(
                        recipientId.toString(),
                        "/queue/typing",
                        message
                );
            } catch (Exception e) {
                log.error("Error forwarding typing indicator: {}", e.getMessage());
            }
        }
    }

    private boolean isValidChat(Long user1, Long user2) {
        return activeChats.containsKey(user1) &&
                activeChats.get(user1).equals(user2) &&
                isUserActive(user1) &&
                isUserActive(user2);
    }

    private void updateLastActivity(Long userId) {
        lastActivity.put(userId, new Date());
    }

    private boolean isUserActive(Long userId) {
        Date lastActiveTime = lastActivity.get(userId);
        if (lastActiveTime == null) return false;

        long inactiveThreshold = 5 * 60 * 1000; // 5 minutes
        return System.currentTimeMillis() - lastActiveTime.getTime() < inactiveThreshold;
    }

    private void cleanupInactiveUsers() {
        List<Long> inactiveUsers = new ArrayList<>();

        lastActivity.forEach((userId, lastActiveTime) -> {
            if (!isUserActive(userId)) {
                inactiveUsers.add(userId);
            }
        });

        inactiveUsers.forEach(this::removeUser);
        log.info("Cleaned up {} inactive users", inactiveUsers.size());
    }

    private void removeFromWaitingQueues(Long userId) {
        waitingUsers.values().forEach(queue -> queue.remove(userId));
    }

    private void sendSearchFailedNotification(Long userId) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "SEARCH_FAILED");
        message.put("content", "Could not find a match");
        message.put("timestamp", new Date().toString());

        try {
            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/messages",
                    message
            );
        } catch (Exception e) {
            log.error("Error sending search failed notification: {}", e.getMessage());
        }
    }

    public void cancelSearch(Long userId) {
        updateLastActivity(userId);
        removeFromWaitingQueues(userId);
        log.info("User {} cancelled search", userId);
    }

    @PreDestroy
    public void cleanup() {
        try {
            // Notify all active users about shutdown
            activeChats.keySet().forEach(userId -> {
                Map<String, Object> message = new HashMap<>();
                message.put("type", "SYSTEM");
                message.put("content", "Server is shutting down. Please reconnect later.");
                messagingTemplate.convertAndSendToUser(
                        userId.toString(),
                        "/queue/messages",
                        message
                );
            });

            // Clear all collections
            activeChats.clear();
            waitingUsers.clear();
            onlineUsers.clear();
            lastActivity.clear();

            // Shutdown scheduler
            scheduler.shutdown();
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }

            log.info("ChatManager cleanup completed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            scheduler.shutdownNow();
            log.error("Error during cleanup: {}", e.getMessage());
        }
    }
}