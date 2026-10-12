package com.foodiehub.coupon;

import com.foodiehub.common.ApiResponse;
import com.foodiehub.coupon.dto.CouponRequest;
import com.foodiehub.coupon.dto.CouponValidationResult;
import com.foodiehub.coupon.dto.ValidateCouponRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/api/coupons/validate")
    public ResponseEntity<ApiResponse<CouponValidationResult>> validateCoupon(
            @Valid @RequestBody ValidateCouponRequest request) {
        CouponValidationResult result = couponService.validateCoupon(request.getCode(), request.getSubtotal());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/api/admin/coupons")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<Coupon>>> getAllCoupons() {
        return ResponseEntity.ok(ApiResponse.success(couponService.getAllCoupons()));
    }

    @PostMapping("/api/admin/coupons")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Coupon>> createCoupon(@Valid @RequestBody CouponRequest request) {
        Coupon created = couponService.createCoupon(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Coupon created successfully", created));
    }

    @PutMapping("/api/admin/coupons/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Coupon>> updateCoupon(
            @PathVariable String id,
            @Valid @RequestBody CouponRequest request) {
        Coupon updated = couponService.updateCoupon(id, request);
        return ResponseEntity.ok(ApiResponse.success("Coupon updated successfully", updated));
    }
}
