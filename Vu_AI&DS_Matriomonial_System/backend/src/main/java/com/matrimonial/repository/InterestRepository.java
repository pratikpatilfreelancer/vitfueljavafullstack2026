package com.matrimonial.repository;

import com.matrimonial.entity.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InterestRepository extends JpaRepository<Interest, Long> {
    List<Interest> findBySenderUserId(Long senderId);
    List<Interest> findByReceiverUserId(Long receiverId);
    boolean existsBySenderUserIdAndReceiverUserIdAndStatus(Long senderId, Long receiverId, String status);

    @Modifying
    @Query(value = "DELETE FROM interests WHERE sender_id = :userId OR receiver_id = :userId", nativeQuery = true)
    void deleteByUserId(@Param("userId") Long userId);
}
