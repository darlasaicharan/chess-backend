package com.neonmate.chess.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "friend_requests",
       uniqueConstraints = @UniqueConstraint(columnNames = {"sender_id", "receiver_id"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private FriendStatus status = FriendStatus.PENDING;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime respondedAt;

    public enum FriendStatus {
        PENDING, ACCEPTED, REJECTED, BLOCKED
    }
}
