package vanguard.web;

/** Uniform JSON error shape returned by GlobalExceptionHandler. */
public class ErrorResponse {
    private final String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() { return error; }
}
