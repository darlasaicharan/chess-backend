package com.neonmate.chess.repository;

import com.neonmate.chess.entity.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {

    List<UserAchievement> findByUserId(String userId);

    Optional<UserAchievement> findByUserIdAndAchievementId(String userId, String achievementId);

    boolean existsByUserIdAndAchievementIdAndUnlockedAtIsNotNull(String userId, String achievementId);
}
