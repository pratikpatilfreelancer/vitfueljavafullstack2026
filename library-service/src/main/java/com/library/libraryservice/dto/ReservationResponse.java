package com.library.libraryservice.dto;

import com.library.libraryservice.entity.Reservation;
import com.library.libraryservice.entity.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReservationResponse {

    private Long reservationId;
    private Long userId;
    private Long bookId;
    private String bookTitle;
    private LocalDateTime reservationDate;
    private ReservationStatus status;

    public static ReservationResponse fromEntity(Reservation reservation) {
        return new ReservationResponse(
                reservation.getReservationId(),
                reservation.getUserId(),
                reservation.getBook().getBookId(),
                reservation.getBook().getTitle(),
                reservation.getReservationDate(),
                reservation.getStatus());
    }
}
