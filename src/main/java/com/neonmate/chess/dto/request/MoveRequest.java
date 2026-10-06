package com.neonmate.chess.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MoveRequest {
    @NotBlank
    private String from;

    @NotBlank
    private String to;

    private String promotion;  // optional, e.g. "q"
    private String player;     // optional username for WebSocket context
}
