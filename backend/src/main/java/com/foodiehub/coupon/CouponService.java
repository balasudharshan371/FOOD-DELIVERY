package com.foodiehub.coupon;

import com.foodiehub.coupon.dto.CouponRequest;
import com.foodiehub.coupon.dto.CouponValidationResult;
import com.foodiehub.exception.BadRequestException;
import com.foodiehub.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponValidationResult validateCoupon(String code, BigDecimal subtotal) {
        if (code == null || code.isBlank()) {
            return CouponValidationResult.builder()
                    .valid(false)
                    .message("Coupon code cannot be empty")
                    .build();
        }

        Coupon coupon = couponRepository.findByCodeIgnoreCase(code.trim())
                .orElse(null);

        if (coupon == null || !coupon.getIsActive()) {
            return CouponValidationResult.builder()
                    .valid(false)
                    .code(code)
                    .message("Invalid or inactive coupon code")
                    .build();
        }

        Instant now = Instant.now();
        if (coupon.getExpiryDate() != null && coupon.getExpiryDate().isBefore(now)) {
            return CouponValidationResult.builder()
                    .valid(false)
                    .code(code)
                    .message("Coupon has expired")
                    .build();
        }

        if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            return CouponValidationResult.builder()
                    .valid(false)
                    .code(code)
                    .message("Coupon redemption limit reached")
                    .build();
        }

        if (coupon.getMinimumOrderValue() != null && subtotal.compareTo(coupon.getMinimumOrderValue()) < 0) {
            return CouponValidationResult.builder()
                    .valid(false)
                    .code(code)
                    .message(String.format("Minimum order value of ₹%s required for this coupon", coupon.getMinimumOrderValue()))
                    .build();
        }

        BigDecimal discountAmount;
        if ("PERCENTAGE".equalsIgnoreCase(coupon.getDiscountType())) {
            BigDecimal rawDiscount = subtotal.multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (coupon.getMaximumDiscount() != null && rawDiscount.compareTo(coupon.getMaximumDiscount()) > 0) {
                discountAmount = coupon.getMaximumDiscount();
            } else {
                discountAmount = rawDiscount;
            }
        } else {
            discountAmount = coupon.getDiscountValue().min(subtotal);
        }

        discountAmount = discountAmount.min(subtotal);
        BigDecimal subtotalAfterDiscount = subtotal.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);

        return CouponValidationResult.builder()
                .valid(true)
                .code(coupon.getCode())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .discountAmount(discountAmount)
                .subtotalAfterDiscount(subtotalAfterDiscount)
                .message(String.format("Coupon %s applied! Saved ₹%s", coupon.getCode(), discountAmount))
                .build();
    }

    public void incrementCouponUsage(String code) {
        if (code == null || code.isBlank()) return;
        couponRepository.findByCodeIgnoreCase(code.trim()).ifPresent(coupon -> {
            coupon.setUsedCount(coupon.getUsedCount() + 1);
            couponRepository.save(coupon);
        });
    }

    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    public Coupon createCoupon(CouponRequest request) {
        if (couponRepository.findByCodeIgnoreCase(request.getCode().trim()).isPresent()) {
            throw new BadRequestException("Coupon code already exists: " + request.getCode());
        }

        Coupon coupon = Coupon.builder()
                .code(request.getCode().trim().toUpperCase())
                .description(request.getDescription())
                .discountType(request.getDiscountType().toUpperCase())
                .discountValue(request.getDiscountValue())
                .minimumOrderValue(request.getMinimumOrderValue() != null ? request.getMinimumOrderValue() : BigDecimal.ZERO)
                .maximumDiscount(request.getMaximumDiscount())
                .startDate(request.getStartDate() != null ? request.getStartDate() : Instant.now())
                .expiryDate(request.getExpiryDate() != null ? request.getExpiryDate() : Instant.now().plusSeconds(86400 * 30))
                .usageLimit(request.getUsageLimit() != null ? request.getUsageLimit() : 1000)
                .usedCount(0)
                .perUserUsageLimit(request.getPerUserUsageLimit() != null ? request.getPerUserUsageLimit() : 1)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        return couponRepository.save(coupon);
    }

    public Coupon updateCoupon(String id, CouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "id", id));

        coupon.setCode(request.getCode().trim().toUpperCase());
        coupon.setDescription(request.getDescription());
        coupon.setDiscountType(request.getDiscountType().toUpperCase());
        coupon.setDiscountValue(request.getDiscountValue());
        if (request.getMinimumOrderValue() != null) coupon.setMinimumOrderValue(request.getMinimumOrderValue());
        if (request.getMaximumDiscount() != null) coupon.setMaximumDiscount(request.getMaximumDiscount());
        if (request.getExpiryDate() != null) coupon.setExpiryDate(request.getExpiryDate());
        if (request.getIsActive() != null) coupon.setIsActive(request.getIsActive());

        return couponRepository.save(coupon);
    }
}
