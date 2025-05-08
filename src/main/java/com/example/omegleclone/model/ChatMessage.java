// File: src/main/java/com/example/omegleclone/model/ChatMessage.java

package com.example.omegleclone.model;

public class ChatMessage {

    private Long senderId;
    private Long recipientId;
    private String content;
    private String timestamp;
    private MessageType type;

    public ChatMessage() {
        this.timestamp = String.valueOf(System.currentTimeMillis());
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE,
        TYPING,
        SEARCH,
        CANCEL,
        SYSTEM,
        HEARTBEAT,
        MATCH,
        SEARCH_FAILED
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "senderId=" + senderId +
                ", recipientId=" + recipientId +
                ", content='" + content + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", type=" + type +
                '}';
    }
}