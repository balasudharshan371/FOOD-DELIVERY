package com.foodiehub.order;

import com.foodiehub.user.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
@CompoundIndex(name = "res_status_idx", def = "{'restaurantId': 1, 'orderStatus': 1}")
@CompoundIndex(name = "cust_created_idx", def = "{'customerId': 1, 'createdAt': -1}")
public class Order {

    @Id
    private String id;

    @Indexed(unique = true)
    private String orderNumber;

    @Indexed
    private String customerId;

    private String customerName;

    private String customerEmail;

    @Indexed
    private String restaurantId;

    private String restaurantName;

    @Builder.Default
    private List<OrderItemSnapshot> items = new ArrayList<>();

    private Address deliveryAddressSnapshot;

    private PricingBreakdown pricingBreakdown;

    private String couponCode;

    private String paymentMethod; // "CASH_ON_DELIVERY", "SIMULATED_ONLINE"

    @Builder.Default
    private String paymentStatus = "PENDING"; // "PENDING", "COMPLETED", "FAILED"

    private String transactionId;

    @Builder.Default
    private String orderStatus = "PLACED"; // PLACED, CONFIRMED, PREPARING, READY_FOR_PICKUP, OUT_FOR_DELIVERY, DELIVERED, CANCELLED, REJECTED

    private String cancellationReason;

    @Builder.Default
    private List<StatusLog> timeline = new ArrayList<>();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
