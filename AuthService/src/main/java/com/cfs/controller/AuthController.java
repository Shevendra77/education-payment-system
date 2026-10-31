
package com.cfs.controller;

import com.cfs.dto.LoginRequest;
import com.cfs.dto.RegisterRequest;
import com.cfs.entity.User;
import com.cfs.security.JwtService;
import com.cfs.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(
            AuthService authService,
            JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request) {

        User user = authService.register(
                request.getName(),
                request.getEmail(),
                request.getPassword()
        );

        Map<String, Object> response = Map.of(
                "message", "User registered successfully",
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request) {

        User user = authService.login(
                request.getEmail(),
                request.getPassword()
        );

        String token = jwtService.generateToken(user.getEmail());

        Map<String, Object> response = Map.of(
                "message", "Login successful",
                "token", token,
                "tokenType", "Bearer",
                "email", user.getEmail()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
        org.springframework.security.core.Authentication authentication) {

    return ResponseEntity.ok(
            Map.of(
                    "message", "Authentication successful",
                    "email", authentication.getName()
            )
    );
}
}
