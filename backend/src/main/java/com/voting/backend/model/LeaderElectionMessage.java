package com.voting.backend.model;

import java.util.UUID;

public class LeaderElectionMessage {

    public enum MessageType {
        ELECTION, // Start an election
        VICTORY,  // Declare victory in an election
        HEARTBEAT, // Regular heartbeat from leader
        JOIN,     // New node joining the cluster
        LEAVE     // Node leaving the cluster
    }

    private UUID messageId;
    private MessageType type;
    private NodeInfo sender;
    private long timestamp;

    // Default constructor
    public LeaderElectionMessage() {
    }

    // All-args constructor
    public LeaderElectionMessage(UUID messageId, MessageType type, NodeInfo sender, long timestamp) {
        this.messageId = messageId;
        this.type = type;
        this.sender = sender;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public NodeInfo getSender() {
        return sender;
    }

    public void setSender(NodeInfo sender) {
        this.sender = sender;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // Static factory methods
    public static LeaderElectionMessage createElectionMessage(NodeInfo sender) {
        return new LeaderElectionMessageBuilder()
                .messageId(UUID.randomUUID())
                .type(MessageType.ELECTION)
                .sender(sender)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static LeaderElectionMessage createVictoryMessage(NodeInfo sender) {
        return new LeaderElectionMessageBuilder()
                .messageId(UUID.randomUUID())
                .type(MessageType.VICTORY)
                .sender(sender)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static LeaderElectionMessage createHeartbeatMessage(NodeInfo sender) {
        return new LeaderElectionMessageBuilder()
                .messageId(UUID.randomUUID())
                .type(MessageType.HEARTBEAT)
                .sender(sender)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static LeaderElectionMessage createJoinMessage(NodeInfo sender) {
        return new LeaderElectionMessageBuilder()
                .messageId(UUID.randomUUID())
                .type(MessageType.JOIN)
                .sender(sender)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static LeaderElectionMessage createLeaveMessage(NodeInfo sender) {
        return new LeaderElectionMessageBuilder()
                .messageId(UUID.randomUUID())
                .type(MessageType.LEAVE)
                .sender(sender)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    // Builder class
    public static class LeaderElectionMessageBuilder {
        private UUID messageId;
        private MessageType type;
        private NodeInfo sender;
        private long timestamp;

        public LeaderElectionMessageBuilder messageId(UUID messageId) {
            this.messageId = messageId;
            return this;
        }

        public LeaderElectionMessageBuilder type(MessageType type) {
            this.type = type;
            return this;
        }

        public LeaderElectionMessageBuilder sender(NodeInfo sender) {
            this.sender = sender;
            return this;
        }

        public LeaderElectionMessageBuilder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public LeaderElectionMessage build() {
            return new LeaderElectionMessage(messageId, type, sender, timestamp);
        }
    }

    public static LeaderElectionMessageBuilder builder() {
        return new LeaderElectionMessageBuilder();
    }
} 