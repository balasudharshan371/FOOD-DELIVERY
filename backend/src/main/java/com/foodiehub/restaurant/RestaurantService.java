package com.foodiehub.restaurant;

import com.foodiehub.common.PagedResponse;
import com.foodiehub.exception.ForbiddenException;
import com.foodiehub.exception.ResourceNotFoundException;
import com.foodiehub.restaurant.dto.RestaurantRequest;
import com.foodiehub.restaurant.dto.RestaurantResponse;
import com.foodiehub.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public PagedResponse<RestaurantResponse> getRestaurants(
            String search, String cuisine, String city,
            int page, int size, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Restaurant> restaurantsPage;
        if ((search != null && !search.isBlank()) || (cuisine != null && !cuisine.isBlank()) || (city != null && !city.isBlank())) {
            String searchPattern = (search != null && !search.isBlank()) ? search.trim() : ".*";
            String cuisineFilter = (cuisine != null && !cuisine.isBlank() && !cuisine.equalsIgnoreCase("ALL")) ? cuisine.trim() : null;
            String cityFilter = (city != null && !city.isBlank() && !city.equalsIgnoreCase("ALL")) ? city.trim() : null;
            restaurantsPage = restaurantRepository.searchRestaurants(searchPattern, cuisineFilter, cityFilter, pageable);
        } else {
            restaurantsPage = restaurantRepository.findByIsActiveTrue(pageable);
        }

        List<RestaurantResponse> content = restaurantsPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PagedResponse.<RestaurantResponse>builder()
                .content(content)
                .pageNumber(restaurantsPage.getNumber())
                .pageSize(restaurantsPage.getSize())
                .totalElements(restaurantsPage.getTotalElements())
                .totalPages(restaurantsPage.getTotalPages())
                .last(restaurantsPage.isLast())
                .build();
    }

    public RestaurantResponse getRestaurantById(String id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", id));
        return mapToResponse(restaurant);
    }

    public Restaurant getRestaurantEntity(String id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", id));
    }

    public Restaurant getRestaurantEntityByOwner(String ownerId) {
        return restaurantRepository.findFirstByOwnerId(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("No restaurant assigned to owner ID: " + ownerId));
    }

    public RestaurantResponse getMyRestaurant(String ownerId) {
        Restaurant restaurant = restaurantRepository.findFirstByOwnerId(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("No restaurant assigned to owner ID: " + ownerId));
        return mapToResponse(restaurant);
    }

    public RestaurantResponse createRestaurant(RestaurantRequest request) {
        Restaurant restaurant = Restaurant.builder()
                .name(request.getName().trim())
                .description(request.getDescription().trim())
                .cuisineTypes(request.getCuisineTypes())
                .address(request.getAddress().trim())
                .city(request.getCity().trim())
                .phone(request.getPhone().trim())
                .ownerId(request.getOwnerId())
                .imageUrl(request.getImageUrl())
                .deliveryFee(request.getDeliveryFee() != null ? request.getDeliveryFee() : new java.math.BigDecimal("40.00"))
                .estimatedDeliveryMinutes(request.getEstimatedDeliveryMinutes() != null ? request.getEstimatedDeliveryMinutes() : 30)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .averageRating(0.0)
                .totalReviews(0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        return mapToResponse(restaurantRepository.save(restaurant));
    }

    public RestaurantResponse updateRestaurant(String id, RestaurantRequest request, UserPrincipal currentUser) {
        Restaurant restaurant = getRestaurantEntity(id);
        verifyOwnershipOrAdmin(restaurant, currentUser);

        restaurant.setName(request.getName().trim());
        restaurant.setDescription(request.getDescription().trim());
        restaurant.setCuisineTypes(request.getCuisineTypes());
        restaurant.setAddress(request.getAddress().trim());
        restaurant.setCity(request.getCity().trim());
        restaurant.setPhone(request.getPhone().trim());
        if (request.getImageUrl() != null) restaurant.setImageUrl(request.getImageUrl());
        if (request.getDeliveryFee() != null) restaurant.setDeliveryFee(request.getDeliveryFee());
        if (request.getEstimatedDeliveryMinutes() != null) restaurant.setEstimatedDeliveryMinutes(request.getEstimatedDeliveryMinutes());
        if (request.getIsActive() != null) restaurant.setIsActive(request.getIsActive());
        restaurant.setUpdatedAt(Instant.now());

        return mapToResponse(restaurantRepository.save(restaurant));
    }

    public RestaurantResponse toggleRestaurantStatus(String id, UserPrincipal currentUser) {
        Restaurant restaurant = getRestaurantEntity(id);
        verifyOwnershipOrAdmin(restaurant, currentUser);

        restaurant.setIsActive(!restaurant.getIsActive());
        restaurant.setUpdatedAt(Instant.now());
        return mapToResponse(restaurantRepository.save(restaurant));
    }

    public void updateRating(String restaurantId, double newAverage, int newTotal) {
        Restaurant restaurant = getRestaurantEntity(restaurantId);
        restaurant.setAverageRating(Math.round(newAverage * 10.0) / 10.0);
        restaurant.setTotalReviews(newTotal);
        restaurant.setUpdatedAt(Instant.now());
        restaurantRepository.save(restaurant);
    }

    public void verifyOwnershipOrAdmin(Restaurant restaurant, UserPrincipal currentUser) {
        if ("ROLE_ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            return;
        }
        if (!restaurant.getOwnerId().equals(currentUser.getId())) {
            throw new ForbiddenException("You are not authorized to manage this restaurant");
        }
    }

    public List<RestaurantResponse> getAllRestaurantsAdmin() {
        return restaurantRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private RestaurantResponse mapToResponse(Restaurant r) {
        return RestaurantResponse.builder()
                .id(r.getId())
                .name(r.getName())
                .description(r.getDescription())
                .cuisineTypes(r.getCuisineTypes())
                .address(r.getAddress())
                .city(r.getCity())
                .phone(r.getPhone())
                .ownerId(r.getOwnerId())
                .imageUrl(r.getImageUrl())
                .averageRating(r.getAverageRating())
                .totalReviews(r.getTotalReviews())
                .deliveryFee(r.getDeliveryFee())
                .estimatedDeliveryMinutes(r.getEstimatedDeliveryMinutes())
                .isActive(r.getIsActive())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
