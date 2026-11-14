package com.citypulse.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * ============================================================
 * CITYPULSE JWT AUTHENTICATION FILTER
 * ============================================================
 *
 * Runs once per HTTP request. It:
 *
 *   1. Checks for "Authorization: Bearer <token>" header.
 *   2. Validates the JWT using JwtUtil.
 *   3. Extracts the user email (subject).
 *   4. Loads the UserDetails via CustomUserDetailsService.
 *   5. Sets the authentication in Spring Security's
 *      SecurityContextHolder.
 *
 * If no Bearer header is present, the filter is a no-op —
 * meaning Basic Auth (email + password) continues to work
 * exactly as before. The two auth mechanisms coexist.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthFilter(
            JwtUtil jwtUtil,
            CustomUserDetailsService customUserDetailsService) {

        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        /* Not a Bearer token → skip. Basic Auth continues
         * to work for old clients. */
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7).trim();

        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        /* If the token is invalid or expired → skip silently.
         * Spring Security's auth chain will then respond with
         * 401 Unauthorized for protected endpoints. */
        if (!jwtUtil.isValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            String email = jwtUtil.extractEmail(token);

            /* Only authenticate if we don't already have an
             * authenticated principal from an earlier filter. */
            if (email != null &&
                    SecurityContextHolder.getContext()
                            .getAuthentication() == null) {

                UserDetails userDetails =
                        customUserDetailsService
                                .loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            }

        } catch (UsernameNotFoundException ignored) {
            /* User doesn't exist anymore → token is stale. */
        } catch (RuntimeException ignored) {
            /* Any other JWT/security issue → 401 later. */
        }

        filterChain.doFilter(request, response);
    }
}