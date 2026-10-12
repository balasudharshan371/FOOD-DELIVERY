package com.foodiehub.coupon.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CouponRequest {

    @NotBlank(message = "Coupon code is required")
    private String code;

    private String description;

    @NotBlank(message = "Discount type is required")
    private String discountType; // PERCENTAGE or FLAT

    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.01")
    private BigDecimal discountValue;

    private BigDecimal minimumOrderValue;
    private BigDecimal maximumDiscount;
    private Instant startDate;
    private Instant expiryDate;
    private Integer usageLimit;
    private Integer perUserUsageLimit;
    private Boolean isActive;
}
