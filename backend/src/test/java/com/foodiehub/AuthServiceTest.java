package com.foodiehub.auth;

import com.foodiehub.auth.dto.AuthResponse;
import com.foodiehub.auth.dto.LoginRequest;
import com.foodiehub.auth.dto.RegisterRequest;
import com.foodiehub.exception.BadRequestException;
import com.foodiehub.security.JwtTokenProvider;
import com.foodiehub.user.User;
import com.foodiehub.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "jwtExpirationInMs", 86400000L);
    }

    @Test
    void register_Success() {
        RegisterRequest request = RegisterRequest.builder()
                .fullName("John Doe")
                .email("john@example.com")
                .password("Password@123")
                .phone("9876543210")
                .role("ROLE_CUSTOMER")
                .build();

        User savedUser = User.builder()
                .id("u1")
                .fullName("John Doe")
                .email("john@example.com")
                .passwordHash("hashed")
                .phone("9876543210")
                .role("ROLE_CUSTOMER")
                .accountStatus("ACTIVE")
                .addresses(new ArrayList<>())
                .createdAt(Instant.now())
                .build();

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password@123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(mock(Authentication.class));
        when(tokenProvider.generateToken(any())).thenReturn("mock-jwt-token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getAccessToken());
        assertEquals("john@example.com", response.getUser().getEmail());
    }

    @Test
    void register_DuplicateEmail_ThrowsBadRequest() {
        RegisterRequest request = RegisterRequest.builder()
                .email("existing@example.com")
                .fullName("Test")
                .password("pass123")
                .phone("1234567890")
                .build();

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(request));
    }

    @Test
    void login_Success() {
        LoginRequest request = LoginRequest.builder()
                .email("john@example.com")
                .password("Password@123")
                .build();

        User user = User.builder()
                .id("u1")
                .fullName("John Doe")
                .email("john@example.com")
                .role("ROLE_CUSTOMER")
                .accountStatus("ACTIVE")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mock(Authentication.class));
        when(tokenProvider.generateToken(any())).thenReturn("mock-jwt-token");
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getAccessToken());
        assertEquals("John Doe", response.getUser().getFullName());
    }
}
