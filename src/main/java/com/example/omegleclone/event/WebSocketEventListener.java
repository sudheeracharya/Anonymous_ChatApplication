//// File: src/main/java/com/example/omegleclone/event/WebSocketEventListener.java
//
//package com.example.omegleclone.event;
//
//import com.example.omegleclone.model.ChatMessage;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.event.EventListener;
//import org.springframework.messaging.simp.SimpMessageSendingOperations;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.messaging.SessionConnectedEvent;
//import org.springframework.web.socket.messaging.SessionDisconnectEvent;
//
//@Component
//@Slf4j
//public class WebSocketEventListener {
//
//    @Autowired
//    private SimpMessageSendingOperations messagingTemplate;
//
//    @EventListener
//    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
//        log.info("Received a new web socket connection");
//    }
//
//    @EventListener
//    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//
//        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
//        if (userId != null) {
//            log.info("User disconnected: {}", userId);
//
//            ChatMessage chatMessage = new ChatMessage();
//            chatMessage.setType(ChatMessage.MessageType.LEAVE);
//            chatMessage.setSenderId(userId);
//
//            messagingTemplate.convertAndSend("/topic/public", chatMessage);
//        }
//    }
//}

// File: src/main/java/com/example/omegleclone/event/WebSocketEventListener.java

package com.example.omegleclone.event;

import com.example.omegleclone.controller.ChatController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
public class WebSocketEventListener {

    @Autowired
    private ChatController chatController;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
        if (userId != null) {
            log.info("User disconnected: {}", userId);

            // Handle the user disconnect
            chatController.handleUserDisconnect(userId);
        }
    }
}

