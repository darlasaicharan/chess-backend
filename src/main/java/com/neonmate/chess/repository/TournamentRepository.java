package com.neonmate.chess.repository;

import com.neonmate.chess.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, String> {

    List<Tournament> findByStatusOrderByStartDateAsc(Tournament.TournamentStatus status);

    List<Tournament> findAllByOrderByCreatedAtDesc();
}
