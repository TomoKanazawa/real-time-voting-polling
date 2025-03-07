package com.voting.backend.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "http://localhost:3000")
public class TestController {

    @GetMapping
    public Map<String, String> test() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "CORS is working!");
        return response;
    }
    
    @PostMapping
    public Map<String, String> testPost(@RequestBody(required = false) Map<String, Object> request) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "POST request received successfully");
        response.put("received", request != null ? request.toString() : "null");
        return response;
    }
} 