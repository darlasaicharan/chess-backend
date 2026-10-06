package com.neonmate.chess.service;

import com.neonmate.chess.dto.response.LeaderboardResponse;
import com.neonmate.chess.dto.response.UserResponse;
import com.neonmate.chess.entity.FriendRequest;
import com.neonmate.chess.entity.User;
import com.neonmate.chess.exception.ResourceNotFoundException;
import com.neonmate.chess.repository.FriendRequestRepository;
import com.neonmate.chess.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;

    public LeaderboardResponse getGlobalLeaderboard() {
        List<User> topUsers = userRepository.findTop100ByOrderByRatingDesc();
        return buildLeaderboardResponse(topUsers);
    }

    public LeaderboardResponse getFriendsLeaderboard(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        List<FriendRequest> friendships = friendRequestRepository.findAcceptedFriendships(user);
        List<User> friends = new ArrayList<>();
        friends.add(user); // Include current user
        for (FriendRequest fr : friendships) {
            friends.add(fr.getSender().getId().equals(user.getId()) ? fr.getReceiver() : fr.getSender());
        }

        friends.sort(Comparator.comparingInt(User::getRating).reversed());
        return buildLeaderboardResponse(friends);
    }

    public LeaderboardResponse getByTimeControl(String timeControl) {
        // Can filter by time control specific rating if available; fallback to global rating
        List<User> users = userRepository.findTop100ByOrderByRatingDesc();
        return buildLeaderboardResponse(users);
    }

    private LeaderboardResponse buildLeaderboardResponse(List<User> users) {
        List<LeaderboardResponse.LeaderboardEntry> entries = new ArrayList<>();
        int rank = 1;

        for (User u : users) {
            double winRate = u.getGamesPlayed() > 0
                    ? (double) u.getWins() / u.getGamesPlayed() * 100.0
                    : 0.0;

            entries.add(LeaderboardResponse.LeaderboardEntry.builder()
                    .rank(rank++)
                    .user(UserResponse.from(u))
                    .rating(u.getRating())
                    .gamesPlayed(u.getGamesPlayed())
                    .winRate(Math.round(winRate * 10.0) / 10.0)
                    .build());
        }

        return LeaderboardResponse.builder()
                .entries(entries)
                .build();
    }
}
