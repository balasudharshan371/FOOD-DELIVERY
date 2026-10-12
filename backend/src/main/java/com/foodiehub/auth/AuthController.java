package com.foodiehub.auth;

import com.foodiehub.auth.dto.AuthResponse;
import com.foodiehub.auth.dto.LoginRequest;
import com.foodiehub.auth.dto.RegisterRequest;
import com.foodiehub.common.ApiResponse;
import com.foodiehub.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse.UserSummary>> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        AuthResponse.UserSummary summary = AuthResponse.UserSummary.builder()
                .id(userPrincipal.getId())
                .fullName(userPrincipal.getFullName())
                .email(userPrincipal.getEmail())
                .role(userPrincipal.getRole())
                .accountStatus(userPrincipal.getAccountStatus())
                .build();

        return ResponseEntity.ok(ApiResponse.success("Current user profile retrieved", summary));
    }
}
