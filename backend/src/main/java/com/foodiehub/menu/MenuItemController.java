package com.foodiehub.menu;

import com.foodiehub.common.ApiResponse;
import com.foodiehub.menu.dto.MenuItemRequest;
import com.foodiehub.menu.dto.MenuItemResponse;
import com.foodiehub.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu-items")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ApiResponse<List<MenuItemResponse>>> getItemsByRestaurant(
            @PathVariable String restaurantId,
            @RequestParam(defaultValue = "true") boolean availableOnly) {
        List<MenuItemResponse> items = menuItemService.getItemsByRestaurant(restaurantId, availableOnly);
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuItemResponse>> getItemById(@PathVariable String id) {
        MenuItemResponse item = menuItemService.getItemById(id);
        return ResponseEntity.ok(ApiResponse.success(item));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_RESTAURANT_OWNER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<MenuItemResponse>> createMenuItem(
            @Valid @RequestBody MenuItemRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        MenuItemResponse response = menuItemService.createMenuItem(request, userPrincipal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Menu item created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_RESTAURANT_OWNER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<MenuItemResponse>> updateMenuItem(
            @PathVariable String id,
            @Valid @RequestBody MenuItemRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        MenuItemResponse response = menuItemService.updateMenuItem(id, request, userPrincipal);
        return ResponseEntity.ok(ApiResponse.success("Menu item updated successfully", response));
    }

    @PatchMapping("/{id}/availability")
    @PreAuthorize("hasAnyAuthority('ROLE_RESTAURANT_OWNER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<MenuItemResponse>> toggleAvailability(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        MenuItemResponse response = menuItemService.toggleAvailability(id, userPrincipal);
        return ResponseEntity.ok(ApiResponse.success("Availability updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_RESTAURANT_OWNER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteMenuItem(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        menuItemService.deleteMenuItem(id, userPrincipal);
        return ResponseEntity.ok(ApiResponse.success("Menu item removed successfully", null));
    }
}
