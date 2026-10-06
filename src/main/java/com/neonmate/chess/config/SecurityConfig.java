package com.neonmate.chess.config;

import com.neonmate.chess.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final CorsConfigurationSource corsConfigurationSource;

    /** Injected from spring.h2.console.enabled — false in prod, true in dev */
    @Value("${spring.h2.console.enabled:false}")
    private boolean h2ConsoleEnabled;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> {
                // Public endpoints
                auth.requestMatchers("/auth/**").permitAll()
                    .requestMatchers("/ws/**").permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                    .requestMatchers(HttpMethod.GET, "/leaderboard/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/puzzles/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/tournaments/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/achievements/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/games/**").permitAll()
                    .requestMatchers(HttpMethod.PUT, "/users/me").authenticated()
                    .requestMatchers(HttpMethod.GET, "/users/**").permitAll()
                    .requestMatchers("/ai/**").permitAll();

                // H2 console — only permitted in dev (when spring.h2.console.enabled=true)
                if (h2ConsoleEnabled) {
                    auth.requestMatchers("/h2-console/**").permitAll();
                }

                // Admin-only endpoints
                auth.requestMatchers("/admin/**").hasRole("ADMIN")
                    // All other endpoints require authentication
                    .anyRequest().authenticated();
            })
            .headers(headers -> {
                if (h2ConsoleEnabled) {
                    // Allow iframes only when H2 console is active (dev only)
                    headers.frameOptions(frame -> frame.sameOrigin());
                }
                // In prod: keep default secure frame options (DENY)
            })
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
