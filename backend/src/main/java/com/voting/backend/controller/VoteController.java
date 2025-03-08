package com.voting.backend.controller;

import com.voting.backend.model.Poll;
import com.voting.backend.model.PollOption;
import com.voting.backend.model.User;
import com.voting.backend.model.Vote;
import com.voting.backend.repository.PollOptionRepository;
import com.voting.backend.repository.PollRepository;
import com.voting.backend.repository.UserRepository;
import com.voting.backend.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/votes")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class VoteController {

    private final VoteRepository voteRepository;
    private final PollRepository pollRepository;
    private final PollOptionRepository pollOptionRepository;
    private final UserRepository userRepository;

    @Autowired
    public VoteController(
            VoteRepository voteRepository,
            PollRepository pollRepository,
            PollOptionRepository pollOptionRepository,
            UserRepository userRepository) {
        this.voteRepository = voteRepository;
        this.pollRepository = pollRepository;
        this.pollOptionRepository = pollOptionRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> createVote(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("Creating vote with request: " + request);
            
            // Validate required fields
            if (!request.containsKey("pollId") || request.get("pollId") == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "pollId is required"));
            }
            
            if (!request.containsKey("optionId") || request.get("optionId") == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "optionId is required"));
            }
            
            // Extract fields from request
            String pollIdStr = request.get("pollId").toString();
            String optionIdStr = request.get("optionId").toString();
            boolean anonymous = request.containsKey("anonymous") && Boolean.parseBoolean(request.get("anonymous").toString());
            
            // Parse UUIDs
            UUID pollId;
            UUID optionId;
            try {
                pollId = UUID.fromString(pollIdStr);
                optionId = UUID.fromString(optionIdStr);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid UUID format"));
            }
            
            // Find poll and option
            Optional<Poll> pollOpt = pollRepository.findById(pollId);
            if (pollOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Poll not found"));
            }
            
            Optional<PollOption> optionOpt = pollOptionRepository.findById(optionId);
            if (optionOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Option not found"));
            }
            
            Poll poll = pollOpt.get();
            PollOption option = optionOpt.get();
            
            // Check if poll is active
            if (!poll.isActive()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Poll is not active"));
            }
            
            // Check if poll has expired
            if (poll.getExpiresAt().isBefore(LocalDateTime.now())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Poll has expired"));
            }
            
            // Create vote
            Vote vote = new Vote();
            vote.setPoll(poll);
            vote.setPollOption(option);
            vote.setTimestamp(LocalDateTime.now());
            vote.setAnonymous(anonymous);
            
            // Set user if not anonymous
            if (!anonymous && request.containsKey("userId") && request.get("userId") != null) {
                String userIdStr = request.get("userId").toString();
                try {
                    UUID userId = UUID.fromString(userIdStr);
                    Optional<User> userOpt = userRepository.findById(userId);
                    if (userOpt.isPresent()) {
                        vote.setUser(userOpt.get());
                    }
                } catch (IllegalArgumentException e) {
                    // Ignore invalid user ID
                }
            }
            
            // Save vote
            Vote savedVote = voteRepository.save(vote);
            
            // Increment vote count
            option.incrementVoteCount();
            pollOptionRepository.save(option);
            
            // Return response
            Map<String, Object> response = new HashMap<>();
            response.put("id", savedVote.getId().toString());
            response.put("pollId", poll.getId().toString());
            response.put("optionId", option.getId().toString());
            response.put("timestamp", savedVote.getTimestamp().toString());
            response.put("anonymous", savedVote.isAnonymous());
            
            if (!anonymous && savedVote.getUser() != null) {
                response.put("userId", savedVote.getUser().getId().toString());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error creating vote: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to create vote: " + e.getMessage()));
        }
    }
} 