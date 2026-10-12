package com.foodiehub.user;

import com.foodiehub.common.ApiResponse;
import com.foodiehub.security.UserPrincipal;
import com.foodiehub.user.dto.AddressRequest;
import com.foodiehub.user.dto.UserProfileResponse;
import com.foodiehub.user.dto.UserStatusUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/users/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserProfileResponse profile = userService.getUserProfile(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PostMapping("/api/users/addresses")
    public ResponseEntity<ApiResponse<UserProfileResponse>> addAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody AddressRequest request) {
        UserProfileResponse profile = userService.addAddress(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Address added successfully", profile));
    }

    @DeleteMapping("/api/users/addresses/{addressId}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> deleteAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String addressId) {
        UserProfileResponse profile = userService.deleteAddress(userPrincipal.getId(), addressId);
        return ResponseEntity.ok(ApiResponse.success("Address removed successfully", profile));
    }

    @GetMapping("/api/admin/users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<UserProfileResponse>>> getAllUsers() {
        List<UserProfileResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PatchMapping("/api/admin/users/{userId}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateUserStatus(
            @PathVariable String userId,
            @Valid @RequestBody UserStatusUpdateRequest request) {
        UserProfileResponse updated = userService.updateUserStatus(userId, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success("User account status updated", updated));
    }
}
