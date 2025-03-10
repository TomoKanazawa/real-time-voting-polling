package com.project.subscriber.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SubscriberService {

    private final RestTemplate restTemplate;

    @Value("${coordinator.url}")
    private String coordinatorUrl;

    @Value("${server.port}")
    private int port;

    private String leaderBroker;
    private List<String> subscribedTopics = new ArrayList<>();
    private Map<String, List<String>> topicMessages = new HashMap<>();
    private long logicalClock = 0;

    public SubscriberService(RestTemplate restTemplate) {
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

    @Scheduled(fixedRate = 2000)
    public void fetchMessages() {
        incrementClock();
        if (leaderBroker != null) {
            for (String topic : subscribedTopics) {
                try {
                    String subscriberUrl = "http://localhost:" + port;
                    List<String> messages = restTemplate.getForObject(
                            leaderBroker + "/api/messages?topic=" + topic + "&subscriberUrl=" + subscriberUrl + "&timestamp=" + logicalClock,
                            List.class);
                    topicMessages.put(topic, messages);
                    System.out.println("Fetched messages for topic " + topic + ": " + messages);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void subscribeTopic(String topic) {
        incrementClock();
        if (leaderBroker != null && !subscribedTopics.contains(topic)) {
            try {
                String subscriberUrl = "http://localhost:" + port;
                restTemplate.postForObject(
                        leaderBroker + "/api/add-subscriber?topic=" + topic + "&timestamp=" + logicalClock,
                        subscriberUrl,
                        String.class);
                subscribedTopics.add(topic);
                topicMessages.put(topic, new ArrayList<>());
                System.out.println("Subscribed to topic: " + topic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void unsubscribeTopic(String topic) {
        incrementClock();
        if (leaderBroker != null && subscribedTopics.contains(topic)) {
            try {
                String subscriberUrl = "http://localhost:" + port;
                restTemplate.postForObject(
                        leaderBroker + "/api/remove-subscriber?topic=" + topic + "&timestamp=" + logicalClock,
                        subscriberUrl,
                        String.class);
                subscribedTopics.remove(topic);
                topicMessages.remove(topic);
                System.out.println("Unsubscribed from topic: " + topic);
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

    public List<String> getSubscribedTopics() {
        incrementClock();
        return subscribedTopics;
    }

    public Map<String, List<String>> getTopicMessages() {
        incrementClock();
        return topicMessages;
    }

    public List<String> getMessagesForTopic(String topic) {
        incrementClock();
        return topicMessages.getOrDefault(topic, new ArrayList<>());
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