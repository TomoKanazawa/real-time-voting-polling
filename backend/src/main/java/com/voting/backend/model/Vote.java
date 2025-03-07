package com.voting.backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "votes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"poll_id", "user_id", "poll_option_id"})
})
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "poll_option_id", nullable = false)
    private PollOption pollOption;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private boolean anonymous;

    // Default constructor
    public Vote() {
    }

    // All-args constructor
    public Vote(UUID id, Poll poll, User user, PollOption pollOption, LocalDateTime timestamp, boolean anonymous) {
        this.id = id;
        this.poll = poll;
        this.user = user;
        this.pollOption = pollOption;
        this.timestamp = timestamp;
        this.anonymous = anonymous;
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PollOption getPollOption() {
        return pollOption;
    }

    public void setPollOption(PollOption pollOption) {
        this.pollOption = pollOption;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

    // Builder class
    public static class VoteBuilder {
        private UUID id;
        private Poll poll;
        private User user;
        private PollOption pollOption;
        private LocalDateTime timestamp;
        private boolean anonymous;

        public VoteBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public VoteBuilder poll(Poll poll) {
            this.poll = poll;
            return this;
        }

        public VoteBuilder user(User user) {
            this.user = user;
            return this;
        }

        public VoteBuilder pollOption(PollOption pollOption) {
            this.pollOption = pollOption;
            return this;
        }

        public VoteBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public VoteBuilder anonymous(boolean anonymous) {
            this.anonymous = anonymous;
            return this;
        }

        public Vote build() {
            return new Vote(id, poll, user, pollOption, timestamp, anonymous);
        }
    }

    public static VoteBuilder builder() {
        return new VoteBuilder();
    }
} 