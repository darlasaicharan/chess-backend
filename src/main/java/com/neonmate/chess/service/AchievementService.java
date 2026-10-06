package com.neonmate.chess.service;

import com.neonmate.chess.dto.response.AchievementResponse;
import com.neonmate.chess.entity.Achievement;
import com.neonmate.chess.entity.User;
import com.neonmate.chess.entity.UserAchievement;
import com.neonmate.chess.exception.ResourceNotFoundException;
import com.neonmate.chess.repository.AchievementRepository;
import com.neonmate.chess.repository.UserAchievementRepository;
import com.neonmate.chess.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<AchievementResponse> getAllAchievements() {
        return achievementRepository.findAll().stream()
                .map(a -> AchievementResponse.from(a, null))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AchievementResponse> getUserAchievements(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        List<Achievement> allAchievements = achievementRepository.findAll();
        List<UserAchievement> userAchievements = userAchievementRepository.findByUserId(user.getId());

        Map<String, UserAchievement> userAchMap = userAchievements.stream()
                .filter(ua -> ua.getAchievement() != null)
                .collect(Collectors.toMap(
                        ua -> ua.getAchievement().getId(),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        return allAchievements.stream()
                .map(a -> AchievementResponse.from(a, userAchMap.get(a.getId())))
                .collect(Collectors.toList());
    }

    @Transactional
    public AchievementResponse unlockOrProgress(String username, String achievementCode, int addedProgress) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Achievement achievement = achievementRepository.findAll().stream()
                .filter(a -> achievementCode.equalsIgnoreCase(a.getCode()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Achievement", "code", achievementCode));

        Optional<UserAchievement> existingOpt = userAchievementRepository.findByUserIdAndAchievementId(user.getId(), achievement.getId());

        UserAchievement ua;
        if (existingOpt.isPresent()) {
            ua = existingOpt.get();
            int newProgress = Math.min(achievement.getMaxProgress(), ua.getProgress() + addedProgress);
            ua.setProgress(newProgress);
            if (newProgress >= achievement.getMaxProgress() && ua.getUnlockedAt() == null) {
                ua.setUnlockedAt(LocalDateTime.now());
            }
        } else {
            int newProgress = Math.min(achievement.getMaxProgress(), addedProgress);
            ua = UserAchievement.builder()
                    .user(user)
                    .achievement(achievement)
                    .progress(newProgress)
                    .unlockedAt(newProgress >= achievement.getMaxProgress() ? LocalDateTime.now() : null)
                    .build();
        }

        ua = userAchievementRepository.save(ua);
        return AchievementResponse.from(achievement, ua);
    }
}
