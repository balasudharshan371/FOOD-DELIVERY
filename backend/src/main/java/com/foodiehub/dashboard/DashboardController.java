package com.foodiehub.dashboard;

import com.foodiehub.common.ApiResponse;
import com.foodiehub.dashboard.dto.AdminDashboardResponse;
import com.foodiehub.dashboard.dto.OwnerDashboardResponse;
import com.foodiehub.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/api/admin/dashboard")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getAdminDashboard() {
        AdminDashboardResponse response = dashboardService.getAdminDashboard();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/api/owner/dashboard")
    @PreAuthorize("hasAnyAuthority('ROLE_RESTAURANT_OWNER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<OwnerDashboardResponse>> getOwnerDashboard(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        OwnerDashboardResponse response = dashboardService.getOwnerDashboard(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
