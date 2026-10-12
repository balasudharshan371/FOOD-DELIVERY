package com.foodiehub.order;

import com.foodiehub.common.ApiResponse;
import com.foodiehub.order.dto.CancelOrderRequest;
import com.foodiehub.order.dto.CreateOrderRequest;
import com.foodiehub.order.dto.OrderResponse;
import com.foodiehub.order.dto.UpdateOrderStatusRequest;
import com.foodiehub.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/api/orders")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        OrderResponse response = orderService.createOrder(request, userPrincipal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order placed successfully", response));
    }

    @GetMapping("/api/orders/my")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<OrderResponse> orders = orderService.getMyOrders(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/api/orders/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        OrderResponse response = orderService.getOrderById(id, userPrincipal);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/api/orders/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable String id,
            @RequestBody(required = false) CancelOrderRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        String reason = request != null ? request.getReason() : "Customer cancelled";
        OrderResponse response = orderService.cancelOrder(id, reason, userPrincipal);
        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", response));
    }

    @PatchMapping("/api/owner/orders/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_RESTAURANT_OWNER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateOrderStatusRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        OrderResponse response = orderService.updateOrderStatus(id, request, userPrincipal);
        return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", response));
    }

    @GetMapping("/api/owner/orders")
    @PreAuthorize("hasAnyAuthority('ROLE_RESTAURANT_OWNER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOwnerOrders(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<OrderResponse> orders = orderService.getOwnerOrders(userPrincipal);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/api/admin/orders")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrdersAdmin() {
        List<OrderResponse> orders = orderService.getAllOrdersAdmin();
        return ResponseEntity.ok(ApiResponse.success(orders));
    }
}
