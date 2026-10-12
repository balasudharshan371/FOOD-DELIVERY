package com.foodiehub.restaurant;

import com.foodiehub.common.ApiResponse;
import com.foodiehub.common.PagedResponse;
import com.foodiehub.restaurant.dto.RestaurantRequest;
import com.foodiehub.restaurant.dto.RestaurantResponse;
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
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping("/api/restaurants")
    public ResponseEntity<ApiResponse<PagedResponse<RestaurantResponse>>> getRestaurants(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "averageRating") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        PagedResponse<RestaurantResponse> response = restaurantService.getRestaurants(
                search, cuisine, city, page, size, sortBy, direction);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/api/restaurants/{id}")
    public ResponseEntity<ApiResponse<RestaurantResponse>> getRestaurantById(@PathVariable String id) {
        RestaurantResponse response = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/api/owner/restaurant/my")
    @PreAuthorize("hasAnyAuthority('ROLE_RESTAURANT_OWNER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<RestaurantResponse>> getMyRestaurant(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        RestaurantResponse response = restaurantService.getMyRestaurant(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/api/restaurants")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<RestaurantResponse>> createRestaurant(
            @Valid @RequestBody RestaurantRequest request) {
        RestaurantResponse response = restaurantService.createRestaurant(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Restaurant created successfully", response));
    }

    @PutMapping("/api/restaurants/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_RESTAURANT_OWNER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<RestaurantResponse>> updateRestaurant(
            @PathVariable String id,
            @Valid @RequestBody RestaurantRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        RestaurantResponse response = restaurantService.updateRestaurant(id, request, userPrincipal);
        return ResponseEntity.ok(ApiResponse.success("Restaurant updated successfully", response));
    }

    @PatchMapping("/api/restaurants/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_RESTAURANT_OWNER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<RestaurantResponse>> toggleRestaurantStatus(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        RestaurantResponse response = restaurantService.toggleRestaurantStatus(id, userPrincipal);
        return ResponseEntity.ok(ApiResponse.success("Restaurant operational status updated", response));
    }

    @GetMapping("/api/admin/restaurants")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<RestaurantResponse>>> getAllRestaurantsAdmin() {
        List<RestaurantResponse> list = restaurantService.getAllRestaurantsAdmin();
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
