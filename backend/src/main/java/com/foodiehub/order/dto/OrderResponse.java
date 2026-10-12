package com.foodiehub.order.dto;

import com.foodiehub.order.OrderItemSnapshot;
import com.foodiehub.order.PricingBreakdown;
import com.foodiehub.order.StatusLog;
import com.foodiehub.user.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private String id;
    private String orderNumber;
    private String customerId;
    private String customerName;
    private String customerEmail;
    private String restaurantId;
    private String restaurantName;
    private List<OrderItemSnapshot> items;
    private Address deliveryAddressSnapshot;
    private PricingBreakdown pricingBreakdown;
    private String couponCode;
    private String paymentMethod;
    private String paymentStatus;
    private String transactionId;
    private String orderStatus;
    private String cancellationReason;
    private List<StatusLog> timeline;
    private Instant createdAt;
}
