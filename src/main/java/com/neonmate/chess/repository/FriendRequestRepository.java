package com.neonmate.chess.repository;

import com.neonmate.chess.entity.FriendRequest;
import com.neonmate.chess.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    List<FriendRequest> findByReceiverAndStatus(User receiver, FriendRequest.FriendStatus status);

    List<FriendRequest> findBySenderAndStatus(User sender, FriendRequest.FriendStatus status);

    @Query("SELECT fr FROM FriendRequest fr WHERE " +
           "((fr.sender = :user OR fr.receiver = :user) AND fr.status = 'ACCEPTED') " +
           "ORDER BY fr.respondedAt DESC")
    List<FriendRequest> findAcceptedFriendships(User user);

    @Query("SELECT fr FROM FriendRequest fr WHERE " +
           "((fr.sender.id = :userId1 AND fr.receiver.id = :userId2) OR " +
           " (fr.sender.id = :userId2 AND fr.receiver.id = :userId1))")
    Optional<FriendRequest> findBetweenUsers(String userId1, String userId2);

    boolean existsBySenderIdAndReceiverIdAndStatus(String senderId, String receiverId, FriendRequest.FriendStatus status);
}
