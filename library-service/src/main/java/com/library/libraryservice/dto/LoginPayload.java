package com.library.libraryservice.dto;

/** Outgoing request body used when this service calls the User Service's /login endpoint. */
public record LoginPayload(String email, String password) {
}
