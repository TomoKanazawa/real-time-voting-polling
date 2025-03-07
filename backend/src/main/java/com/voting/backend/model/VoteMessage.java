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
public class VoteMessage {

    private UUID messageId;
    private UUID pollId;
    private UUID optionId;
    private UUID userId; // Can be null for anonymous votes
    private long timestamp;
    private String nodeId; // ID of the node that processed this vote
    private boolean processed; // Whether this vote has been processed by the leader

    public static VoteMessage createVoteMessage(UUID pollId, UUID optionId, UUID userId, String nodeId) {
        return VoteMessage.builder()
                .messageId(UUID.randomUUID())
                .pollId(pollId)
                .optionId(optionId)
                .userId(userId)
                .timestamp(System.currentTimeMillis())
                .nodeId(nodeId)
                .processed(false)
                .build();
    }
} 