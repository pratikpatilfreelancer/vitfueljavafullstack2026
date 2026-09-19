package com.library.libraryservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/** Payload the librarian sends to issue a book against an approved reservation. */
@Getter
@Setter
public class BorrowingRequest {

    @NotNull(message = "reservationId is required")
    private Long reservationId;
}
