package com.library.libraryservice.client;

import com.library.libraryservice.dto.LoginPayload;
import com.library.libraryservice.dto.UserInfoResponse;
import com.library.libraryservice.exception.RemoteServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * The Library Service's only integration point with the User Service.
 *
 * The Library Service does not keep a users table of its own (avoiding duplicated,
 * denormalized user data across services), so every incoming request's Basic Auth
 * credentials are verified here by calling the User Service's POST /api/users/login
 * endpoint over plain REST. This is the concrete "microservice-to-microservice
 * communication" in this project.
 */
@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final RestTemplate restTemplate;

    @Value("${user-service.base-url}")
    private String userServiceBaseUrl;

    public Optional<UserInfoResponse> authenticate(String email, String password) {
        try {
            UserInfoResponse response = restTemplate.postForObject(
                    userServiceBaseUrl + "/api/users/login",
                    new LoginPayload(email, password),
                    UserInfoResponse.class);
            return Optional.ofNullable(response);
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode() == HttpStatusCode.valueOf(401)) {
                return Optional.empty();
            }
            throw new RemoteServiceException("User Service rejected the authentication request: " + ex.getMessage());
        } catch (RestClientException ex) {
            throw new RemoteServiceException("Unable to reach User Service for authentication: " + ex.getMessage());
        }
    }
}
