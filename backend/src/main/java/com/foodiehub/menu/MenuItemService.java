package com.foodiehub.menu;

import com.foodiehub.exception.ResourceNotFoundException;
import com.foodiehub.menu.dto.MenuItemRequest;
import com.foodiehub.menu.dto.MenuItemResponse;
import com.foodiehub.restaurant.Restaurant;
import com.foodiehub.restaurant.RestaurantService;
import com.foodiehub.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantService restaurantService;

    public List<MenuItemResponse> getItemsByRestaurant(String restaurantId, boolean onlyAvailable) {
        List<MenuItem> items = onlyAvailable
                ? menuItemRepository.findByRestaurantIdAndIsAvailableTrue(restaurantId)
                : menuItemRepository.findByRestaurantId(restaurantId);

        return items.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public MenuItemResponse getItemById(String id) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", id));
        return mapToResponse(item);
    }

    public MenuItem getItemEntity(String id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", id));
    }

    public MenuItemResponse createMenuItem(MenuItemRequest request, UserPrincipal currentUser) {
        Restaurant restaurant = restaurantService.getRestaurantEntity(request.getRestaurantId());
        restaurantService.verifyOwnershipOrAdmin(restaurant, currentUser);

        MenuItem item = MenuItem.builder()
                .restaurantId(request.getRestaurantId())
                .categoryId(request.getCategoryId())
                .name(request.getName().trim())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .foodType(request.getFoodType() != null ? request.getFoodType() : "VEG")
                .isAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true)
                .preparationTimeMinutes(request.getPreparationTimeMinutes() != null ? request.getPreparationTimeMinutes() : 20)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        return mapToResponse(menuItemRepository.save(item));
    }

    public MenuItemResponse updateMenuItem(String id, MenuItemRequest request, UserPrincipal currentUser) {
        MenuItem item = getItemEntity(id);
        Restaurant restaurant = restaurantService.getRestaurantEntity(item.getRestaurantId());
        restaurantService.verifyOwnershipOrAdmin(restaurant, currentUser);

        item.setName(request.getName().trim());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setCategoryId(request.getCategoryId());
        if (request.getImageUrl() != null) item.setImageUrl(request.getImageUrl());
        if (request.getFoodType() != null) item.setFoodType(request.getFoodType());
        if (request.getIsAvailable() != null) item.setIsAvailable(request.getIsAvailable());
        if (request.getPreparationTimeMinutes() != null) item.setPreparationTimeMinutes(request.getPreparationTimeMinutes());
        item.setUpdatedAt(Instant.now());

        return mapToResponse(menuItemRepository.save(item));
    }

    public MenuItemResponse toggleAvailability(String id, UserPrincipal currentUser) {
        MenuItem item = getItemEntity(id);
        Restaurant restaurant = restaurantService.getRestaurantEntity(item.getRestaurantId());
        restaurantService.verifyOwnershipOrAdmin(restaurant, currentUser);

        item.setIsAvailable(!item.getIsAvailable());
        item.setUpdatedAt(Instant.now());
        return mapToResponse(menuItemRepository.save(item));
    }

    public void deleteMenuItem(String id, UserPrincipal currentUser) {
        MenuItem item = getItemEntity(id);
        Restaurant restaurant = restaurantService.getRestaurantEntity(item.getRestaurantId());
        restaurantService.verifyOwnershipOrAdmin(restaurant, currentUser);

        menuItemRepository.delete(item);
    }

    private MenuItemResponse mapToResponse(MenuItem i) {
        return MenuItemResponse.builder()
                .id(i.getId())
                .restaurantId(i.getRestaurantId())
                .categoryId(i.getCategoryId())
                .name(i.getName())
                .description(i.getDescription())
                .price(i.getPrice())
                .imageUrl(i.getImageUrl())
                .foodType(i.getFoodType())
                .isAvailable(i.getIsAvailable())
                .preparationTimeMinutes(i.getPreparationTimeMinutes())
                .createdAt(i.getCreatedAt())
                .build();
    }
}
