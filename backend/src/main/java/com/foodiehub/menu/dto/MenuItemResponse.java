package com.foodiehub.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemResponse {
    private String id;
    private String restaurantId;
    private String categoryId;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private String foodType;
    private Boolean isAvailable;
    private Integer preparationTimeMinutes;
    private Instant createdAt;
}
