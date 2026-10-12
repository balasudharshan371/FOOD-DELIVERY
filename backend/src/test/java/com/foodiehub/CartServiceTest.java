package com.foodiehub;

import com.foodiehub.cart.Cart;
import com.foodiehub.cart.CartRepository;
import com.foodiehub.cart.CartService;
import com.foodiehub.cart.dto.AddToCartRequest;
import com.foodiehub.cart.dto.CartResponse;
import com.foodiehub.exception.BadRequestException;
import com.foodiehub.menu.MenuItem;
import com.foodiehub.menu.MenuItemService;
import com.foodiehub.restaurant.Restaurant;
import com.foodiehub.restaurant.RestaurantService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private MenuItemService menuItemService;

    @Mock
    private RestaurantService restaurantService;

    @InjectMocks
    private CartService cartService;

    @Test
    void addToCart_SingleRestaurantInvariant_Violated_ThrowsException() {
        MenuItem newItem = MenuItem.builder()
                .id("m2")
                .restaurantId("res2")
                .name("Pizza")
                .price(new BigDecimal("300.00"))
                .isAvailable(true)
                .build();

        Restaurant res2 = Restaurant.builder().id("res2").name("Pizza Place").isActive(true).build();

        Cart existingCart = Cart.builder()
                .userId("u1")
                .restaurantId("res1")
                .restaurantName("Biryani House")
                .items(new ArrayList<>())
                .build();

        when(menuItemService.getItemEntity("m2")).thenReturn(newItem);
        when(restaurantService.getRestaurantEntity("res2")).thenReturn(res2);
        when(cartRepository.findByUserId("u1")).thenReturn(Optional.of(existingCart));

        AddToCartRequest request = AddToCartRequest.builder().menuItemId("m2").quantity(1).build();

        assertThrows(BadRequestException.class, () -> cartService.addToCart("u1", request, false));
    }

    @Test
    void addToCart_Success() {
        MenuItem dish = MenuItem.builder()
                .id("m1")
                .restaurantId("res1")
                .name("Biryani")
                .price(new BigDecimal("250.00"))
                .isAvailable(true)
                .build();

        Restaurant res1 = Restaurant.builder()
                .id("res1")
                .name("Biryani House")
                .isActive(true)
                .deliveryFee(new BigDecimal("40.00"))
                .build();

        Cart cart = Cart.builder()
                .userId("u1")
                .items(new ArrayList<>())
                .build();

        when(menuItemService.getItemEntity("m1")).thenReturn(dish);
        when(restaurantService.getRestaurantEntity("res1")).thenReturn(res1);
        when(cartRepository.findByUserId("u1")).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AddToCartRequest request = AddToCartRequest.builder().menuItemId("m1").quantity(2).build();

        CartResponse response = cartService.addToCart("u1", request, false);

        assertNotNull(response);
        assertEquals(new BigDecimal("500.00"), response.getSubtotal());
        assertEquals(2, response.getTotalItemCount());
    }
}
