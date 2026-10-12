package com.foodiehub;

import com.foodiehub.cart.Cart;
import com.foodiehub.cart.CartItem;
import com.foodiehub.cart.CartService;
import com.foodiehub.coupon.CouponService;
import com.foodiehub.coupon.dto.CouponValidationResult;
import com.foodiehub.exception.BadRequestException;
import com.foodiehub.menu.MenuItem;
import com.foodiehub.menu.MenuItemService;
import com.foodiehub.order.Order;
import com.foodiehub.order.OrderRepository;
import com.foodiehub.order.OrderService;
import com.foodiehub.order.dto.CreateOrderRequest;
import com.foodiehub.order.dto.OrderResponse;
import com.foodiehub.payment.PaymentService;
import com.foodiehub.restaurant.Restaurant;
import com.foodiehub.restaurant.RestaurantService;
import com.foodiehub.security.UserPrincipal;
import com.foodiehub.user.Address;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartService cartService;

    @Mock
    private MenuItemService menuItemService;

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private CouponService couponService;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_EmptyCart_ThrowsBadRequest() {
        UserPrincipal user = UserPrincipal.builder().id("u1").fullName("Bala").email("bala@test.com").role("ROLE_CUSTOMER").build();
        Cart emptyCart = Cart.builder().items(List.of()).build();

        when(cartService.getOrCreateCart("u1")).thenReturn(emptyCart);

        CreateOrderRequest request = CreateOrderRequest.builder().paymentMethod("SIMULATED_ONLINE").build();

        assertThrows(BadRequestException.class, () -> orderService.createOrder(request, user));
    }

    @Test
    void createOrder_SuccessWithSnapshotAndPricing() {
        UserPrincipal user = UserPrincipal.builder().id("u1").fullName("Bala").email("bala@test.com").role("ROLE_CUSTOMER").build();

        CartItem item = CartItem.builder().menuItemId("m1").quantity(2).build();
        Cart cart = Cart.builder().restaurantId("res1").items(List.of(item)).build();

        Restaurant res = Restaurant.builder().id("res1").name("Biryani Villa").isActive(true).deliveryFee(new BigDecimal("40.00")).build();
        MenuItem dish = MenuItem.builder().id("m1").name("Chicken Biryani").price(new BigDecimal("200.00")).isAvailable(true).build();

        Address address = Address.builder().street("12 Road").city("Hyderabad").zipCode("500001").build();

        when(cartService.getOrCreateCart("u1")).thenReturn(cart);
        when(restaurantService.getRestaurantEntity("res1")).thenReturn(res);
        when(menuItemService.getItemEntity("m1")).thenReturn(dish);
        when(couponService.validateCoupon("FOODIE10", new BigDecimal("400.00")))
                .thenReturn(CouponValidationResult.builder().valid(true).code("FOODIE10").discountAmount(new BigDecimal("40.00")).build());
        when(paymentService.processPayment(any(), any(), any()))
                .thenReturn(new PaymentService.PaymentResult(true, "TXN-1", "Success"));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateOrderRequest request = CreateOrderRequest.builder()
                .deliveryAddress(address)
                .couponCode("FOODIE10")
                .paymentMethod("SIMULATED_ONLINE")
                .build();

        OrderResponse response = orderService.createOrder(request, user);

        assertNotNull(response);
        assertEquals("PLACED", response.getOrderStatus());
        assertEquals(new BigDecimal("400.00"), response.getPricingBreakdown().getSubtotal());
        assertEquals(new BigDecimal("40.00"), response.getPricingBreakdown().getDiscount());
        verify(cartService).clearCart("u1");
    }
}
