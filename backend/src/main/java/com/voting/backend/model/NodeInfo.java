package com.voting.backend.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class NodeInfo {

    private UUID nodeId;
    private String hostname;
    private int port;
    private boolean isLeader;
    private LocalDateTime lastHeartbeat;
    private long priority; // Used for leader election

    // Default constructor
    public NodeInfo() {
    }

    // All-args constructor
    public NodeInfo(UUID nodeId, String hostname, int port, boolean isLeader, LocalDateTime lastHeartbeat, long priority) {
        this.nodeId = nodeId;
        this.hostname = hostname;
        this.port = port;
        this.isLeader = isLeader;
        this.lastHeartbeat = lastHeartbeat;
        this.priority = priority;
    }

    // Getters and setters
    public UUID getNodeId() {
        return nodeId;
    }

    public void setNodeId(UUID nodeId) {
        this.nodeId = nodeId;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public void setLeader(boolean leader) {
        isLeader = leader;
    }

    public LocalDateTime getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(LocalDateTime lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    public long getPriority() {
        return priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }

    public static NodeInfo createNode(String hostname, int port) {
        return new NodeInfoBuilder()
                .nodeId(UUID.randomUUID())
                .hostname(hostname)
                .port(port)
                .isLeader(false)
                .lastHeartbeat(LocalDateTime.now())
                .priority(System.currentTimeMillis()) // Use timestamp as priority
                .build();
    }

    public void updateHeartbeat() {
        this.lastHeartbeat = LocalDateTime.now();
    }

    // Builder class
    public static class NodeInfoBuilder {
        private UUID nodeId;
        private String hostname;
        private int port;
        private boolean isLeader;
        private LocalDateTime lastHeartbeat;
        private long priority;

        public NodeInfoBuilder nodeId(UUID nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public NodeInfoBuilder hostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        public NodeInfoBuilder port(int port) {
            this.port = port;
            return this;
        }

        public NodeInfoBuilder isLeader(boolean isLeader) {
            this.isLeader = isLeader;
            return this;
        }

        public NodeInfoBuilder lastHeartbeat(LocalDateTime lastHeartbeat) {
            this.lastHeartbeat = lastHeartbeat;
            return this;
        }

        public NodeInfoBuilder priority(long priority) {
            this.priority = priority;
            return this;
        }

        public NodeInfo build() {
            return new NodeInfo(nodeId, hostname, port, isLeader, lastHeartbeat, priority);
        }
    }

    public static NodeInfoBuilder builder() {
        return new NodeInfoBuilder();
    }
} 