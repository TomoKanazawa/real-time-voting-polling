package com.project.subscriber.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SubscriberService {

    private final RestTemplate restTemplate;
    private final AdminClient adminClient;
    private final KafkaListenerEndpointRegistry kafkaListenerRegistry;

    @Value("${coordinator.url}")
    private String coordinatorUrl;

    @Value("${server.port}")
    private int port;

    private String leaderBroker;
    private List<String> subscribedTopics = new ArrayList<>();
    private Map<String, List<String>> topicMessages = new ConcurrentHashMap<>();
    private long logicalClock = 0;

    public SubscriberService(RestTemplate restTemplate, 
                            AdminClient adminClient,
                            KafkaListenerEndpointRegistry kafkaListenerRegistry) {
        this.restTemplate = restTemplate;
        this.adminClient = adminClient;
        this.kafkaListenerRegistry = kafkaListenerRegistry;
    }

    @PostConstruct
    public void init() {
        updateLeaderBroker();
        // Initialize the Kafka listener
        System.out.println("Initializing Kafka listener for all topics");
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

    // Listen to all topics
    @KafkaListener(id = "all-topics-listener", topicPattern = ".*")
    public void listen(ConsumerRecord<String, String> record) {
        String topic = record.topic();
        String message = record.value();
        
        // Skip internal Kafka topics
        if (topic.startsWith("__")) {
            return;
        }
        
        System.out.println("Received message from topic " + topic + ": " + message);
        
        // Store the message even if we haven't explicitly subscribed
        // This ensures we capture all messages
        topicMessages.computeIfAbsent(topic, k -> new ArrayList<>()).add(message);
        
        // If we receive a message for a topic we're not subscribed to,
        // automatically add it to our subscribed topics
        if (!subscribedTopics.contains(topic)) {
            subscribedTopics.add(topic);
            System.out.println("Auto-subscribed to topic: " + topic);
        }
    }

    public void subscribeTopic(String topic) {
        incrementClock();
        if (!subscribedTopics.contains(topic)) {
            subscribedTopics.add(topic);
            // Initialize the message list for this topic if it doesn't exist
            topicMessages.putIfAbsent(topic, new ArrayList<>());
            System.out.println("Subscribed to topic: " + topic);
        }
    }

    public void unsubscribeTopic(String topic) {
        incrementClock();
        subscribedTopics.remove(topic);
        System.out.println("Unsubscribed from topic: " + topic);
    }

    @Scheduled(fixedRate = 5000)
    public void syncTopics() {
        incrementClock();
        try {
            ListTopicsResult listTopicsResult = adminClient.listTopics();
            Set<String> kafkaTopics = listTopicsResult.names().get();
            // Remove internal Kafka topics
            kafkaTopics.removeIf(topic -> topic.startsWith("__"));
            System.out.println("Available Kafka topics: " + kafkaTopics);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getTopics() {
        incrementClock();
        try {
            ListTopicsResult listTopicsResult = adminClient.listTopics();
            Set<String> kafkaTopics = listTopicsResult.names().get();
            // Remove internal Kafka topics
            kafkaTopics.removeIf(topic -> topic.startsWith("__"));
            return new ArrayList<>(kafkaTopics);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
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
        // If we're asked for messages for a topic we're not subscribed to,
        // automatically subscribe to it
        if (!subscribedTopics.contains(topic)) {
            subscribeTopic(topic);
        }
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