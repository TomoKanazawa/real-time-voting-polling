package com.voting.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "poll_options")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PollOption {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String text;

    @ManyToOne
    @JoinColumn(name = "poll_id", nullable = false)
    @JsonIgnore
    private Poll poll;

    @Column(nullable = false)
    private int voteCount;

    // This field is used for real-time vote counting
    @Transient
    private volatile int temporaryVoteCount;

    // Default constructor
    public PollOption() {
    }

    // All-args constructor
    public PollOption(UUID id, String text, Poll poll, int voteCount, int temporaryVoteCount) {
        this.id = id;
        this.text = text;
        this.poll = poll;
        this.voteCount = voteCount;
        this.temporaryVoteCount = temporaryVoteCount;
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public synchronized void incrementVoteCount() {
        this.voteCount++;
        this.temporaryVoteCount++;
    }

    public synchronized int getTemporaryVoteCount() {
        return temporaryVoteCount;
    }

    public void setTemporaryVoteCount(int temporaryVoteCount) {
        this.temporaryVoteCount = temporaryVoteCount;
    }

    // Builder class
    public static class PollOptionBuilder {
        private UUID id;
        private String text;
        private Poll poll;
        private int voteCount;
        private int temporaryVoteCount;

        public PollOptionBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public PollOptionBuilder text(String text) {
            this.text = text;
            return this;
        }

        public PollOptionBuilder poll(Poll poll) {
            this.poll = poll;
            return this;
        }

        public PollOptionBuilder voteCount(int voteCount) {
            this.voteCount = voteCount;
            return this;
        }

        public PollOptionBuilder temporaryVoteCount(int temporaryVoteCount) {
            this.temporaryVoteCount = temporaryVoteCount;
            return this;
        }

        public PollOption build() {
            return new PollOption(id, text, poll, voteCount, temporaryVoteCount);
        }
    }

    public static PollOptionBuilder builder() {
        return new PollOptionBuilder();
    }
} 