package com.voting.backend.repository;

import com.voting.backend.model.Poll;
import com.voting.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PollRepository extends JpaRepository<Poll, UUID> {
    List<Poll> findByActiveTrue();
    List<Poll> findByCreatedBy(User user);
} 