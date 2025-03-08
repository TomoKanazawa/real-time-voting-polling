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

    // This method handles the request from the frontend
    @PostMapping
    public ResponseEntity<Poll> createPoll(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("Creating poll: " + request);
            
            // Extract userId from request
            String userIdStr = request.get("userId").toString();
            UUID userId = UUID.fromString(userIdStr);
            
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            // Create poll
            Poll poll = new Poll();
            poll.setTitle(request.get("title").toString());
            poll.setDescription(request.get("description") != null ? request.get("description").toString() : "");
            poll.setMultipleChoiceAllowed(Boolean.parseBoolean(request.get("multipleChoiceAllowed").toString()));
            poll.setAnonymousVotingAllowed(Boolean.parseBoolean(request.get("anonymousVotingAllowed").toString()));
            poll.setActive(true);
            
            // Save poll
            Poll savedPoll = pollService.createPoll(poll, userOpt.get());
            
            // Create options
            List<Map<String, Object>> optionsData = (List<Map<String, Object>>) request.get("options");
            if (optionsData != null) {
                for (Map<String, Object> optionData : optionsData) {
                    PollOption option = new PollOption();
                    option.setText(optionData.get("text").toString());
                    option.setPoll(savedPoll);
                    option.setVoteCount(0);
                    pollOptionRepository.save(option);
                }
            }
            
            // Fetch the poll with options
            Optional<Poll> createdPollOpt = pollService.getPollById(savedPoll.getId());
            return createdPollOpt.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.internalServerError().build());
        } catch (Exception e) {
            System.err.println("Error creating poll: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
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