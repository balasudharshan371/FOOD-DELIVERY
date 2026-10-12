package com.foodiehub.review;

import com.foodiehub.exception.BadRequestException;
import com.foodiehub.order.Order;
import com.foodiehub.order.OrderService;
import com.foodiehub.restaurant.RestaurantService;
import com.foodiehub.review.dto.ReviewRequest;
import com.foodiehub.review.dto.ReviewResponse;
import com.foodiehub.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderService orderService;
    private final RestaurantService restaurantService;

    public ReviewResponse addReview(ReviewRequest request, UserPrincipal currentUser) {
        Order order = orderService.getOrderEntity(request.getOrderId());

        if (!order.getCustomerId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only review orders that you placed");
        }

        if (!"DELIVERED".equalsIgnoreCase(order.getOrderStatus())) {
            throw new BadRequestException("Reviews can only be submitted for DELIVERED orders. Current status: " + order.getOrderStatus());
        }

        if (reviewRepository.existsByUserIdAndOrderId(currentUser.getId(), request.getOrderId())) {
            throw new BadRequestException("You have already reviewed this order");
        }

        Review review = Review.builder()
                .userId(currentUser.getId())
                .userName(currentUser.getFullName())
                .restaurantId(order.getRestaurantId())
                .orderId(order.getId())
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(Instant.now())
                .build();

        Review saved = reviewRepository.save(review);

        // Recalculate restaurant rating
        List<Review> allReviews = reviewRepository.findByRestaurantIdOrderByCreatedAtDesc(order.getRestaurantId());
        double avg = allReviews.stream().mapToInt(Review::getRating).average().orElse(5.0);
        restaurantService.updateRating(order.getRestaurantId(), avg, allReviews.size());

        return mapToResponse(saved);
    }

    public List<ReviewResponse> getRestaurantReviews(String restaurantId) {
        return reviewRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ReviewResponse mapToResponse(Review r) {
        return ReviewResponse.builder()
                .id(r.getId())
                .userId(r.getUserId())
                .userName(r.getUserName())
                .restaurantId(r.getRestaurantId())
                .orderId(r.getOrderId())
                .rating(r.getRating())
                .comment(r.getComment())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
