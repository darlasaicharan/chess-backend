package com.neonmate.chess.controller;

import com.neonmate.chess.dto.response.AchievementResponse;
import com.neonmate.chess.service.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;

    @GetMapping
    public ResponseEntity<List<AchievementResponse>> getAchievements(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String username) {
        String targetUser = (username != null && !username.isBlank())
                ? username
                : (userDetails != null ? userDetails.getUsername() : null);

        if (targetUser != null) {
            return ResponseEntity.ok(achievementService.getUserAchievements(targetUser));
        }
        return ResponseEntity.ok(achievementService.getAllAchievements());
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<AchievementResponse>> getUserAchievements(@PathVariable String username) {
        return ResponseEntity.ok(achievementService.getUserAchievements(username));
    }

    @PostMapping("/progress")
    public ResponseEntity<AchievementResponse> updateProgress(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> payload) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        String code = (String) payload.get("code");
        int progress = payload.get("progress") != null ? ((Number) payload.get("progress")).intValue() : 1;
        return ResponseEntity.ok(achievementService.unlockOrProgress(userDetails.getUsername(), code, progress));
    }
}
