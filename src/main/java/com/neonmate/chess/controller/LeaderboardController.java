package com.neonmate.chess.controller;

import com.neonmate.chess.dto.response.LeaderboardResponse;
import com.neonmate.chess.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    public ResponseEntity<LeaderboardResponse> getLeaderboard() {
        return ResponseEntity.ok(leaderboardService.getGlobalLeaderboard());
    }

    @GetMapping("/global")
    public ResponseEntity<LeaderboardResponse> getGlobal() {
        return ResponseEntity.ok(leaderboardService.getGlobalLeaderboard());
    }

    @GetMapping("/friends")
    public ResponseEntity<LeaderboardResponse> getFriends(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.ok(leaderboardService.getGlobalLeaderboard());
        }
        return ResponseEntity.ok(leaderboardService.getFriendsLeaderboard(userDetails.getUsername()));
    }

    @GetMapping("/{timeControl}")
    public ResponseEntity<LeaderboardResponse> getByTimeControl(@PathVariable String timeControl) {
        return ResponseEntity.ok(leaderboardService.getByTimeControl(timeControl));
    }
}
