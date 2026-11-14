package com.citypulse.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * ============================================================
 * CITYPULSE JWT UTILITY
 * ============================================================
 *
 * Generates, validates, and reads JSON Web Tokens (JWT).
 *
 * ALGORITHM: HMAC-SHA256 (HS256).
 *
 * Token claims:
 *   - subject: user email (login identifier)
 *   - userId:  database ID
 *   - role:    USER / ADMIN / etc.
 *   - iat:     issued-at timestamp
 *   - exp:     expiry timestamp
 */
@Component
public class JwtUtil {

    /** Signing key (bytes). Minimum 32 bytes for HS256. */
    private final SecretKey signingKey;

    /** Token validity in milliseconds. Default: 24 hours. */
    private final long expirationMs;

    public JwtUtil(
            @Value("${citypulse.jwt.secret:change-me-in-production-please-use-a-very-long-secret-key-for-citypulse}") String secret,
            @Value("${citypulse.jwt.expiration-ms:86400000}") long expirationMs
    ) {

        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException(
                    "citypulse.jwt.secret must be at least 32 bytes for HS256"
            );
        }

        this.signingKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
        this.expirationMs = expirationMs;
    }

    /**
     * Generates a JWT for the given user.
     *
     * @param userId user's database ID
     * @param email  user's email (used as subject)
     * @param role   user's role (e.g. "USER", "ADMIN")
     * @return a signed JWT string
     */
    public String generateToken(long userId, String email, String role) {

        Instant now = Instant.now();
        Instant expiry = now.plusMillis(expirationMs);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(signingKey)
                .compact();
    }

    /**
     * Returns true if the token is well-formed, correctly
     * signed, and not expired.
     */
    public boolean isValid(String token) {

        try {
            Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Extracts the email (subject) from a valid token.
     */
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Extracts the userId claim from a valid token.
     */
    public Long extractUserId(String token) {
        Object raw = parseClaims(token).get("userId");
        if (raw instanceof Number n) {
            return n.longValue();
        }
        return null;
    }

    /**
     * Extracts the role claim from a valid token.
     */
    public String extractRole(String token) {
        Object raw = parseClaims(token).get("role");
        return raw != null ? raw.toString() : "USER";
    }

    /**
     * Returns the token's expiry time in milliseconds since epoch.
     */
    public long extractExpiry(String token) {
        Date exp = parseClaims(token).getExpiration();
        return exp != null ? exp.getTime() : 0L;
    }

    /**
     * Returns the number of seconds until the token expires.
     * Negative means already expired.
     */
    public long extractSecondsRemaining(String token) {
        long expiry = extractExpiry(token);
        return (expiry - System.currentTimeMillis()) / 1000L;
    }

    private Claims parseClaims(String token) {

        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}