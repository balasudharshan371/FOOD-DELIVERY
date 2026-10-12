package com.foodiehub.cart.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddToCartRequest {

    @NotBlank(message = "Menu item ID is required")
    private String menuItemId;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 20, message = "Maximum quantity per item is 20")
    @Builder.Default
    private int quantity = 1;
}
