package com.neonmate.chess.controller;

import com.neonmate.chess.dto.request.MoveRequest;
import com.neonmate.chess.dto.response.GameResponse;
import com.neonmate.chess.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class WebSocketGameController {

    private final GameService gameService;

    @MessageMapping("/game/{gameId}/move")
    @SendTo("/topic/game/{gameId}")
    public GameResponse handleMove(
            @DestinationVariable String gameId,
            @Payload MoveRequest request,
            Principal principal) {
        String username = principal != null ? principal.getName() : "anonymous";
        return gameService.makeMove(gameId, username, request);
    }

    @MessageMapping("/game/{gameId}/chat")
    @SendTo("/topic/game/{gameId}/chat")
    public Map<String, Object> handleChat(
            @DestinationVariable String gameId,
            @Payload Map<String, String> message,
            Principal principal) {
        String username = principal != null ? principal.getName() : "User";
        return Map.of(
                "gameId", gameId,
                "sender", username,
                "text", message.getOrDefault("text", ""),
                "timestamp", LocalDateTime.now().toString()
        );
    }
}
