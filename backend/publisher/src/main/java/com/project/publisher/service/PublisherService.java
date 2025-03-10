package com.project.publisher.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class PublisherService {

    private final RestTemplate restTemplate;

    @Value("${coordinator.url}")
    private String coordinatorUrl;

    @Value("${server.port}")
    private int port;

    private String leaderBroker;
    private List<String> topics = new ArrayList<>();
    private long logicalClock = 0;

    public PublisherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        updateLeaderBroker();
    }

    @Scheduled(fixedRate = 5000)
    public void updateLeaderBroker() {
        incrementClock();
        try {
            this.leaderBroker = restTemplate.getForObject(coordinatorUrl + "/api/leader?timestamp=" + logicalClock, String.class);
            System.out.println("Updated leader broker: " + leaderBroker);
            // Sync topics after updating leader broker
            syncTopics();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // New method to sync topics with the broker
    @Scheduled(fixedRate = 3000)
    public void syncTopics() {
        incrementClock();
        if (leaderBroker != null) {
            try {
                List<String> brokerTopics = restTemplate.getForObject(leaderBroker + "/api/topics?timestamp=" + logicalClock, List.class);
                if (brokerTopics != null) {
                    this.topics = brokerTopics;
                    System.out.println("Synced topics from broker: " + topics);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void createTopic(String topic) {
        incrementClock();
        if (leaderBroker != null) {
            try {
                restTemplate.postForObject(leaderBroker + "/api/add-topic?timestamp=" + logicalClock, topic, String.class);
                // Force a sync after creating a topic
                syncTopics();
                System.out.println("Created topic: " + topic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void publishMessage(String topic, String message) {
        incrementClock();
        if (leaderBroker != null) {
            try {
                restTemplate.postForObject(leaderBroker + "/api/add-message?topic=" + topic + "&timestamp=" + logicalClock, message, String.class);
                System.out.println("Published message to topic " + topic + ": " + message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> getTopics() {
        incrementClock();
        // First try to get topics from the broker
        if (leaderBroker != null) {
            try {
                List<String> brokerTopics = restTemplate.getForObject(leaderBroker + "/api/topics?timestamp=" + logicalClock, List.class);
                if (brokerTopics != null) {
                    // Update our local cache
                    this.topics = brokerTopics;
                    return brokerTopics;
                }
            } catch (Exception e) {
                e.printStackTrace();
                // If we can't reach the broker, fall back to our local cache
                System.out.println("Failed to get topics from broker, using local cache");
            }
        }
        // Return our local cache if we couldn't get from broker
        return new ArrayList<>(topics);
    }

    public String getCoordinatorUrl() {
        return coordinatorUrl;
    }

    public void setCoordinatorUrl(String coordinatorUrl) {
        this.coordinatorUrl = coordinatorUrl;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getLeaderBroker() {
        incrementClock();
        return leaderBroker;
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