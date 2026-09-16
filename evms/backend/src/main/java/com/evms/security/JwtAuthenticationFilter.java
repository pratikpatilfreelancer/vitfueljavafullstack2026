package com.evms.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter that intercepts every HTTP request.
 * <p>
 * Extracts the JWT token from the {@code Authorization: Bearer xxx} header,
 * validates it, and sets the authenticated user in Spring Security's
 * {@link SecurityContextHolder}. This replaces the Express {@code authenticate}
 * middleware from the original Node.js backend.
 * <p>
 * If no token is present or the token is invalid, the filter simply lets the
 * request pass through — Spring Security's authorization rules will then
 * return 401 for protected endpoints.
 *
 * @author EVMS Team
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** JWT utility for token validation and claim extraction. */
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Constructs the filter with the JWT token provider dependency.
     *
     * @param jwtTokenProvider the JWT utility component
     */
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Filters each request, extracting and validating the JWT token.
     * <p>
     * If a valid token is found, constructs a {@link UserPrincipal} from the
     * token claims and sets it in the security context. This makes the
     * authenticated user available to controllers via
     * {@code SecurityContextHolder.getContext().getAuthentication().getPrincipal()}.
     *
     * @param request     the HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain to continue processing
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Extract the token from the Authorization header
        String token = extractTokenFromRequest(request);

        // Validate the token and set the security context
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            // Build a UserPrincipal from the JWT claims
            UserPrincipal userPrincipal = new UserPrincipal(
                    jwtTokenProvider.getUserIdFromToken(token),
                    jwtTokenProvider.getNameFromToken(token),
                    jwtTokenProvider.getEmailFromToken(token),
                    jwtTokenProvider.getRoleFromToken(token),
                    jwtTokenProvider.getDepartmentFromToken(token)
            );

            // Create an authentication token with the user's authorities
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userPrincipal,
                            null,
                            userPrincipal.getAuthorities());

            // Set the authenticated user in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Continue the filter chain regardless of authentication result
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the {@code Authorization} header.
     * <p>
     * Expected format: {@code Authorization: Bearer <token>}
     *
     * @param request the HTTP request
     * @return the token string, or null if not present
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
