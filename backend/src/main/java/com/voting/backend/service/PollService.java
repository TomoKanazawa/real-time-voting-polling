package com.voting.backend.service;

import com.voting.backend.model.Poll;
import com.voting.backend.model.User;
import com.voting.backend.repository.PollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PollService {

    private final PollRepository pollRepository;

    @Autowired
    public PollService(PollRepository pollRepository) {
        this.pollRepository = pollRepository;
    }

    public List<Poll> getAllPolls() {
        return pollRepository.findAll();
    }

    public List<Poll> getActivePolls() {
        return pollRepository.findByActiveTrue();
    }

    public List<Poll> getUserPolls(User user) {
        return pollRepository.findByCreatedBy(user);
    }

    public Optional<Poll> getPollById(UUID id) {
        return pollRepository.findById(id);
    }

    public Poll createPoll(Poll poll, User user) {
        poll.setCreatedBy(user);
        poll.setCreatedAt(LocalDateTime.now());
        if (poll.getExpiresAt() == null) {
            poll.setExpiresAt(LocalDateTime.now().plusDays(1));
        }
        poll.setActive(true);
        return pollRepository.save(poll);
    }

    public Poll updatePoll(Poll poll) {
        return pollRepository.save(poll);
    }

    public void deletePoll(UUID id) {
        pollRepository.deleteById(id);
    }
} 