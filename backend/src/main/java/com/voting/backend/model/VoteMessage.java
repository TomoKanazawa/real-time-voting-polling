package com.voting.backend.model;

import java.util.UUID;

public class VoteMessage {

    private UUID messageId;
    private UUID pollId;
    private UUID optionId;
    private UUID userId; // Can be null for anonymous votes
    private long timestamp;
    private String nodeId; // ID of the node that processed this vote
    private boolean processed; // Whether this vote has been processed by the leader

    // Default constructor
    public VoteMessage() {
    }

    // All-args constructor
    public VoteMessage(UUID messageId, UUID pollId, UUID optionId, UUID userId, long timestamp, String nodeId, boolean processed) {
        this.messageId = messageId;
        this.pollId = pollId;
        this.optionId = optionId;
        this.userId = userId;
        this.timestamp = timestamp;
        this.nodeId = nodeId;
        this.processed = processed;
    }

    // Getters and setters
    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public UUID getPollId() {
        return pollId;
    }

    public void setPollId(UUID pollId) {
        this.pollId = pollId;
    }

    public UUID getOptionId() {
        return optionId;
    }

    public void setOptionId(UUID optionId) {
        this.optionId = optionId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public static VoteMessage createVoteMessage(UUID pollId, UUID optionId, UUID userId, String nodeId) {
        return new VoteMessageBuilder()
                .messageId(UUID.randomUUID())
                .pollId(pollId)
                .optionId(optionId)
                .userId(userId)
                .timestamp(System.currentTimeMillis())
                .nodeId(nodeId)
                .processed(false)
                .build();
    }

    // Builder class
    public static class VoteMessageBuilder {
        private UUID messageId;
        private UUID pollId;
        private UUID optionId;
        private UUID userId;
        private long timestamp;
        private String nodeId;
        private boolean processed;

        public VoteMessageBuilder messageId(UUID messageId) {
            this.messageId = messageId;
            return this;
        }

        public VoteMessageBuilder pollId(UUID pollId) {
            this.pollId = pollId;
            return this;
        }

        public VoteMessageBuilder optionId(UUID optionId) {
            this.optionId = optionId;
            return this;
        }

        public VoteMessageBuilder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public VoteMessageBuilder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public VoteMessageBuilder nodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public VoteMessageBuilder processed(boolean processed) {
            this.processed = processed;
            return this;
        }

        public VoteMessage build() {
            return new VoteMessage(messageId, pollId, optionId, userId, timestamp, nodeId, processed);
        }
    }

    public static VoteMessageBuilder builder() {
        return new VoteMessageBuilder();
    }
} 