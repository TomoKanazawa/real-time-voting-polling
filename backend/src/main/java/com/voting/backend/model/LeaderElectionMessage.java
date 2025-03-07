package com.voting.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    public static LeaderElectionMessage createElectionMessage(NodeInfo sender) {
        return LeaderElectionMessage.builder()
                .messageId(UUID.randomUUID())
                .type(MessageType.ELECTION)
                .sender(sender)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static LeaderElectionMessage createVictoryMessage(NodeInfo sender) {
        return LeaderElectionMessage.builder()
                .messageId(UUID.randomUUID())
                .type(MessageType.VICTORY)
                .sender(sender)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static LeaderElectionMessage createHeartbeatMessage(NodeInfo sender) {
        return LeaderElectionMessage.builder()
                .messageId(UUID.randomUUID())
                .type(MessageType.HEARTBEAT)
                .sender(sender)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static LeaderElectionMessage createJoinMessage(NodeInfo sender) {
        return LeaderElectionMessage.builder()
                .messageId(UUID.randomUUID())
                .type(MessageType.JOIN)
                .sender(sender)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static LeaderElectionMessage createLeaveMessage(NodeInfo sender) {
        return LeaderElectionMessage.builder()
                .messageId(UUID.randomUUID())
                .type(MessageType.LEAVE)
                .sender(sender)
                .timestamp(System.currentTimeMillis())
                .build();
    }
} 