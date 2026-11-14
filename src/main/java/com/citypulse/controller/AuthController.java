package com.citypulse.controller;

import com.citypulse.dto.LoginRequest;
import com.citypulse.dto.LoginResponse;
import com.citypulse.dto.UserRequest;
import com.citypulse.model.User;
import com.citypulse.security.JwtUtil;
import com.citypulse.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(
            UserService userService,
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil) {

        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody UserRequest request) {

        if (userService.emailExists(request.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Email already registered");
        }

        User user = new User();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(
                request.getRole() != null && !request.getRole().isBlank()
                        ? request.getRole()
                        : "USER"
        );
        user.setPhone(request.getPhone());
        user.setActive(
                request.getActive() != null
                        ? request.getActive()
                        : true
        );

        User savedUser = userService.createUser(user);

        /* Issue a JWT immediately so the user is logged in
         * automatically after registration. */
        String token = jwtUtil.generateToken(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole()
        );

        LoginResponse response = new LoginResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                token,
                "Registration successful"
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        User user = userService
                .getUserByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );

        LoginResponse response = new LoginResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                token,
                "Login successful"
        );

                return ResponseEntity.ok(response);
    }

    /* =========================================================
       CURRENT USER (used by frontend session restore)
       ========================================================= */

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {

        if (authentication == null
                || authentication.getName() == null
                || authentication.getName().isBlank()) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Not authenticated");
        }

        return userService
                .getUserByEmail(authentication.getName())
                .<ResponseEntity<?>>map(user -> {

                    java.util.Map<String, Object> profile =
                            new java.util.LinkedHashMap<>();

                    profile.put("id", user.getId());
                    profile.put("fullName", user.getFullName());
                    profile.put("email", user.getEmail());
                    profile.put("role", user.getRole());
                    profile.put("phone", user.getPhone());
                    profile.put("active", user.getActive());

                    return ResponseEntity.ok(profile);

                })
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("User not found"));
    }
}