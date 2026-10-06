package com.neonmate.chess.service;

import com.neonmate.chess.dto.response.UserResponse;
import com.neonmate.chess.entity.Game;
import com.neonmate.chess.entity.RatingHistory;
import com.neonmate.chess.entity.User;
import com.neonmate.chess.exception.ResourceNotFoundException;
import com.neonmate.chess.repository.RatingHistoryRepository;
import com.neonmate.chess.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RatingHistoryRepository ratingHistoryRepository;

    public UserResponse getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateProfile(String username, Map<String, Object> updates) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        if (updates.containsKey("bio")) user.setBio((String) updates.get("bio"));
        if (updates.containsKey("avatar")) user.setAvatar((String) updates.get("avatar"));
        if (updates.containsKey("country")) user.setCountry((String) updates.get("country"));
        if (updates.containsKey("soundEnabled")) user.setSoundEnabled((Boolean) updates.get("soundEnabled"));
        if (updates.containsKey("boardTheme")) user.setBoardTheme((String) updates.get("boardTheme"));
        if (updates.containsKey("pieceStyle")) user.setPieceStyle((String) updates.get("pieceStyle"));

        user = userRepository.save(user);
        return UserResponse.from(user);
    }

    public Map<String, Object> getStats(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        double winRate = user.getGamesPlayed() > 0
                ? (double) user.getWins() / user.getGamesPlayed() * 100
                : 0;

        return Map.of(
                "rating", user.getRating(),
                "gamesPlayed", user.getGamesPlayed(),
                "wins", user.getWins(),
                "losses", user.getLosses(),
                "draws", user.getDraws(),
                "winRate", Math.round(winRate * 10.0) / 10.0
        );
    }

    public List<UserResponse> searchUsers(String query) {
        return userRepository.searchByUsername(query).stream()
                .map(UserResponse::from)
                .toList();
    }

    /**
     * Returns REAL rating history from the rating_history table.
     * Falls back to a single data point (current rating) if no history exists yet.
     */
    public List<Map<String, Object>> getRatingHistory(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Query last 90 days of real history
        LocalDateTime since = LocalDateTime.now().minusDays(90);
        List<RatingHistory> history = ratingHistoryRepository.findByUserSince(user, since);

        // If user has no history yet (no games played), return a single seed point
        if (history.isEmpty()) {
            return List.of(Map.of(
                    "date", java.time.LocalDate.now().toString(),
                    "rating", user.getRating(),
                    "change", 0
            ));
        }

        return history.stream()
                .map(r -> Map.<String, Object>of(
                        "date", r.getRecordedAt().toLocalDate().toString(),
                        "rating", r.getRating(),
                        "change", r.getRatingChange()
                ))
                .toList();
    }

    /**
     * Records a rating snapshot for a user after a game completes.
     * Called by GameService.updatePlayerStats().
     */
    @Transactional
    public void saveRatingSnapshot(User user, Game game, int ratingChange) {
        RatingHistory snapshot = RatingHistory.builder()
                .user(user)
                .rating(user.getRating())
                .ratingChange(ratingChange)
                .game(game)
                .build();
        ratingHistoryRepository.save(snapshot);
    }

    /**
     * Updates rating after a game result using Elo formula (K=32).
     */
    @Transactional
    public int[] updateRatings(User winner, User loser, boolean isDraw) {
        int K = 32;
        double expectedWinner = 1.0 / (1 + Math.pow(10, (loser.getRating() - winner.getRating()) / 400.0));
        double expectedLoser = 1.0 - expectedWinner;

        int winnerChange, loserChange;

        if (isDraw) {
            winnerChange = (int) Math.round(K * (0.5 - expectedWinner));
            loserChange = (int) Math.round(K * (0.5 - expectedLoser));
        } else {
            winnerChange = (int) Math.round(K * (1 - expectedWinner));
            loserChange = (int) Math.round(K * (0 - expectedLoser));
        }

        winner.setRating(Math.max(100, winner.getRating() + winnerChange));
        loser.setRating(Math.max(100, loser.getRating() + loserChange));

        userRepository.save(winner);
        userRepository.save(loser);

        return new int[]{winnerChange, loserChange};
    }
}
