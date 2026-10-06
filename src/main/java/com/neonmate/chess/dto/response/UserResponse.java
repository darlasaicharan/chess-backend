package com.neonmate.chess.dto.response;

import com.neonmate.chess.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private String avatar;
    private int rating;
    private int gamesPlayed;
    private int wins;
    private int losses;
    private int draws;
    private String role;
    private String createdAt;
    private String country;
    private String bio;
    private String title;
    private boolean isOnline;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .rating(user.getRating())
                .gamesPlayed(user.getGamesPlayed())
                .wins(user.getWins())
                .losses(user.getLosses())
                .draws(user.getDraws())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                .country(user.getCountry())
                .bio(user.getBio())
                .title(user.getTitle())
                .isOnline(user.isOnline())
                .build();
    }
}
