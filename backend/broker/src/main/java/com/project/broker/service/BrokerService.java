package com.project.broker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.util.*;

@Service
public class BrokerService {

    private final RestTemplate restTemplate;

    @Value("${coordinator.url}")
    private String coordinatorUrl;

    @Value("${server.port}")
    private int port;

    private String leader;
    private List<String> brokers = new ArrayList<>();
    private Set<String> topics = new HashSet<>();
    private Map<String, List<String>> messages = new HashMap<>();
    private Map<String, List<String>> subscribers = new HashMap<>();
    private long logicalClock = 0;

    public BrokerService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        registerBroker();
        updateLeaderAndBrokers();
    }


    public boolean isSubscriberAlive(String subscriberUrl) {
        try {
            restTemplate.getForObject(subscriberUrl + "/api/ping?timestamp=" + logicalClock, String.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String, List<String>> getSubscribersWithTopics() {
        incrementClock();
        return subscribers;
    }

    @Scheduled(fixedRate = 1000)
    public void sendHeartbeat() {
        incrementClock();
        System.out.println("Sending heart beat: " + new Date());
        try {
            String brokerUrl = "http://localhost:" + port;
            restTemplate.postForObject(coordinatorUrl + "/api/heartbeat?timestamp=" + logicalClock, brokerUrl, String.class);
            System.out.println("Sent heartbeat from broker at: " + brokerUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 3000)
    public void updateLeaderAndBrokers() {
        incrementClock();
        try {
            this.leader = restTemplate.getForObject(coordinatorUrl + "/api/leader?timestamp=" + logicalClock, String.class);
            this.brokers = restTemplate.getForObject(coordinatorUrl + "/api/brokers?timestamp=" + logicalClock, List.class);
            System.out.println("Updated leader: " + leader);
            System.out.println("Updated brokers: " + brokers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 1000)
    public void syncDataWithLeader() {
        incrementClock();
        if (!("http://localhost:" + port).equals(leader)) {
            try {
                String leaderUrl = leader + "/api/data?timestamp=" + logicalClock;
                Map<String, Object> leaderData = restTemplate.getForObject(leaderUrl, Map.class);
                updateInMemoryData(leaderData);
                System.out.println("Synchronized data with leader broker at: " + leaderUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateInMemoryData(Map<String, Object> data) {
        this.topics = new HashSet<>((List<String>) data.get("topics"));
        this.messages = (Map<String, List<String>>) data.get("messages");
        this.subscribers = (Map<String, List<String>>) data.get("subscribers");
    }

    private void registerBroker() {
        incrementClock();
        if (port!=0) {
            try {
                String brokerUrl = "http://localhost:" + port;
                restTemplate.postForObject(coordinatorUrl + "/api/register?timestamp=" + logicalClock, brokerUrl, String.class);
                System.out.println("Registered broker at: " + brokerUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isSubscriberSubscribedToTopic(String subscriberUrl, String topic) {
        incrementClock();
        return subscribers.containsKey(topic) && subscribers.get(topic).contains(subscriberUrl);
    }

    public List<String> getBrokers() {
        incrementClock();
        return brokers;
    }

    public String getLeader() {
        incrementClock();
        return leader;
    }

    public Set<String> getTopics() {
        incrementClock();
        return topics;
    }

    public Map<String, List<String>> getMessages() {
        incrementClock();
        return messages;
    }

    public List<String> getMessages(String topic) {
        incrementClock();
        return messages.getOrDefault(topic, new ArrayList<>());
    }

    public void addTopic(String topic) {
        incrementClock();
        topics.add(topic);
    }

    public void addMessage(String topic, String message) {
        incrementClock();
        messages.computeIfAbsent(topic, k -> new ArrayList<>()).add(message);
        System.out.println("Message added to topic " + topic + ": " + message);
    }

    public List<String> getSubscribers() {
        incrementClock();
        List<String> allSubscribers = new ArrayList<>();
        for (List<String> topicSubscribers : subscribers.values()) {
            allSubscribers.addAll(topicSubscribers);
        }
        return allSubscribers;
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

    public void logNewPublisherContact(String publisherUrl) {
        incrementClock();
        System.out.println("New publisher contacted the leader broker: " + publisherUrl);
    }

    public void addSubscriber(String topic, String subscriberUrl) {
        incrementClock();
        subscribers.computeIfAbsent(topic, k -> new ArrayList<>()).add(subscriberUrl);
    }

    public void removeSubscriber(String topic, String subscriberUrl) {
        incrementClock();
        List<String> topicSubscribers = subscribers.get(topic);
        if (topicSubscribers != null) {
            topicSubscribers.remove(subscriberUrl);
            if (topicSubscribers.isEmpty()) {
                subscribers.remove(topic);
            }
        }
    }

    public List<String> getSubscribers(String topic) {
        incrementClock();
        return subscribers.getOrDefault(topic, new ArrayList<>());
    }

    public Map<String, Object> getAllData() {
        incrementClock();
        Map<String, Object> data = new HashMap<>();
        data.put("topics", new ArrayList<>(topics));
        data.put("messages", new HashMap<>(messages));
        data.put("subscribers", new HashMap<>(subscribers));
        return data;
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