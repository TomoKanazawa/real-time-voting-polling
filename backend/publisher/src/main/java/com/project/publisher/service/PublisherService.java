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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createTopic(String topic) {
        incrementClock();
        if (leaderBroker != null) {
            try {
                restTemplate.postForObject(leaderBroker + "/api/add-topic?timestamp=" + logicalClock, topic, String.class);
                if (!topics.contains(topic)) {
                    topics.add(topic);
                }
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
        if (leaderBroker != null) {
            try {
                return restTemplate.getForObject(leaderBroker + "/api/topics?timestamp=" + logicalClock, List.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
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