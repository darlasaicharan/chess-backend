package com.neonmate.chess.repository;

import com.neonmate.chess.entity.RatingHistory;
import com.neonmate.chess.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RatingHistoryRepository extends JpaRepository<RatingHistory, String> {

    @Query("SELECT r FROM RatingHistory r WHERE r.user = :user ORDER BY r.recordedAt ASC")
    List<RatingHistory> findByUserOrderByRecordedAtAsc(@Param("user") User user);

    @Query("SELECT r FROM RatingHistory r WHERE r.user = :user AND r.recordedAt >= :since ORDER BY r.recordedAt ASC")
    List<RatingHistory> findByUserSince(@Param("user") User user, @Param("since") LocalDateTime since);

    long countByUser(User user);
}
