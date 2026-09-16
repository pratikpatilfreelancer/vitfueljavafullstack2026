package com.library.libraryservice.controller;

import com.library.libraryservice.dto.BorrowingRequest;
import com.library.libraryservice.dto.BorrowingResponse;
import com.library.libraryservice.service.BorrowingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Issuing and returning books. Both operations are LIBRARIAN-only (enforced in SecurityConfig).
 */
@RestController
@RequestMapping("/api/borrowings")
@RequiredArgsConstructor
public class BorrowingController {

    private final BorrowingService borrowingService;

    @PostMapping("/issue")
    public ResponseEntity<BorrowingResponse> issueBook(@Valid @RequestBody BorrowingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(borrowingService.issueBook(request.getReservationId()));
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<BorrowingResponse> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(borrowingService.returnBook(id));
    }

    @GetMapping
    public ResponseEntity<List<BorrowingResponse>> getAllBorrowings() {
        return ResponseEntity.ok(borrowingService.getAllBorrowings());
    }
}
