package com.foodiehub.cart.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartItemRequest {
    @Min(value = 0, message = "Quantity cannot be negative")
    @Max(value = 20, message = "Maximum quantity per item is 20")
    private int quantity;
}
