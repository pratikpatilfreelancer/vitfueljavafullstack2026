package com.evms.exception;

import com.evms.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * Global exception handler for the EVMS REST API.
 * <p>
 * Catches all application exceptions and converts them into consistent
 * JSON error responses in the format: {@code { "error": "message" }}.
 * This matches the exact error response shape the React frontend expects.
 * <p>
 * HTTP status code mapping:
 * <ul>
 *   <li>400 — {@link BadRequestException}, validation errors</li>
 *   <li>401 — Authentication errors (handled by SecurityConfig)</li>
 *   <li>403 — {@link AccessDeniedException}</li>
 *   <li>404 — {@link ResourceNotFoundException}</li>
 *   <li>409 — {@link InvalidStateException}</li>
 *   <li>500 — All unhandled exceptions</li>
 * </ul>
 *
 * @author EVMS Team
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles 400 Bad Request exceptions (validation failures).
     *
     * @param ex the BadRequestException
     * @return JSON error response with HTTP 400
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }

    /**
     * Handles 403 Forbidden exceptions (authorization failures).
     *
     * @param ex the AccessDeniedException
     * @return JSON error response with HTTP 403
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(ex.getMessage()));
    }

    /**
     * Handles 404 Not Found exceptions (missing resources).
     *
     * @param ex the ResourceNotFoundException
     * @return JSON error response with HTTP 404
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    /**
     * Handles 409 Conflict exceptions (invalid state transitions).
     *
     * @param ex the InvalidStateException
     * @return JSON error response with HTTP 409
     */
    @ExceptionHandler(InvalidStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidState(InvalidStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage()));
    }

    /**
     * Handles Bean Validation errors (e.g., @NotBlank, @Email).
     * Extracts the first validation error message for the response.
     *
     * @param ex the MethodArgumentNotValidException
     * @return JSON error response with HTTP 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message));
    }

    /**
     * Handles file upload size exceeded errors.
     * Replaces the Multer file size limit error from the Node.js backend.
     *
     * @param ex the MaxUploadSizeExceededException
     * @return JSON error response with HTTP 400
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleFileTooLarge(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("File too large. Maximum size is 2MB."));
    }

    /**
     * Catch-all handler for any unhandled exceptions.
     * Returns a generic 500 error to avoid leaking internal details.
     *
     * @param ex the unhandled exception
     * @return JSON error response with HTTP 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        ex.printStackTrace(); // Log for debugging
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Something went wrong."));
    }
}
