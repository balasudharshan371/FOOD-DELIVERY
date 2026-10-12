package com.foodiehub.cart;

import com.foodiehub.cart.dto.AddToCartRequest;
import com.foodiehub.cart.dto.CartResponse;
import com.foodiehub.cart.dto.UpdateCartItemRequest;
import com.foodiehub.common.ApiResponse;
import com.foodiehub.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        CartResponse response = cartService.getCartResponse(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody AddToCartRequest request,
            @RequestParam(defaultValue = "false") boolean confirmReplace) {
        CartResponse response = cartService.addToCart(userPrincipal.getId(), request, confirmReplace);
        return ResponseEntity.ok(ApiResponse.success("Item added to cart", response));
    }

    @PatchMapping("/items/{menuItemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateItemQuantity(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String menuItemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        CartResponse response = cartService.updateItemQuantity(userPrincipal.getId(), menuItemId, request.getQuantity());
        return ResponseEntity.ok(ApiResponse.success("Cart updated", response));
    }

    @DeleteMapping("/items/{menuItemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String menuItemId) {
        CartResponse response = cartService.removeItem(userPrincipal.getId(), menuItemId);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart", response));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        cartService.clearCart(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Cart cleared", null));
    }
}
