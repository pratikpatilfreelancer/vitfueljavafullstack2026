package com.library.libraryservice.service;

import com.library.libraryservice.dto.ReservationResponse;
import com.library.libraryservice.entity.Book;
import com.library.libraryservice.entity.Reservation;
import com.library.libraryservice.entity.ReservationStatus;
import com.library.libraryservice.exception.BookNotFoundException;
import com.library.libraryservice.exception.BookUnavailableException;
import com.library.libraryservice.exception.DuplicateReservationException;
import com.library.libraryservice.exception.InvalidReservationStateException;
import com.library.libraryservice.exception.ReservationNotFoundException;
import com.library.libraryservice.repository.BookRepository;
import com.library.libraryservice.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Implements the reservation workflow described in the project spec:
 * create (PENDING, availableCopies-1) -> approve (APPROVED) -> issued/cancelled,
 * with duplicate-reservation and availability checks along the way.
 */
@Service
@RequiredArgsConstructor
public class ReservationService {

    /** Statuses that count as "an active reservation already exists for this book". */
    private static final Set<ReservationStatus> ACTIVE_STATUSES =
            EnumSet.of(ReservationStatus.PENDING, ReservationStatus.APPROVED, ReservationStatus.ISSUED);

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;

    @Transactional
    public ReservationResponse createReservation(Long userId, Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + bookId));

        if (book.getAvailableCopies() <= 0) {
            throw new BookUnavailableException("No copies of \"" + book.getTitle() + "\" are currently available");
        }

        boolean hasActiveReservation = reservationRepository
                .existsByUserIdAndBook_BookIdAndStatusIn(userId, bookId, ACTIVE_STATUSES);
        if (hasActiveReservation) {
            throw new DuplicateReservationException("You already have an active reservation for this book");
        }

        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setBook(book);
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setStatus(ReservationStatus.PENDING);

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        return ReservationResponse.fromEntity(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationResponse cancelReservation(Long reservationId, Long requesterUserId, boolean isLibrarian) {
        Reservation reservation = findReservationOrThrow(reservationId);

        if (!isLibrarian && !reservation.getUserId().equals(requesterUserId)) {
            throw new AccessDeniedException("You can only cancel your own reservations");
        }

        if (reservation.getStatus() != ReservationStatus.PENDING && reservation.getStatus() != ReservationStatus.APPROVED) {
            throw new InvalidReservationStateException(
                    "Only PENDING or APPROVED reservations can be cancelled (current status: " + reservation.getStatus() + ")");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);

        Book book = reservation.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return ReservationResponse.fromEntity(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationResponse approveReservation(Long reservationId) {
        Reservation reservation = findReservationOrThrow(reservationId);

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new InvalidReservationStateException(
                    "Only PENDING reservations can be approved (current status: " + reservation.getStatus() + ")");
        }

        reservation.setStatus(ReservationStatus.APPROVED);
        return ReservationResponse.fromEntity(reservationRepository.save(reservation));
    }

    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll().stream().map(ReservationResponse::fromEntity).toList();
    }

    public List<ReservationResponse> getReservationsByUser(Long userId, Long requesterUserId, boolean isLibrarian) {
        if (!isLibrarian && !userId.equals(requesterUserId)) {
            throw new AccessDeniedException("You can only view your own reservations");
        }
        return reservationRepository.findByUserId(userId).stream().map(ReservationResponse::fromEntity).toList();
    }

    Reservation findReservationOrThrow(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with id: " + reservationId));
    }
}
