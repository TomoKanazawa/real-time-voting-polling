package com.voting.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "polls")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (expiresAt == null) {
            // Default expiration: 24 hours from creation
            expiresAt = createdAt.plusHours(24);
        }
    }
} 