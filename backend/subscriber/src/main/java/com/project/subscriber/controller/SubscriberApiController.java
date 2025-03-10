package com.project.subscriber.controller;

import com.project.subscriber.service.SubscriberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SubscriberApiController {

    @Autowired
    private SubscriberService subscriberService;

    @GetMapping("/topics")
    public List<String> getTopics(@RequestParam long timestamp) {
        subscriberService.updateClock(timestamp);
        return subscriberService.getTopics();
    }

    @GetMapping("/subscribed-topics")
    public List<String> getSubscribedTopics(@RequestParam long timestamp) {
        subscriberService.updateClock(timestamp);
        return subscriberService.getSubscribedTopics();
    }

    @GetMapping("/messages")
    public Map<String, List<String>> getMessages(@RequestParam long timestamp) {
        subscriberService.updateClock(timestamp);
        return subscriberService.getTopicMessages();
    }

    @GetMapping("/messages/{topic}")
    public List<String> getMessagesForTopic(@PathVariable String topic, @RequestParam long timestamp) {
        subscriberService.updateClock(timestamp);
        return subscriberService.getMessagesForTopic(topic);
    }

    @PostMapping("/subscribe")
    public void subscribeTopic(@RequestBody String topic, @RequestParam long timestamp) {
        subscriberService.updateClock(timestamp);
        subscriberService.subscribeTopic(topic);
    }

    @PostMapping("/unsubscribe")
    public void unsubscribeTopic(@RequestBody String topic, @RequestParam long timestamp) {
        subscriberService.updateClock(timestamp);
        subscriberService.unsubscribeTopic(topic);
    }

    @GetMapping("/leader-broker")
    public String getLeaderBroker(@RequestParam long timestamp) {
        subscriberService.updateClock(timestamp);
        return subscriberService.getLeaderBroker();
    }

    @GetMapping("/ping")
    public String ping(@RequestParam long timestamp) {
        subscriberService.updateClock(timestamp);
        return "pong";
    }
} 