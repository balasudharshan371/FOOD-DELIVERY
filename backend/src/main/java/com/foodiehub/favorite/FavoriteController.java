package com.foodiehub.favorite;

import com.foodiehub.common.ApiResponse;
import com.foodiehub.restaurant.dto.RestaurantResponse;
import com.foodiehub.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RestaurantResponse>>> getMyFavorites(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<RestaurantResponse> list = favoriteService.getMyFavorites(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping("/{restaurantId}")
    public ResponseEntity<ApiResponse<Void>> addFavorite(
            @PathVariable String restaurantId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        favoriteService.addFavorite(userPrincipal.getId(), restaurantId);
        return ResponseEntity.ok(ApiResponse.success("Added to favorites", null));
    }

    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<ApiResponse<Void>> removeFavorite(
            @PathVariable String restaurantId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        favoriteService.removeFavorite(userPrincipal.getId(), restaurantId);
        return ResponseEntity.ok(ApiResponse.success("Removed from favorites", null));
    }
}
