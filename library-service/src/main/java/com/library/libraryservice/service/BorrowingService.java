package com.library.libraryservice.service;

import com.library.libraryservice.dto.BorrowingResponse;
import com.library.libraryservice.entity.Book;
import com.library.libraryservice.entity.Borrowing;
import com.library.libraryservice.entity.BorrowingStatus;
import com.library.libraryservice.entity.Reservation;
import com.library.libraryservice.entity.ReservationStatus;
import com.library.libraryservice.exception.BorrowingNotFoundException;
import com.library.libraryservice.exception.InvalidReservationStateException;
import com.library.libraryservice.repository.BookRepository;
import com.library.libraryservice.repository.BorrowingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Handles issuing an approved reservation as a physical loan, and processing its return.
 */
@Service
@RequiredArgsConstructor
public class BorrowingService {

    private final BorrowingRepository borrowingRepository;
    private final BookRepository bookRepository;
    private final ReservationService reservationService;

    @Value("${library.loan-period-days}")
    private int loanPeriodDays;

    @Transactional
    public BorrowingResponse issueBook(Long reservationId) {
        Reservation reservation = reservationService.findReservationOrThrow(reservationId);

        if (reservation.getStatus() != ReservationStatus.APPROVED) {
            throw new InvalidReservationStateException(
                    "Only APPROVED reservations can be issued (current status: " + reservation.getStatus() + ")");
        }

        Borrowing borrowing = new Borrowing();
        borrowing.setReservation(reservation);
        borrowing.setIssueDate(LocalDate.now());
        borrowing.setDueDate(LocalDate.now().plusDays(loanPeriodDays));
        borrowing.setStatus(BorrowingStatus.BORROWED);

        reservation.setStatus(ReservationStatus.ISSUED);
        // Note: availableCopies was already decremented when the reservation was created;
        // issuing does not touch it again.

        Borrowing saved = borrowingRepository.save(borrowing);
        return BorrowingResponse.fromEntity(saved);
    }

    @Transactional
    public BorrowingResponse returnBook(Long borrowingId) {
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new BorrowingNotFoundException("Borrowing record not found with id: " + borrowingId));

        if (borrowing.getStatus() == BorrowingStatus.RETURNED) {
            throw new InvalidReservationStateException("This book has already been returned");
        }

        borrowing.setReturnDate(LocalDate.now());
        borrowing.setStatus(BorrowingStatus.RETURNED);

        Reservation reservation = borrowing.getReservation();
        reservation.setStatus(ReservationStatus.COMPLETED);

        Book book = reservation.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return BorrowingResponse.fromEntity(borrowingRepository.save(borrowing));
    }

    public List<BorrowingResponse> getAllBorrowings() {
        return borrowingRepository.findAll().stream().map(BorrowingResponse::fromEntity).toList();
    }
}
