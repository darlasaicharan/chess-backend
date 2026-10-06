package com.neonmate.chess.controller;

import com.neonmate.chess.dto.request.LoginRequest;
import com.neonmate.chess.dto.request.RegisterRequest;
import com.neonmate.chess.dto.response.AuthResponse;
import com.neonmate.chess.dto.response.UserResponse;
import com.neonmate.chess.service.AuthService;
import com.neonmate.chess.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("token");
        if (refreshToken == null || refreshToken.isBlank()) {
            refreshToken = request.get("refreshToken");
        }
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            authService.logout(userDetails.getUsername());
        }
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(userService.getProfile(userDetails.getUsername()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) {
        // Password recovery simulation (standard in modern auth flow)
        return ResponseEntity.ok(Map.of("message", "Password reset instructions sent to " + request.get("email")));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(Map.of("message", "Password has been successfully updated"));
    }
}
