package com.foodiehub.review;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByRestaurantIdOrderByCreatedAtDesc(String restaurantId);
    Optional<Review> findByUserIdAndOrderId(String userId, String orderId);
    Boolean existsByUserIdAndOrderId(String userId, String orderId);
}
