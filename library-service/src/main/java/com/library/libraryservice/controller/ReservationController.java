package com.library.libraryservice.controller;

import com.library.libraryservice.dto.ReservationRequest;
import com.library.libraryservice.dto.ReservationResponse;
import com.library.libraryservice.security.AuthenticatedUser;
import com.library.libraryservice.security.SecurityUtils;
import com.library.libraryservice.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Reservation endpoints covering the student-facing (create/cancel/view own) and
 * librarian-facing (view all/approve) sides of the workflow. Role restrictions are
 * enforced in SecurityConfig; ownership checks live in ReservationService.
 */
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request,
                                                                   Authentication authentication) {
        AuthenticatedUser user = SecurityUtils.currentUser(authentication);
        ReservationResponse response = reservationService.createReservation(user.getUserId(), request.getBookId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        // Restricted to LIBRARIAN by SecurityConfig.
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationResponse>> getReservationsByUser(@PathVariable Long userId,
                                                                            Authentication authentication) {
        AuthenticatedUser user = SecurityUtils.currentUser(authentication);
        boolean isLibrarian = SecurityUtils.isLibrarian(authentication);
        return ResponseEntity.ok(reservationService.getReservationsByUser(userId, user.getUserId(), isLibrarian));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ReservationResponse> cancelReservation(@PathVariable Long id, Authentication authentication) {
        AuthenticatedUser user = SecurityUtils.currentUser(authentication);
        boolean isLibrarian = SecurityUtils.isLibrarian(authentication);
        return ResponseEntity.ok(reservationService.cancelReservation(id, user.getUserId(), isLibrarian));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ReservationResponse> approveReservation(@PathVariable Long id) {
        // Restricted to LIBRARIAN by SecurityConfig.
        return ResponseEntity.ok(reservationService.approveReservation(id));
    }
}
