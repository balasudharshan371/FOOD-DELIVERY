package com.foodiehub.cart;

import com.foodiehub.cart.dto.AddToCartRequest;
import com.foodiehub.cart.dto.CartResponse;
import com.foodiehub.common.AppConstants;
import com.foodiehub.exception.BadRequestException;
import com.foodiehub.menu.MenuItem;
import com.foodiehub.menu.MenuItemService;
import com.foodiehub.restaurant.Restaurant;
import com.foodiehub.restaurant.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final MenuItemService menuItemService;
    private final RestaurantService restaurantService;

    public Cart getOrCreateCart(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.builder()
                        .userId(userId)
                        .items(new ArrayList<>())
                        .updatedAt(Instant.now())
                        .build()));
    }

    public CartResponse getCartResponse(String userId) {
        Cart cart = getOrCreateCart(userId);
        return mapToCartResponse(cart);
    }

    public CartResponse addToCart(String userId, AddToCartRequest request, boolean confirmReplace) {
        MenuItem menuItem = menuItemService.getItemEntity(request.getMenuItemId());
        if (!menuItem.getIsAvailable()) {
            throw new BadRequestException("Item '" + menuItem.getName() + "' is currently sold out and unavailable.");
        }

        Restaurant restaurant = restaurantService.getRestaurantEntity(menuItem.getRestaurantId());
        if (!restaurant.getIsActive()) {
            throw new BadRequestException("Restaurant '" + restaurant.getName() + "' is currently closed.");
        }

        Cart cart = getOrCreateCart(userId);

        // Single Restaurant Invariant Rule
        if (cart.getRestaurantId() != null && !cart.getRestaurantId().equals(restaurant.getId())) {
            if (!confirmReplace) {
                throw new BadRequestException("Your cart already contains items from '" + cart.getRestaurantName() +
                        "'. Confirm to clear cart and start order from '" + restaurant.getName() + "'.");
            }
            cart.getItems().clear();
        }

        cart.setRestaurantId(restaurant.getId());
        cart.setRestaurantName(restaurant.getName());

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(i -> i.getMenuItemId().equals(menuItem.getId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existing = existingItemOpt.get();
            int newQty = existing.getQuantity() + request.getQuantity();
            if (newQty > 20) {
                throw new BadRequestException("Maximum quantity of 20 reached for item: " + menuItem.getName());
            }
            existing.setQuantity(newQty);
            existing.setSubtotal(existing.getPrice().multiply(BigDecimal.valueOf(newQty)));
        } else {
            CartItem newItem = CartItem.builder()
                    .menuItemId(menuItem.getId())
                    .name(menuItem.getName())
                    .price(menuItem.getPrice())
                    .quantity(request.getQuantity())
                    .imageUrl(menuItem.getImageUrl())
                    .foodType(menuItem.getFoodType())
                    .subtotal(menuItem.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())))
                    .build();
            cart.getItems().add(newItem);
        }

        cart.setUpdatedAt(Instant.now());
        Cart saved = cartRepository.save(cart);
        return mapToCartResponse(saved);
    }

    public CartResponse updateItemQuantity(String userId, String menuItemId, int quantity) {
        Cart cart = getOrCreateCart(userId);

        if (quantity <= 0) {
            cart.getItems().removeIf(i -> i.getMenuItemId().equals(menuItemId));
            if (cart.getItems().isEmpty()) {
                cart.setRestaurantId(null);
                cart.setRestaurantName(null);
            }
        } else {
            CartItem item = cart.getItems().stream()
                    .filter(i -> i.getMenuItemId().equals(menuItemId))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException("Item not found in cart"));

            item.setQuantity(quantity);
            item.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(quantity)));
        }

        cart.setUpdatedAt(Instant.now());
        Cart saved = cartRepository.save(cart);
        return mapToCartResponse(saved);
    }

    public CartResponse removeItem(String userId, String menuItemId) {
        return updateItemQuantity(userId, menuItemId, 0);
    }

    public void clearCart(String userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().clear();
        cart.setRestaurantId(null);
        cart.setRestaurantName(null);
        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);
    }

    private CartResponse mapToCartResponse(Cart cart) {
        BigDecimal subtotal = BigDecimal.ZERO;
        int totalCount = 0;

        for (CartItem item : cart.getItems()) {
            subtotal = subtotal.add(item.getSubtotal());
            totalCount += item.getQuantity();
        }

        BigDecimal deliveryFee = BigDecimal.ZERO;
        if (cart.getRestaurantId() != null && !cart.getItems().isEmpty()) {
            try {
                Restaurant r = restaurantService.getRestaurantEntity(cart.getRestaurantId());
                deliveryFee = r.getDeliveryFee();
            } catch (Exception e) {
                deliveryFee = AppConstants.DEFAULT_DELIVERY_FEE;
            }
        }

        BigDecimal tax = subtotal.multiply(AppConstants.TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(tax).add(deliveryFee).setScale(2, RoundingMode.HALF_UP);

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .restaurantId(cart.getRestaurantId())
                .restaurantName(cart.getRestaurantName())
                .items(cart.getItems())
                .subtotal(subtotal)
                .estimatedTax(tax)
                .deliveryFee(deliveryFee)
                .estimatedTotal(total)
                .totalItemCount(totalCount)
                .build();
    }
}
