package com.neonmate.chess;

import com.neonmate.chess.dto.response.AIAnalysisResponse;
import com.neonmate.chess.entity.User;
import com.neonmate.chess.repository.UserRepository;
import com.neonmate.chess.service.AiService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NeonmateApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AiService aiService;

    @Test
    @DisplayName("Spring context loads successfully")
    void contextLoads() {
        assertNotNull(userRepository);
        assertNotNull(aiService);
    }

    @Test
    @DisplayName("Database is pre-seeded with admin and GM accounts")
    void testDatabaseSeeding() {
        Optional<User> admin = userRepository.findByUsername("admin");
        assertTrue(admin.isPresent(), "Admin user should be seeded");
        assertEquals(User.Role.ADMIN, admin.get().getRole());

        Optional<User> gm = userRepository.findByUsername("NeonKing");
        assertTrue(gm.isPresent(), "NeonKing GM should be seeded");
        assertTrue(gm.get().getRating() >= 2800);
    }

    @Test
    @DisplayName("AI heuristic evaluation produces valid moves and scores")
    void testAiHeuristicEvaluation() {
        String startingFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        AIAnalysisResponse analysis = aiService.analyzePosition(startingFen, 10);

        assertNotNull(analysis);
        assertNotNull(analysis.getBestMove(), "Best move must not be null");
        assertEquals(10, analysis.getDepth());
        assertFalse(analysis.getPv().isEmpty());

        Map<String, Object> bestMoveMap = aiService.getBestMove(startingFen, 12);
        assertNotNull(bestMoveMap.get("bestMove"));
        assertEquals(12, bestMoveMap.get("depth"));
    }
}
