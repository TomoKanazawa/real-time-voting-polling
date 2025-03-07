package com.voting.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NodeInfo {

    private UUID nodeId;
    private String hostname;
    private int port;
    private boolean isLeader;
    private LocalDateTime lastHeartbeat;
    private long priority; // Used for leader election

    public static NodeInfo createNode(String hostname, int port) {
        return NodeInfo.builder()
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
} 