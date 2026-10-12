package com.foodiehub.cart.dto;

import com.foodiehub.cart.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private String id;
    private String userId;
    private String restaurantId;
    private String restaurantName;
    private List<CartItem> items;
    private BigDecimal subtotal;
    private BigDecimal estimatedTax;
    private BigDecimal deliveryFee;
    private BigDecimal estimatedTotal;
    private int totalItemCount;
}
