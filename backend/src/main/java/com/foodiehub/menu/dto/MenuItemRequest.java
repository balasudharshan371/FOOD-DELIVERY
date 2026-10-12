package com.foodiehub.menu.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemRequest {

    @NotBlank(message = "Restaurant ID is required")
    private String restaurantId;

    @NotBlank(message = "Category ID is required")
    private String categoryId;

    @NotBlank(message = "Dish name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be strictly greater than zero")
    private BigDecimal price;

    private String imageUrl;

    private String foodType; // VEG, NON_VEG, VEGAN

    private Boolean isAvailable;

    private Integer preparationTimeMinutes;
}
