package com.neonmate.chess.controller;

import com.neonmate.chess.dto.response.UserResponse;
import com.neonmate.chess.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getProfile(@PathVariable String username) {
        return ResponseEntity.ok(userService.getProfile(username));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(userService.updateProfile(userDetails.getUsername(), updates));
    }

    @GetMapping("/{username}/stats")
    public ResponseEntity<Map<String, Object>> getStats(@PathVariable String username) {
        return ResponseEntity.ok(userService.getStats(username));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam("q") String query) {
        return ResponseEntity.ok(userService.searchUsers(query));
    }

    @GetMapping("/{username}/rating-history")
    public ResponseEntity<List<Map<String, Object>>> getRatingHistory(@PathVariable String username) {
        return ResponseEntity.ok(userService.getRatingHistory(username));
    }
}
