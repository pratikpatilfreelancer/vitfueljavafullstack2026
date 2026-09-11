package vanguard.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vanguard.exceptions.IncidentNotFoundException;
import vanguard.exceptions.InvalidReportException;
import vanguard.exceptions.NoAvailableResourceException;

/**
 * Converts VANGUARD's custom exceptions into proper HTTP status codes
 * and JSON error bodies, in one place, instead of every controller
 * method needing its own try/catch.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IncidentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(IncidentNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(NoAvailableResourceException.class)
    public ResponseEntity<ErrorResponse> handleNoResource(NoAvailableResourceException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(InvalidReportException.class)
    public ResponseEntity<ErrorResponse> handleInvalidReport(InvalidReportException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
    }
}
