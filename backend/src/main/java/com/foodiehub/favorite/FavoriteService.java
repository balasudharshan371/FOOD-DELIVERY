package com.foodiehub.favorite;

import com.foodiehub.restaurant.Restaurant;
import com.foodiehub.restaurant.RestaurantRepository;
import com.foodiehub.restaurant.dto.RestaurantResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final RestaurantRepository restaurantRepository;

    public void addFavorite(String userId, String restaurantId) {
        if (!favoriteRepository.existsByUserIdAndRestaurantId(userId, restaurantId)) {
            Favorite favorite = Favorite.builder()
                    .userId(userId)
                    .restaurantId(restaurantId)
                    .createdAt(Instant.now())
                    .build();
            favoriteRepository.save(favorite);
        }
    }

    public void removeFavorite(String userId, String restaurantId) {
        favoriteRepository.deleteByUserIdAndRestaurantId(userId, restaurantId);
    }

    public List<RestaurantResponse> getMyFavorites(String userId) {
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);
        List<String> resIds = favorites.stream().map(Favorite::getRestaurantId).collect(Collectors.toList());

        List<Restaurant> restaurants = restaurantRepository.findAllById(resIds);

        return restaurants.stream().map(r -> RestaurantResponse.builder()
                .id(r.getId())
                .name(r.getName())
                .description(r.getDescription())
                .cuisineTypes(r.getCuisineTypes())
                .address(r.getAddress())
                .city(r.getCity())
                .phone(r.getPhone())
                .imageUrl(r.getImageUrl())
                .averageRating(r.getAverageRating())
                .totalReviews(r.getTotalReviews())
                .deliveryFee(r.getDeliveryFee())
                .estimatedDeliveryMinutes(r.getEstimatedDeliveryMinutes())
                .isActive(r.getIsActive())
                .build()).collect(Collectors.toList());
    }

    public boolean isFavorite(String userId, String restaurantId) {
        return favoriteRepository.existsByUserIdAndRestaurantId(userId, restaurantId);
    }
}
