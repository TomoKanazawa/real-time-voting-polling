package com.voting.backend.controller;

import com.voting.backend.model.Poll;
import com.voting.backend.model.PollOption;
import com.voting.backend.model.User;
import com.voting.backend.repository.PollOptionRepository;
import com.voting.backend.repository.UserRepository;
import com.voting.backend.service.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/polls")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class PollController {

    private final PollService pollService;
    private final UserRepository userRepository;
    private final PollOptionRepository pollOptionRepository;

    @Autowired
    public PollController(PollService pollService, UserRepository userRepository, PollOptionRepository pollOptionRepository) {
        this.pollService = pollService;
        this.userRepository = userRepository;
        this.pollOptionRepository = pollOptionRepository;
    }

    @GetMapping
    public ResponseEntity<List<Poll>> getAllPolls() {
        try {
            System.out.println("Fetching all polls");
            List<Poll> polls = pollService.getAllPolls();
            return ResponseEntity.ok(polls);
        } catch (Exception e) {
            System.err.println("Error fetching polls: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<Poll>> getActivePolls() {
        try {
            List<Poll> polls = pollService.getActivePolls();
            return ResponseEntity.ok(polls);
        } catch (Exception e) {
            System.err.println("Error fetching active polls: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Poll>> getUserPolls(@PathVariable UUID userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            List<Poll> polls = pollService.getUserPolls(userOpt.get());
            return ResponseEntity.ok(polls);
        } catch (Exception e) {
            System.err.println("Error fetching user polls: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Poll> getPollById(@PathVariable UUID id) {
        try {
            Optional<Poll> pollOpt = pollService.getPollById(id);
            return pollOpt.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.err.println("Error fetching poll: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}/results")
    public ResponseEntity<?> getPollResults(@PathVariable UUID id) {
        try {
            System.out.println("Fetching results for poll: " + id);
            Optional<Poll> pollOpt = pollService.getPollById(id);
            
            if (pollOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Poll poll = pollOpt.get();
            List<PollOption> options = pollOptionRepository.findByPoll(poll);
            
            int totalVotes = options.stream().mapToInt(PollOption::getVoteCount).sum();
            
            List<Map<String, Object>> optionResults = new ArrayList<>();
            for (PollOption option : options) {
                Map<String, Object> optionResult = new HashMap<>();
                optionResult.put("optionId", option.getId().toString());
                optionResult.put("optionText", option.getText());
                optionResult.put("voteCount", option.getVoteCount());
                
                double percentage = totalVotes > 0 
                    ? (double) option.getVoteCount() / totalVotes * 100 
                    : 0;
                optionResult.put("percentage", percentage);
                
                optionResults.add(optionResult);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("pollId", poll.getId().toString());
            result.put("pollTitle", poll.getTitle());
            result.put("totalVotes", totalVotes);
            result.put("options", optionResults);
            result.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("Error fetching poll results: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // This method handles the request from the frontend
    @PostMapping
    public ResponseEntity<?> createPoll(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("Creating poll with request: " + request);
            System.out.println("Request keys: " + request.keySet());
            System.out.println("Request values: " + request.values());
            
            // Validate required fields
            if (!request.containsKey("userId") || request.get("userId") == null) {
                System.err.println("Missing userId in request");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "userId is required");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            if (!request.containsKey("title") || request.get("title") == null) {
                System.err.println("Missing title in request");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "title is required");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            if (!request.containsKey("options") || request.get("options") == null) {
                System.err.println("Missing options in request");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "options are required");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Extract userId from request
            String userIdStr = request.get("userId").toString();
            System.out.println("User ID from request: " + userIdStr);
            
            UUID userId;
            try {
                userId = UUID.fromString(userIdStr);
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid userId format: " + userIdStr);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid userId format");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Find user
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                System.err.println("User not found with ID: " + userId);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "User not found");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            System.out.println("Found user: " + userOpt.get().getUsername());
            
            // Create poll
            Poll poll = new Poll();
            poll.setTitle(request.get("title").toString());
            poll.setDescription(request.get("description") != null ? request.get("description").toString() : "");
            
            // Handle boolean values
            try {
                poll.setMultipleChoiceAllowed(Boolean.parseBoolean(request.get("multipleChoiceAllowed").toString()));
                poll.setAnonymousVotingAllowed(Boolean.parseBoolean(request.get("anonymousVotingAllowed").toString()));
            } catch (Exception e) {
                System.err.println("Error parsing boolean values: " + e.getMessage());
                poll.setMultipleChoiceAllowed(false);
                poll.setAnonymousVotingAllowed(false);
            }
            
            poll.setActive(true);
            
            // Save poll
            System.out.println("Saving poll: " + poll.getTitle());
            Poll savedPoll = pollService.createPoll(poll, userOpt.get());
            System.out.println("Poll saved with ID: " + savedPoll.getId());
            
            // Create options
            try {
                List<Map<String, Object>> optionsData = (List<Map<String, Object>>) request.get("options");
                System.out.println("Options data: " + optionsData);
                
                if (optionsData != null && !optionsData.isEmpty()) {
                    for (Map<String, Object> optionData : optionsData) {
                        System.out.println("Processing option: " + optionData);
                        
                        if (optionData.containsKey("text") && optionData.get("text") != null) {
                            PollOption option = new PollOption();
                            option.setText(optionData.get("text").toString());
                            option.setPoll(savedPoll);
                            option.setVoteCount(0);
                            PollOption savedOption = pollOptionRepository.save(option);
                            System.out.println("Saved option: " + savedOption.getText() + " with ID: " + savedOption.getId());
                        } else {
                            System.err.println("Option missing text field: " + optionData);
                        }
                    }
                } else {
                    System.err.println("No options provided or empty options list");
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "At least one option is required");
                    return ResponseEntity.badRequest().body(errorResponse);
                }
            } catch (ClassCastException e) {
                System.err.println("Error parsing options: " + e.getMessage());
                e.printStackTrace();
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid options format");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Fetch the poll with options
            Optional<Poll> createdPollOpt = pollService.getPollById(savedPoll.getId());
            if (createdPollOpt.isEmpty()) {
                System.err.println("Failed to retrieve created poll with ID: " + savedPoll.getId());
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Failed to retrieve created poll");
                return ResponseEntity.internalServerError().body(errorResponse);
            }
            
            System.out.println("Poll created successfully: " + createdPollOpt.get().getId());
            return ResponseEntity.ok(createdPollOpt.get());
        } catch (Exception e) {
            System.err.println("Error creating poll: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create poll: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Poll> updatePoll(@PathVariable UUID id, @RequestBody Poll poll) {
        try {
            Optional<Poll> existingPollOpt = pollService.getPollById(id);
            if (existingPollOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Poll existingPoll = existingPollOpt.get();
            existingPoll.setTitle(poll.getTitle());
            existingPoll.setDescription(poll.getDescription());
            existingPoll.setExpiresAt(poll.getExpiresAt());
            existingPoll.setActive(poll.isActive());
            existingPoll.setMultipleChoiceAllowed(poll.isMultipleChoiceAllowed());
            existingPoll.setAnonymousVotingAllowed(poll.isAnonymousVotingAllowed());
            
            Poll updatedPoll = pollService.updatePoll(existingPoll);
            return ResponseEntity.ok(updatedPoll);
        } catch (Exception e) {
            System.err.println("Error updating poll: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePoll(@PathVariable UUID id) {
        try {
            Optional<Poll> pollOpt = pollService.getPollById(id);
            if (pollOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            pollService.deletePoll(id);
            return ResponseEntity.ok(Map.of("message", "Poll deleted successfully"));
        } catch (Exception e) {
            System.err.println("Error deleting poll: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
} 