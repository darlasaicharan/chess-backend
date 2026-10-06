package com.neonmate.chess.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "achievements")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private String icon;  // Emoji or icon name

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Rarity rarity = Rarity.COMMON;

    @Builder.Default
    private int maxProgress = 1;  // Target to unlock

    @Column(nullable = false, unique = true, length = 50)
    private String code;  // Internal identifier e.g. "FIRST_WIN", "AI_SLAYER"

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum Rarity {
        COMMON, RARE, EPIC, LEGENDARY
    }
}
