package com.neonmate.chess.dto.response;

import com.neonmate.chess.entity.Achievement;
import com.neonmate.chess.entity.UserAchievement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AchievementResponse {
    private String id;
    private String code;
    private String title;
    private String description;
    private String icon;
    private String rarity;
    private int progress;
    private int maxProgress;
    private String unlockedAt;

    public static AchievementResponse from(Achievement achievement, UserAchievement userAchievement) {
        return AchievementResponse.builder()
                .id(achievement.getId())
                .code(achievement.getCode())
                .title(achievement.getTitle())
                .description(achievement.getDescription())
                .icon(achievement.getIcon())
                .rarity(achievement.getRarity().name().toLowerCase())
                .maxProgress(achievement.getMaxProgress())
                .progress(userAchievement != null ? userAchievement.getProgress() : 0)
                .unlockedAt(userAchievement != null && userAchievement.getUnlockedAt() != null
                        ? userAchievement.getUnlockedAt().toString() : null)
                .build();
    }
}
