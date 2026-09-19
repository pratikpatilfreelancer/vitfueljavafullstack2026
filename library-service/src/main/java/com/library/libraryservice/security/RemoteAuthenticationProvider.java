package com.library.libraryservice.security;

import com.library.libraryservice.client.UserServiceClient;
import com.library.libraryservice.dto.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Authenticates every incoming HTTP Basic request by delegating the
 * email/password check to the User Service instead of a local UserDetailsService.
 * On success, the resolved user's id/name/role are stashed on the Authentication's
 * "details" so downstream controllers/services can use them.
 */
@Component
@RequiredArgsConstructor
public class RemoteAuthenticationProvider implements AuthenticationProvider {

    private final UserServiceClient userServiceClient;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        Optional<UserInfoResponse> userInfo = userServiceClient.authenticate(email, password);
        UserInfoResponse user = userInfo.orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
        UsernamePasswordAuthenticationToken authenticated =
                new UsernamePasswordAuthenticationToken(email, null, authorities);
        authenticated.setDetails(new AuthenticatedUser(user.getUserId(), user.getName(), user.getEmail(), user.getRole()));
        return authenticated;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
