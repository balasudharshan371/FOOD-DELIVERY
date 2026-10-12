package com.foodiehub.favorite;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends MongoRepository<Favorite, String> {
    List<Favorite> findByUserId(String userId);
    Optional<Favorite> findByUserIdAndRestaurantId(String userId, String restaurantId);
    Boolean existsByUserIdAndRestaurantId(String userId, String restaurantId);
    void deleteByUserIdAndRestaurantId(String userId, String restaurantId);
}
