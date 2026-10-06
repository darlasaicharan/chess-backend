package com.neonmate.chess.dto.response;

import com.neonmate.chess.entity.FriendRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestResponse {
    private Long id;
    private UserResponse sender;
    private UserResponse receiver;
    private String status;
    private String createdAt;
    private String respondedAt;

    public static FriendRequestResponse from(FriendRequest fr) {
        return FriendRequestResponse.builder()
                .id(fr.getId())
                .sender(UserResponse.from(fr.getSender()))
                .receiver(UserResponse.from(fr.getReceiver()))
                .status(fr.getStatus().name())
                .createdAt(fr.getCreatedAt() != null ? fr.getCreatedAt().toString() : null)
                .respondedAt(fr.getRespondedAt() != null ? fr.getRespondedAt().toString() : null)
                .build();
    }
}
