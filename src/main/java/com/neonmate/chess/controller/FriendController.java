package com.neonmate.chess.controller;

import com.neonmate.chess.dto.response.FriendRequestResponse;
import com.neonmate.chess.dto.response.UserResponse;
import com.neonmate.chess.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getFriends(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(friendService.getFriends(userDetails.getUsername()));
    }

    @GetMapping("/requests")
    public ResponseEntity<List<FriendRequestResponse>> getPendingRequests(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(friendService.getPendingRequests(userDetails.getUsername()));
    }

    @PostMapping("/request/{userId}")
    public ResponseEntity<FriendRequestResponse> sendRequest(
            @PathVariable String userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(friendService.sendRequest(userDetails.getUsername(), userId));
    }

    @PostMapping("/accept/{userId}")
    public ResponseEntity<FriendRequestResponse> acceptRequest(
            @PathVariable String userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(friendService.acceptRequest(userDetails.getUsername(), userId));
    }

    @DeleteMapping("/reject/{userId}")
    public ResponseEntity<Map<String, String>> rejectRequest(
            @PathVariable String userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        friendService.rejectRequest(userDetails.getUsername(), userId);
        return ResponseEntity.ok(Map.of("message", "Friend request rejected"));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, String>> removeFriend(
            @PathVariable String userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        friendService.removeFriend(userDetails.getUsername(), userId);
        return ResponseEntity.ok(Map.of("message", "Friend removed"));
    }

    @PostMapping("/block/{userId}")
    public ResponseEntity<Map<String, String>> blockUser(
            @PathVariable String userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        friendService.blockUser(userDetails.getUsername(), userId);
        return ResponseEntity.ok(Map.of("message", "User blocked"));
    }
}
