package com.voting.backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "polls")
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean active;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PollOption> options = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(nullable = false)
    private boolean multipleChoiceAllowed;

    @Column(nullable = false)
    private boolean anonymousVotingAllowed;

    // Default constructor
    public Poll() {
    }

    // All-args constructor
    public Poll(UUID id, String title, String description, LocalDateTime createdAt, LocalDateTime expiresAt, 
                boolean active, List<PollOption> options, User createdBy, boolean multipleChoiceAllowed, 
                boolean anonymousVotingAllowed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.active = active;
        this.options = options;
        this.createdBy = createdBy;
        this.multipleChoiceAllowed = multipleChoiceAllowed;
        this.anonymousVotingAllowed = anonymousVotingAllowed;
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<PollOption> getOptions() {
        return options;
    }

    public void setOptions(List<PollOption> options) {
        this.options = options;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isMultipleChoiceAllowed() {
        return multipleChoiceAllowed;
    }

    public void setMultipleChoiceAllowed(boolean multipleChoiceAllowed) {
        this.multipleChoiceAllowed = multipleChoiceAllowed;
    }

    public boolean isAnonymousVotingAllowed() {
        return anonymousVotingAllowed;
    }

    public void setAnonymousVotingAllowed(boolean anonymousVotingAllowed) {
        this.anonymousVotingAllowed = anonymousVotingAllowed;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (expiresAt == null) {
            // Default expiration: 24 hours from creation
            expiresAt = createdAt.plusHours(24);
        }
    }

    // Builder class
    public static class PollBuilder {
        private UUID id;
        private String title;
        private String description;
        private LocalDateTime createdAt;
        private LocalDateTime expiresAt;
        private boolean active;
        private List<PollOption> options = new ArrayList<>();
        private User createdBy;
        private boolean multipleChoiceAllowed;
        private boolean anonymousVotingAllowed;

        public PollBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public PollBuilder title(String title) {
            this.title = title;
            return this;
        }

        public PollBuilder description(String description) {
            this.description = description;
            return this;
        }

        public PollBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PollBuilder expiresAt(LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public PollBuilder active(boolean active) {
            this.active = active;
            return this;
        }

        public PollBuilder options(List<PollOption> options) {
            this.options = options;
            return this;
        }

        public PollBuilder createdBy(User createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public PollBuilder multipleChoiceAllowed(boolean multipleChoiceAllowed) {
            this.multipleChoiceAllowed = multipleChoiceAllowed;
            return this;
        }

        public PollBuilder anonymousVotingAllowed(boolean anonymousVotingAllowed) {
            this.anonymousVotingAllowed = anonymousVotingAllowed;
            return this;
        }

        public Poll build() {
            return new Poll(id, title, description, createdAt, expiresAt, active, options, createdBy, 
                    multipleChoiceAllowed, anonymousVotingAllowed);
        }
    }

    public static PollBuilder builder() {
        return new PollBuilder();
    }
} 