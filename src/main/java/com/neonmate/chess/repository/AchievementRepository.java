package com.neonmate.chess.repository;

import com.neonmate.chess.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, String> {

    Optional<Achievement> findByCode(String code);
}
