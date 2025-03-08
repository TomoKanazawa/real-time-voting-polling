package com.voting.backend.repository;

import com.voting.backend.model.Poll;
import com.voting.backend.model.PollOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PollOptionRepository extends JpaRepository<PollOption, UUID> {
    List<PollOption> findByPoll(Poll poll);
} 