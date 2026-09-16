package com.library.libraryservice.dto;

import com.library.libraryservice.entity.Borrowing;
import com.library.libraryservice.entity.BorrowingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class BorrowingResponse {

    private Long borrowingId;
    private Long reservationId;
    private Long bookId;
    private String bookTitle;
    private Long userId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private BorrowingStatus status;

    public static BorrowingResponse fromEntity(Borrowing borrowing) {
        return new BorrowingResponse(
                borrowing.getBorrowingId(),
                borrowing.getReservation().getReservationId(),
                borrowing.getReservation().getBook().getBookId(),
                borrowing.getReservation().getBook().getTitle(),
                borrowing.getReservation().getUserId(),
                borrowing.getIssueDate(),
                borrowing.getDueDate(),
                borrowing.getReturnDate(),
                borrowing.getStatus());
    }
}
