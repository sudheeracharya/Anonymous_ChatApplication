//// File: src/main/java/com/example/omegleclone/controller/ChatController.java
//
//package com.example.omegleclone.controller;
//
//import com.example.omegleclone.model.ChatMessage;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Controller;
//
//import java.util.Map;
//import java.util.Queue;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentLinkedQueue;
//
//@Controller
//@Slf4j
//public class ChatController {
//
//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;
//
//    private final Queue<Long> waitingUsers = new ConcurrentLinkedQueue<>();
//    private final Map<Long, Long> activeChats = new ConcurrentHashMap<>();
//
//    @MessageMapping("/chat.sendMessage")
//    public void sendMessage(@Payload ChatMessage chatMessage) {
//        log.info("Received message: {}", chatMessage);
//
//        // Ensure the message type is set to CHAT
//        chatMessage.setType(ChatMessage.MessageType.CHAT);
//
//        // Forward the message to the recipient
//        messagingTemplate.convertAndSendToUser(
//                chatMessage.getRecipientId().toString(),
//                "/queue/messages",
//                chatMessage
//        );
//
//        log.info("Message forwarded from {} to {}: {}",
//                chatMessage.getSenderId(),
//                chatMessage.getRecipientId(),
//                chatMessage.getContent());
//    }
//
//    @MessageMapping("/chat.typing")
//    public void typing(@Payload ChatMessage chatMessage) {
//        log.info("Typing indicator from: {}", chatMessage.getSenderId());
//
//        // Ensure the message type is set to TYPING
//        chatMessage.setType(ChatMessage.MessageType.TYPING);
//
//        messagingTemplate.convertAndSendToUser(
//                chatMessage.getRecipientId().toString(),
//                "/queue/messages",
//                chatMessage
//        );
//    }
//
//    @MessageMapping("/chat.findMatch")
//    public void findMatch(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
//        log.info("Finding match for user: {}", chatMessage.getSenderId());
//
//        Long userId = chatMessage.getSenderId();
//        headerAccessor.getSessionAttributes().put("userId", userId);
//
//        // Set the message type to SEARCH
//        chatMessage.setType(ChatMessage.MessageType.SEARCH);
//
//        // Check if user is already in a chat
//        if (activeChats.containsKey(userId)) {
//            Long partnerId = activeChats.get(userId);
//            log.info("User {} is already in a chat with {}", userId, partnerId);
//
//            // Send a reminder match notification
//            sendMatchNotification(userId, partnerId);
//            return;
//        }
//
//        // Remove from any existing chat
//        endExistingChat(userId);
//
//        // Check if there's already a waiting user
//        Long matchedUser = waitingUsers.poll();
//
//        if (matchedUser != null && !matchedUser.equals(userId)) {
//            // Match found - connect the users
//            activeChats.put(userId, matchedUser);
//            activeChats.put(matchedUser, userId);
//
//            // Send match notification to both users
//            sendMatchNotification(userId, matchedUser);
//            sendMatchNotification(matchedUser, userId);
//
//            log.info("Matched users {} and {}", userId, matchedUser);
//        } else {
//            // No match found - add to waiting queue
//            waitingUsers.add(userId);
//            log.info("User {} added to waiting queue", userId);
//
//            // Send a system message that we're searching
//            ChatMessage systemMessage = new ChatMessage();
//            systemMessage.setSenderId(0L); // System sender ID
//            systemMessage.setRecipientId(userId);
//            systemMessage.setType(ChatMessage.MessageType.SYSTEM);
//            systemMessage.setContent("Looking for a match...");
//
//            messagingTemplate.convertAndSendToUser(
//                    userId.toString(),
//                    "/queue/messages",
//                    systemMessage
//            );
//        }
//    }
//
//    @MessageMapping("/chat.leave")
//    public void leave(@Payload ChatMessage chatMessage) {
//        log.info("User leaving chat: {}", chatMessage.getSenderId());
//
//        // Ensure the message type is set to LEAVE
//        chatMessage.setType(ChatMessage.MessageType.LEAVE);
//
//        endExistingChat(chatMessage.getSenderId());
//    }
//
//    @MessageMapping("/chat.cancelSearch")
//    public void cancelSearch(@Payload ChatMessage chatMessage) {
//        log.info("User cancelling search: {}", chatMessage.getSenderId());
//
//        // Ensure the message type is set to CANCEL
//        chatMessage.setType(ChatMessage.MessageType.CANCEL);
//
//        waitingUsers.remove(chatMessage.getSenderId());
//
//        // Send a system message that search was cancelled
//        ChatMessage systemMessage = new ChatMessage();
//        systemMessage.setSenderId(0L); // System sender ID
//        systemMessage.setRecipientId(chatMessage.getSenderId());
//        systemMessage.setType(ChatMessage.MessageType.SYSTEM);
//        systemMessage.setContent("Search cancelled");
//
//        messagingTemplate.convertAndSendToUser(
//                chatMessage.getSenderId().toString(),
//                "/queue/messages",
//                systemMessage
//        );
//    }
//
//    @MessageMapping("/chat.heartbeat")
//    public void heartbeat(@Payload ChatMessage chatMessage) {
//        // Ensure the message type is set to HEARTBEAT
//        chatMessage.setType(ChatMessage.MessageType.HEARTBEAT);
//
//        // Just log the heartbeat
//        log.debug("Heartbeat from user: {}", chatMessage.getSenderId());
//    }
//
//    @MessageMapping("/chat.join")
//    public void join(@Payload ChatMessage chatMessage) {
//        log.info("User joining: {}", chatMessage.getSenderId());
//
//        // Ensure the message type is set to JOIN
//        chatMessage.setType(ChatMessage.MessageType.JOIN);
//
//        // Send a system message that user joined
//        ChatMessage systemMessage = new ChatMessage();
//        systemMessage.setSenderId(0L); // System sender ID
//        systemMessage.setRecipientId(chatMessage.getSenderId());
//        systemMessage.setType(ChatMessage.MessageType.SYSTEM);
//        systemMessage.setContent("Welcome to the chat!");
//
//        messagingTemplate.convertAndSendToUser(
//                chatMessage.getSenderId().toString(),
//                "/queue/messages",
//                systemMessage
//        );
//    }
//
//    private void endExistingChat(Long userId) {
//        Long partnerId = activeChats.remove(userId);
//
//        if (partnerId != null) {
//            activeChats.remove(partnerId);
//
//            // Notify the partner that the chat has ended
//            ChatMessage leaveMessage = new ChatMessage();
//            leaveMessage.setSenderId(userId);
//            leaveMessage.setRecipientId(partnerId);
//            leaveMessage.setType(ChatMessage.MessageType.LEAVE);
//            leaveMessage.setContent("Stranger has disconnected");
//            leaveMessage.setTimestamp(String.valueOf(System.currentTimeMillis()));
//
//            messagingTemplate.convertAndSendToUser(
//                    partnerId.toString(),
//                    "/queue/messages",
//                    leaveMessage
//            );
//
//            log.info("Chat ended between users {} and {}", userId, partnerId);
//        }
//    }
//
//    private void sendMatchNotification(Long userId, Long partnerId) {
//        ChatMessage matchMessage = new ChatMessage();
//        matchMessage.setSenderId(partnerId);
//        matchMessage.setRecipientId(userId);
//        matchMessage.setType(ChatMessage.MessageType.MATCH);
//        matchMessage.setContent("You are now connected with a stranger");
//        matchMessage.setTimestamp(String.valueOf(System.currentTimeMillis()));
//
//        // Send the match notification directly to the user's queue
//        messagingTemplate.convertAndSendToUser(
//                userId.toString(),
//                "/queue/messages",
//                matchMessage
//        );
//
//        log.info("Match notification sent to user {}", userId);
//    }
//
//    private void sendSearchFailedNotification(Long userId) {
//        ChatMessage failedMessage = new ChatMessage();
//        failedMessage.setSenderId(0L); // System sender ID
//        failedMessage.setRecipientId(userId);
//        failedMessage.setType(ChatMessage.MessageType.SEARCH_FAILED);
//        failedMessage.setContent("Could not find a match at this time. Please try again later.");
//        failedMessage.setTimestamp(String.valueOf(System.currentTimeMillis()));
//
//        messagingTemplate.convertAndSendToUser(
//                userId.toString(),
//                "/queue/messages",
//                failedMessage
//        );
//
//        log.info("Search failed notification sent to user {}", userId);
//    }
//}

package com.example.omegleclone.controller;

import com.example.omegleclone.model.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Controller
@Slf4j
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final Queue<Long> waitingUsers = new ConcurrentLinkedQueue<>();
    private final Map<Long, Long> activeChats = new ConcurrentHashMap<>();

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        log.info("Received message: {}", chatMessage);

        // Ensure the message type is set to CHAT
        chatMessage.setType(ChatMessage.MessageType.CHAT);

        // Forward the message to the recipient
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId().toString(),
                "/queue/messages",
                chatMessage
        );

        log.info("Message forwarded from {} to {}: {}",
                chatMessage.getSenderId(),
                chatMessage.getRecipientId(),
                chatMessage.getContent());
    }

    @MessageMapping("/chat.typing")
    public void typing(@Payload ChatMessage chatMessage) {
        log.info("Typing indicator from: {}", chatMessage.getSenderId());

        // Ensure the message type is set to TYPING
        chatMessage.setType(ChatMessage.MessageType.TYPING);

        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId().toString(),
                "/queue/messages",
                chatMessage
        );
    }

    @MessageMapping("/chat.findMatch")
    public void findMatch(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        log.info("Finding match for user: {}", chatMessage.getSenderId());

        Long userId = chatMessage.getSenderId();
        headerAccessor.getSessionAttributes().put("userId", userId);

        // Set the message type to SEARCH
        chatMessage.setType(ChatMessage.MessageType.SEARCH);

        // Check if user is already in a chat
        if (activeChats.containsKey(userId)) {
            Long partnerId = activeChats.get(userId);
            log.info("User {} is already in a chat with {}", userId, partnerId);

            // Send a reminder match notification
            sendMatchNotification(userId, partnerId);
            return;
        }

        // Remove from any existing chat
        endExistingChat(userId);

        // Check if there's already a waiting user
        Long matchedUser = waitingUsers.poll();

        if (matchedUser != null && !matchedUser.equals(userId)) {
            // Match found - connect the users
            activeChats.put(userId, matchedUser);
            activeChats.put(matchedUser, userId);

            // Send match notification to both users
            try {
                sendMatchNotification(userId, matchedUser);
                log.info("Match notification sent to user {}", userId);
            } catch (Exception e) {
                log.error("Error sending match notification to user {}: {}", userId, e.getMessage());
            }

            try {
                sendMatchNotification(matchedUser, userId);
                log.info("Match notification sent to user {}", matchedUser);
            } catch (Exception e) {
                log.error("Error sending match notification to user {}: {}", matchedUser, e.getMessage());
            }

            log.info("Matched users {} and {}", userId, matchedUser);
        } else {
            // No match found - add to waiting queue
            waitingUsers.add(userId);
            log.info("User {} added to waiting queue", userId);

            // Send a system message that we're searching
            ChatMessage systemMessage = new ChatMessage();
            systemMessage.setSenderId(0L); // System sender ID
            systemMessage.setRecipientId(userId);
            systemMessage.setType(ChatMessage.MessageType.SYSTEM);
            systemMessage.setContent("Looking for a match...");

            try {
                messagingTemplate.convertAndSendToUser(
                        userId.toString(),
                        "/queue/messages",
                        systemMessage
                );
            } catch (Exception e) {
                log.error("Error sending system message to user {}: {}", userId, e.getMessage());
            }
        }
    }

    @MessageMapping("/chat.leave")
    public void leave(@Payload ChatMessage chatMessage) {
        log.info("User leaving chat: {}", chatMessage.getSenderId());

        // Ensure the message type is set to LEAVE
        chatMessage.setType(ChatMessage.MessageType.LEAVE);

        endExistingChat(chatMessage.getSenderId());
    }

    @MessageMapping("/chat.cancelSearch")
    public void cancelSearch(@Payload ChatMessage chatMessage) {
        log.info("User cancelling search: {}", chatMessage.getSenderId());

        // Ensure the message type is set to CANCEL
        chatMessage.setType(ChatMessage.MessageType.CANCEL);

        waitingUsers.remove(chatMessage.getSenderId());

        // Send a system message that search was cancelled
        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setSenderId(0L); // System sender ID
        systemMessage.setRecipientId(chatMessage.getSenderId());
        systemMessage.setType(ChatMessage.MessageType.SYSTEM);
        systemMessage.setContent("Search cancelled");

        messagingTemplate.convertAndSendToUser(
                chatMessage.getSenderId().toString(),
                "/queue/messages",
                systemMessage
        );
    }

    @MessageMapping("/chat.heartbeat")
    public void heartbeat(@Payload ChatMessage chatMessage) {
        // Ensure the message type is set to HEARTBEAT
        chatMessage.setType(ChatMessage.MessageType.HEARTBEAT);

        // Just log the heartbeat
        log.debug("Heartbeat from user: {}", chatMessage.getSenderId());
    }

    @MessageMapping("/chat.join")
    public void join(@Payload ChatMessage chatMessage) {
        log.info("User joining: {}", chatMessage.getSenderId());

        // Ensure the message type is set to JOIN
        chatMessage.setType(ChatMessage.MessageType.JOIN);

        // Send a system message that user joined
        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setSenderId(0L); // System sender ID
        systemMessage.setRecipientId(chatMessage.getSenderId());
        systemMessage.setType(ChatMessage.MessageType.SYSTEM);
        systemMessage.setContent("Welcome to the chat!");

        messagingTemplate.convertAndSendToUser(
                chatMessage.getSenderId().toString(),
                "/queue/messages",
                systemMessage
        );
    }

    private void endExistingChat(Long userId) {
        Long partnerId = activeChats.remove(userId);

        if (partnerId != null) {
            activeChats.remove(partnerId);

            // Notify the partner that the chat has ended
            ChatMessage leaveMessage = new ChatMessage();
            leaveMessage.setSenderId(userId);
            leaveMessage.setRecipientId(partnerId);
            leaveMessage.setType(ChatMessage.MessageType.LEAVE);
            leaveMessage.setContent("Stranger has disconnected");
            leaveMessage.setTimestamp(String.valueOf(System.currentTimeMillis()));

            try {
                messagingTemplate.convertAndSendToUser(
                        partnerId.toString(),
                        "/queue/messages",
                        leaveMessage
                );
                log.info("Leave notification sent to user {}", partnerId);
            } catch (Exception e) {
                log.error("Error sending leave notification to user {}: {}", partnerId, e.getMessage());
            }

            log.info("Chat ended between users {} and {}", userId, partnerId);
        }
    }

    private void sendMatchNotification(Long userId, Long partnerId) {
        ChatMessage matchMessage = new ChatMessage();
        matchMessage.setSenderId(partnerId);
        matchMessage.setRecipientId(userId);
        matchMessage.setType(ChatMessage.MessageType.MATCH);
        matchMessage.setContent("You are now connected with a stranger");
        matchMessage.setTimestamp(String.valueOf(System.currentTimeMillis()));

        // Send the match notification directly to the user's queue
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/messages",
                matchMessage
        );

        log.info("Match notification sent to user {}", userId);
    }

    private void sendSearchFailedNotification(Long userId) {
        ChatMessage failedMessage = new ChatMessage();
        failedMessage.setSenderId(0L); // System sender ID
        failedMessage.setRecipientId(userId);
        failedMessage.setType(ChatMessage.MessageType.SEARCH_FAILED);
        failedMessage.setContent("Could not find a match at this time. Please try again later.");
        failedMessage.setTimestamp(String.valueOf(System.currentTimeMillis()));

        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/messages",
                failedMessage
        );

        log.info("Search failed notification sent to user {}", userId);
    }

    // Method to handle user disconnection from WebSocket event listener
    public void handleUserDisconnect(Long userId) {
        log.info("Handling disconnect for user: {}", userId);
        endExistingChat(userId);
        waitingUsers.remove(userId);
        log.info("User {} has been removed from all chats and queues", userId);
    }
}

