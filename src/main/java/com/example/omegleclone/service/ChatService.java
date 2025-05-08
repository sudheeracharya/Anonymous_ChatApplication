// File: src/main/java/com/example/omegleclone/service/ChatService.java

package com.example.omegleclone.service;

import com.example.omegleclone.model.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;
    private final Map<Long, Long> activeChats = new ConcurrentHashMap<>();

    @Autowired
    public ChatService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void handleChatMessage(ChatMessage message) {
        log.info("Processing chat message: {}", message);

        if (message.getType() == ChatMessage.MessageType.CHAT) {
            // Forward the message to the recipient
            messagingTemplate.convertAndSendToUser(
                    message.getRecipientId().toString(),
                    "/queue/messages",
                    message
            );
            log.info("Chat message forwarded to user {}", message.getRecipientId());
        }
    }

    public void createMatch(Long user1Id, Long user2Id) {
        // Store the match in active chats
        activeChats.put(user1Id, user2Id);
        activeChats.put(user2Id, user1Id);

        // Send match notification to both users
        sendMatchNotification(user1Id, user2Id);
        sendMatchNotification(user2Id, user1Id);

        log.info("Match created between users {} and {}", user1Id, user2Id);
    }

    private void sendMatchNotification(Long userId, Long partnerId) {
        ChatMessage matchMessage = new ChatMessage();
        matchMessage.setSenderId(partnerId);
        matchMessage.setRecipientId(userId);
        matchMessage.setType(ChatMessage.MessageType.MATCH);
        matchMessage.setContent("You are now connected with a stranger");
        matchMessage.setTimestamp(String.valueOf(System.currentTimeMillis()));

        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/messages",
                matchMessage
        );
    }

    public Long getPartnerIdFor(Long userId) {
        return activeChats.get(userId);
    }

    public void endChat(Long userId) {
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

            messagingTemplate.convertAndSendToUser(
                    partnerId.toString(),
                    "/queue/messages",
                    leaveMessage
            );

            log.info("Chat ended between users {} and {}", userId, partnerId);
        }
    }
}