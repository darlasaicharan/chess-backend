package com.neonmate.chess.service;

import com.neonmate.chess.dto.response.FriendRequestResponse;
import com.neonmate.chess.dto.response.UserResponse;
import com.neonmate.chess.entity.FriendRequest;
import com.neonmate.chess.entity.User;
import com.neonmate.chess.exception.ResourceNotFoundException;
import com.neonmate.chess.repository.FriendRequestRepository;
import com.neonmate.chess.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;

    public List<UserResponse> getFriends(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        List<FriendRequest> friendships = friendRequestRepository.findAcceptedFriendships(user);
        return friendships.stream()
                .map(fr -> fr.getSender().getId().equals(user.getId()) ? fr.getReceiver() : fr.getSender())
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    public List<FriendRequestResponse> getPendingRequests(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        List<FriendRequest> pending = friendRequestRepository.findByReceiverAndStatus(user, FriendRequest.FriendStatus.PENDING);
        return pending.stream()
                .map(FriendRequestResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public FriendRequestResponse sendRequest(String username, String targetUserId) {
        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        User receiver = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", targetUserId));

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("Cannot send a friend request to yourself");
        }

        // Check if relationship already exists
        return friendRequestRepository.findBetweenUsers(sender.getId(), receiver.getId())
                .map(existing -> {
                    if (existing.getStatus() == FriendRequest.FriendStatus.ACCEPTED) {
                        throw new IllegalStateException("Already friends with this user");
                    }
                    if (existing.getStatus() == FriendRequest.FriendStatus.BLOCKED) {
                        throw new IllegalStateException("Unable to send friend request");
                    }
                    existing.setStatus(FriendRequest.FriendStatus.PENDING);
                    existing.setSender(sender);
                    existing.setReceiver(receiver);
                    return FriendRequestResponse.from(friendRequestRepository.save(existing));
                })
                .orElseGet(() -> {
                    FriendRequest request = FriendRequest.builder()
                            .sender(sender)
                            .receiver(receiver)
                            .status(FriendRequest.FriendStatus.PENDING)
                            .build();
                    return FriendRequestResponse.from(friendRequestRepository.save(request));
                });
    }

    @Transactional
    public FriendRequestResponse acceptRequest(String username, String targetUserId) {
        User receiver = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        FriendRequest request = friendRequestRepository.findBetweenUsers(receiver.getId(), targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("FriendRequest", "targetUserId", targetUserId));

        request.setStatus(FriendRequest.FriendStatus.ACCEPTED);
        request.setRespondedAt(LocalDateTime.now());
        return FriendRequestResponse.from(friendRequestRepository.save(request));
    }

    @Transactional
    public void rejectRequest(String username, String targetUserId) {
        User receiver = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        friendRequestRepository.findBetweenUsers(receiver.getId(), targetUserId)
                .ifPresent(friendRequestRepository::delete);
    }

    @Transactional
    public void removeFriend(String username, String targetUserId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        friendRequestRepository.findBetweenUsers(user.getId(), targetUserId)
                .ifPresent(friendRequestRepository::delete);
    }

    @Transactional
    public void blockUser(String username, String targetUserId) {
        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", targetUserId));

        FriendRequest request = friendRequestRepository.findBetweenUsers(sender.getId(), target.getId())
                .orElseGet(() -> FriendRequest.builder().sender(sender).receiver(target).build());

        request.setStatus(FriendRequest.FriendStatus.BLOCKED);
        request.setRespondedAt(LocalDateTime.now());
        friendRequestRepository.save(request);
    }
}
