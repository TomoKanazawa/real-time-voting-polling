package com.voting.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "poll_options")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PollOption {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String text;

    @ManyToOne
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @Column(nullable = false)
    private int voteCount;

    // This field is used for real-time vote counting
    @Transient
    private volatile int temporaryVoteCount;

    public synchronized void incrementVoteCount() {
        this.voteCount++;
        this.temporaryVoteCount++;
    }

    public synchronized int getTemporaryVoteCount() {
        return temporaryVoteCount;
    }
} 