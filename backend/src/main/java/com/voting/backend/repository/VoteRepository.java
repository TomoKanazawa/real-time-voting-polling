package com.voting.backend.repository;

import com.voting.backend.model.Poll;
import com.voting.backend.model.User;
import com.voting.backend.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VoteRepository extends JpaRepository<Vote, UUID> {
    List<Vote> findByPoll(Poll poll);
    List<Vote> findByUser(User user);
    List<Vote> findByPollAndUser(Poll poll, User user);
} 