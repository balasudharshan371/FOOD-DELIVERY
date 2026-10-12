package com.foodiehub.order.dto;

import com.foodiehub.user.Address;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotNull(message = "Delivery address snapshot is required")
    private Address deliveryAddress;

    private String couponCode;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // "CASH_ON_DELIVERY" or "SIMULATED_ONLINE"
}
