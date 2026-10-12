package com.foodiehub.review;

import com.foodiehub.common.ApiResponse;
import com.foodiehub.review.dto.ReviewRequest;
import com.foodiehub.review.dto.ReviewResponse;
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
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<ReviewResponse>> addReview(
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ReviewResponse response = reviewService.addReview(request, userPrincipal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review submitted successfully", response));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getRestaurantReviews(
            @PathVariable String restaurantId) {
        List<ReviewResponse> list = reviewService.getRestaurantReviews(restaurantId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
