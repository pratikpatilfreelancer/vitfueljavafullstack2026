package com.library.libraryservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Note: there is deliberately no userId field here. The reserving student is
 * always taken from the authenticated principal (see SecurityContext), never
 * from client input - this prevents a student from reserving a book on
 * someone else's behalf by tampering with the request body.
 */
@Getter
@Setter
public class ReservationRequest {

    @NotNull(message = "bookId is required")
    private Long bookId;
}
