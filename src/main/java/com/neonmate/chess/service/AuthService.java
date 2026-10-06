package com.neonmate.chess.service;

import com.neonmate.chess.dto.request.LoginRequest;
import com.neonmate.chess.dto.request.RegisterRequest;
import com.neonmate.chess.dto.response.AuthResponse;
import com.neonmate.chess.dto.response.UserResponse;
import com.neonmate.chess.entity.User;
import com.neonmate.chess.repository.UserRepository;
import com.neonmate.chess.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username '" + request.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email '" + request.getEmail() + "' is already registered");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rating(1200)
                .build();

        user = userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .user(UserResponse.from(user))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        // The email field may contain username or email
        String identifier = request.getEmail();
        User user = userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByUsername(identifier))
                .orElseThrow(() -> new IllegalArgumentException("Invalid email/username or password"));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword())
        );

        // Mark user online
        user.setOnline(true);
        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .user(UserResponse.from(user))
                .build();
    }

    public AuthResponse refreshToken(String refreshToken) {
        String username = jwtUtil.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!jwtUtil.isTokenValid(refreshToken, userDetails)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String newToken = jwtUtil.generateToken(userDetails);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return AuthResponse.builder()
                .token(newToken)
                .refreshToken(refreshToken)
                .user(UserResponse.from(user))
                .build();
    }

    @Transactional
    public void logout(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setOnline(false);
            userRepository.save(user);
        });
    }
}
