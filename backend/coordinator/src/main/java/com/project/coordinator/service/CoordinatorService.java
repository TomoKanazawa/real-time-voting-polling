package com.project.coordinator.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class CoordinatorService {

    private final RestTemplate restTemplate;
    private List<String> brokers = new ArrayList<>();
    private Map<String, Long> lastHeartbeats = new HashMap<>();
    private String leader;
    private long logicalClock = 0;

    public CoordinatorService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<String> getBrokers() {
        incrementClock();
        return brokers;
    }

    public String getLeader() {
        incrementClock();
        return leader;
    }

    public void registerBroker(String brokerUrl) {
        incrementClock();
        if (!brokers.contains(brokerUrl)) {
            brokers.add(brokerUrl);
            lastHeartbeats.put(brokerUrl, System.currentTimeMillis());
            System.out.println("Registered broker: " + brokerUrl);
            if (leader == null) {
                leader = brokerUrl;
                System.out.println("Set leader to: " + leader);
            }
        }
    }

    public void heartbeat(String brokerUrl) {
        incrementClock();
        lastHeartbeats.put(brokerUrl, System.currentTimeMillis());
        if (!brokers.contains(brokerUrl)) {
            brokers.add(brokerUrl);
            System.out.println("Added broker from heartbeat: " + brokerUrl);
            if (leader == null) {
                leader = brokerUrl;
                System.out.println("Set leader to: " + leader);
            }
        }
    }

    @Scheduled(fixedRate = 5000)
    public void checkBrokerStatus() {
        incrementClock();
        long now = System.currentTimeMillis();
        List<String> deadBrokers = new ArrayList<>();

        for (Map.Entry<String, Long> entry : lastHeartbeats.entrySet()) {
            if (now - entry.getValue() > 10000) {  // 10 seconds timeout
                deadBrokers.add(entry.getKey());
            }
        }

        for (String deadBroker : deadBrokers) {
            brokers.remove(deadBroker);
            lastHeartbeats.remove(deadBroker);
            System.out.println("Removed dead broker: " + deadBroker);
        }

        if (leader != null && deadBrokers.contains(leader)) {
            electNewLeader();
        }
    }

    private void electNewLeader() {
        incrementClock();
        if (brokers.isEmpty()) {
            leader = null;
            System.out.println("No brokers available, leader set to null");
        } else {
            leader = brokers.get(0);  // Simple leader election: choose the first available broker
            System.out.println("Elected new leader: " + leader);
        }
    }

    private synchronized void incrementClock() {
        logicalClock++;
    }

    public synchronized void updateClock(long receivedTimestamp) {
        logicalClock = Math.max(logicalClock, receivedTimestamp) + 1;
    }

    public synchronized long getLogicalClock() {
        return logicalClock;
    }
} 