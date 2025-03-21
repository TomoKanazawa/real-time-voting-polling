package com.project.publisher.controller;

import com.project.publisher.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PublisherApiController {

    @Autowired
    private PublisherService publisherService;

    @GetMapping("/topics")
    public List<String> getTopics(@RequestParam long timestamp) {
        publisherService.updateClock(timestamp);
        return publisherService.getTopics();
    }

    @PostMapping("/create-topic")
    public void createTopic(@RequestBody String topic, @RequestParam long timestamp) {
        publisherService.updateClock(timestamp);
        publisherService.createTopic(topic);
    }

    @PostMapping("/publish")
    public void publishMessage(@RequestParam String topic, @RequestBody String message, @RequestParam long timestamp) {
        publisherService.updateClock(timestamp);
        publisherService.publishMessage(topic, message);
    }

    @GetMapping("/leader-broker")
    public String getLeaderBroker(@RequestParam long timestamp) {
        publisherService.updateClock(timestamp);
        return publisherService.getLeaderBroker();
    }

    @GetMapping("/ping")
    public String ping(@RequestParam long timestamp) {
        publisherService.updateClock(timestamp);
        return "pong";
    }
} 