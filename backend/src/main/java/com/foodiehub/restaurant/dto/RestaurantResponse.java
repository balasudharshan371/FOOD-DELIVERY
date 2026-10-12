package com.foodiehub.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponse {
    private String id;
    private String name;
    private String description;
    private List<String> cuisineTypes;
    private String address;
    private String city;
    private String phone;
    private String ownerId;
    private String imageUrl;
    private Double averageRating;
    private Integer totalReviews;
    private BigDecimal deliveryFee;
    private Integer estimatedDeliveryMinutes;
    private Boolean isActive;
    private Instant createdAt;
}
