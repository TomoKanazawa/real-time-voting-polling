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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SubscriberService {

    private static final Logger logger = LoggerFactory.getLogger(SubscriberService.class);

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
        logger.info("Initializing Kafka listener for all topics");
    }

    @Scheduled(fixedRate = 5000)
    public void updateLeaderBroker() {
        incrementClock();
        try {
            this.leaderBroker = restTemplate.getForObject(coordinatorUrl + "/api/leader?timestamp=" + logicalClock, String.class);
            logger.info("Updated leader broker: {}", leaderBroker);
        } catch (Exception e) {
            logger.error("Error updating leader broker", e);
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
        
        logger.info("Received message from topic {}: {}", topic, message);
        
        // Store the message even if we haven't explicitly subscribed
        // This ensures we capture all messages
        topicMessages.computeIfAbsent(topic, k -> new ArrayList<>()).add(message);
        
        // If we receive a message for a topic we're not subscribed to,
        // automatically add it to our subscribed topics
        if (!subscribedTopics.contains(topic)) {
            subscribedTopics.add(topic);
            logger.info("Auto-subscribed to topic: {}", topic);
        }
    }

    public void subscribeTopic(String topic) {
        incrementClock();
        if (!subscribedTopics.contains(topic)) {
            subscribedTopics.add(topic);
            // Initialize the message list for this topic if it doesn't exist
            topicMessages.putIfAbsent(topic, new ArrayList<>());
            logger.info("Subscribed to topic: {}", topic);
        }
    }

    public void unsubscribeTopic(String topic) {
        incrementClock();
        subscribedTopics.remove(topic);
        logger.info("Unsubscribed from topic: {}", topic);
    }

    @Scheduled(fixedRate = 5000)
    public void syncTopics() {
        incrementClock();
        try {
            ListTopicsResult listTopicsResult = adminClient.listTopics();
            Set<String> kafkaTopics = listTopicsResult.names().get();
            // Remove internal Kafka topics
            kafkaTopics.removeIf(topic -> topic.startsWith("__"));
            logger.info("Available Kafka topics: {}", kafkaTopics);
        } catch (Exception e) {
            logger.error("Error getting Kafka topics", e);
        }
    }

    public List<String> getTopics() {
        incrementClock();
        try {
            ListTopicsResult topics = adminClient.listTopics();
            Set<String> kafkaTopics = topics.names().get();
            // Filter out internal Kafka topics
            kafkaTopics.removeIf(topic -> topic.startsWith("__"));
            logger.info("Available Kafka topics: {}", kafkaTopics);
            return new ArrayList<>(kafkaTopics);
        } catch (Exception e) {
            logger.error("Error getting Kafka topics", e);
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
        logger.info("Getting messages for topic: {}, logical clock: {}", topic, logicalClock);
        
        // If we're asked for messages for a topic we're not subscribed to,
        // automatically subscribe to it
        if (!subscribedTopics.contains(topic)) {
            logger.info("Auto-subscribing to topic: {} as it was not in subscribed topics: {}", topic, subscribedTopics);
            subscribeTopic(topic);
        }
        
        List<String> messages = topicMessages.getOrDefault(topic, new ArrayList<>());
        logger.info("Retrieved {} messages for topic: {}", messages.size(), topic);
        if (messages.size() > 0) {
            logger.debug("First message sample: {}", messages.get(0));
            if (messages.size() > 1) {
                logger.debug("Last message sample: {}", messages.get(messages.size() - 1));
            }
        }
        
        return messages;
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
        logger.info("Subscriber service port set to: {}", port);
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