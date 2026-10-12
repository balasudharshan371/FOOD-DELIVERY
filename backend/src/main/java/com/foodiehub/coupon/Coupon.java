package com.foodiehub.coupon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "coupons")
public class Coupon {

    @Id
    private String id;

    @Indexed(unique = true)
    private String code; // e.g. "FOODIE50"

    private String description;

    @Builder.Default
    private String discountType = "PERCENTAGE"; // "PERCENTAGE" or "FLAT"

    private BigDecimal discountValue; // percentage (e.g. 10.00) or flat (e.g. 50.00)

    @Builder.Default
    private BigDecimal minimumOrderValue = BigDecimal.ZERO;

    private BigDecimal maximumDiscount; // cap for percentage discount

    private Instant startDate;

    private Instant expiryDate;

    @Builder.Default
    private Integer usageLimit = 1000;

    @Builder.Default
    private Integer usedCount = 0;

    @Builder.Default
    private Integer perUserUsageLimit = 1;

    @Builder.Default
    private Boolean isActive = true;
}
