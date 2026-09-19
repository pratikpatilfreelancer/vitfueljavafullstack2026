package com.library.libraryservice.repository;

import com.library.libraryservice.entity.Reservation;
import com.library.libraryservice.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserId(Long userId);

    boolean existsByUserIdAndBook_BookIdAndStatusIn(Long userId, Long bookId, Collection<ReservationStatus> statuses);
}
