package com.foodiehub.order;

import com.foodiehub.cart.Cart;
import com.foodiehub.cart.CartItem;
import com.foodiehub.cart.CartService;
import com.foodiehub.common.AppConstants;
import com.foodiehub.coupon.CouponService;
import com.foodiehub.coupon.dto.CouponValidationResult;
import com.foodiehub.exception.BadRequestException;
import com.foodiehub.exception.ForbiddenException;
import com.foodiehub.exception.ResourceNotFoundException;
import com.foodiehub.menu.MenuItem;
import com.foodiehub.menu.MenuItemService;
import com.foodiehub.order.dto.CreateOrderRequest;
import com.foodiehub.order.dto.OrderResponse;
import com.foodiehub.order.dto.UpdateOrderStatusRequest;
import com.foodiehub.payment.PaymentService;
import com.foodiehub.restaurant.Restaurant;
import com.foodiehub.restaurant.RestaurantService;
import com.foodiehub.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final MenuItemService menuItemService;
    private final RestaurantService restaurantService;
    private final CouponService couponService;
    private final PaymentService paymentService;

    public OrderResponse createOrder(CreateOrderRequest request, UserPrincipal currentUser) {
        Cart cart = cartService.getOrCreateCart(currentUser.getId());

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BadRequestException("Cannot checkout: Shopping cart is empty");
        }

        Restaurant restaurant = restaurantService.getRestaurantEntity(cart.getRestaurantId());
        if (!restaurant.getIsActive()) {
            throw new BadRequestException("Restaurant '" + restaurant.getName() + "' is currently closed.");
        }

        // Build item snapshots and calculate authoritative subtotal
        List<OrderItemSnapshot> itemSnapshots = new ArrayList<>();
        BigDecimal calculatedSubtotal = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            MenuItem liveDish = menuItemService.getItemEntity(cartItem.getMenuItemId());
            if (!liveDish.getIsAvailable()) {
                throw new BadRequestException("Item '" + liveDish.getName() + "' is sold out. Please remove from cart.");
            }

            BigDecimal itemSubtotal = liveDish.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            calculatedSubtotal = calculatedSubtotal.add(itemSubtotal);

            itemSnapshots.add(OrderItemSnapshot.builder()
                    .menuItemId(liveDish.getId())
                    .name(liveDish.getName())
                    .price(liveDish.getPrice())
                    .quantity(cartItem.getQuantity())
                    .subtotal(itemSubtotal)
                    .imageUrl(liveDish.getImageUrl())
                    .foodType(liveDish.getFoodType())
                    .build());
        }

        // Apply and validate coupon
        BigDecimal discount = BigDecimal.ZERO;
        String validatedCouponCode = null;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            CouponValidationResult result = couponService.validateCoupon(request.getCouponCode(), calculatedSubtotal);
            if (result.isValid()) {
                discount = result.getDiscountAmount();
                validatedCouponCode = result.getCode();
            } else {
                throw new BadRequestException(result.getMessage());
            }
        }

        // Calculate tax, delivery fee, and final payable total
        BigDecimal taxableAmount = calculatedSubtotal.subtract(discount).max(BigDecimal.ZERO);
        BigDecimal tax = taxableAmount.multiply(AppConstants.TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal deliveryFee = restaurant.getDeliveryFee() != null ? restaurant.getDeliveryFee() : AppConstants.DEFAULT_DELIVERY_FEE;
        BigDecimal finalTotal = taxableAmount.add(tax).add(deliveryFee).setScale(2, RoundingMode.HALF_UP);

        PricingBreakdown pricingBreakdown = PricingBreakdown.builder()
                .subtotal(calculatedSubtotal)
                .discount(discount)
                .tax(tax)
                .deliveryFee(deliveryFee)
                .finalTotal(finalTotal)
                .build();

        String orderNumber = "FH-" + System.currentTimeMillis() % 100000000;

        // Process payment
        PaymentService.PaymentResult paymentResult = paymentService.processPayment(
                request.getPaymentMethod(), finalTotal, orderNumber);

        String paymentStatus = "CASH_ON_DELIVERY".equalsIgnoreCase(request.getPaymentMethod()) ? "PENDING" : "COMPLETED";

        Instant now = Instant.now();
        List<StatusLog> timeline = new ArrayList<>();
        timeline.add(StatusLog.builder()
                .status("PLACED")
                .timestamp(now)
                .updatedBy(currentUser.getFullName())
                .note("Order placed via " + request.getPaymentMethod())
                .build());

        Order order = Order.builder()
                .orderNumber(orderNumber)
                .customerId(currentUser.getId())
                .customerName(currentUser.getFullName())
                .customerEmail(currentUser.getEmail())
                .restaurantId(restaurant.getId())
                .restaurantName(restaurant.getName())
                .items(itemSnapshots)
                .deliveryAddressSnapshot(request.getDeliveryAddress())
                .pricingBreakdown(pricingBreakdown)
                .couponCode(validatedCouponCode)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(paymentStatus)
                .transactionId(paymentResult.transactionId())
                .orderStatus("PLACED")
                .timeline(timeline)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Order savedOrder = orderRepository.save(order);

        // Clear cart and increment coupon usage
        cartService.clearCart(currentUser.getId());
        if (validatedCouponCode != null) {
            couponService.incrementCouponUsage(validatedCouponCode);
        }

        return mapToResponse(savedOrder);
    }

    public OrderResponse cancelOrder(String orderId, String reason, UserPrincipal currentUser) {
        Order order = getOrderEntity(orderId);

        // Ownership or Admin check
        boolean isAdmin = "ROLE_ADMIN".equalsIgnoreCase(currentUser.getRole());
        if (!isAdmin && !order.getCustomerId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can only cancel your own orders");
        }

        // Status restriction for customers
        if (!isAdmin && !"PLACED".equalsIgnoreCase(order.getOrderStatus())) {
            throw new BadRequestException("Order cannot be cancelled in status: " + order.getOrderStatus() +
                    ". Customer cancellation is only allowed while order is 'PLACED'.");
        }

        order.setOrderStatus("CANCELLED");
        order.setCancellationReason(reason != null ? reason : "Cancelled by " + currentUser.getFullName());
        order.setUpdatedAt(Instant.now());

        order.getTimeline().add(StatusLog.builder()
                .status("CANCELLED")
                .timestamp(Instant.now())
                .updatedBy(currentUser.getFullName())
                .note(reason != null ? reason : "Order cancelled")
                .build());

        return mapToResponse(orderRepository.save(order));
    }

    public OrderResponse updateOrderStatus(String orderId, UpdateOrderStatusRequest request, UserPrincipal currentUser) {
        Order order = getOrderEntity(orderId);
        String nextStatus = request.getStatus().toUpperCase();

        Restaurant restaurant = restaurantService.getRestaurantEntity(order.getRestaurantId());
        restaurantService.verifyOwnershipOrAdmin(restaurant, currentUser);

        validateStatusTransition(order.getOrderStatus(), nextStatus);

        order.setOrderStatus(nextStatus);
        order.setUpdatedAt(Instant.now());

        order.getTimeline().add(StatusLog.builder()
                .status(nextStatus)
                .timestamp(Instant.now())
                .updatedBy(currentUser.getFullName())
                .note(request.getNote() != null ? request.getNote() : "Status updated to " + nextStatus)
                .build());

        return mapToResponse(orderRepository.save(order));
    }

    public OrderResponse getOrderById(String orderId, UserPrincipal currentUser) {
        Order order = getOrderEntity(orderId);

        boolean isAdmin = "ROLE_ADMIN".equalsIgnoreCase(currentUser.getRole());
        boolean isCustomer = order.getCustomerId().equals(currentUser.getId());
        boolean isOwner = false;
        try {
            Restaurant r = restaurantService.getRestaurantEntity(order.getRestaurantId());
            isOwner = r.getOwnerId().equals(currentUser.getId());
        } catch (Exception ignored) {}

        if (!isAdmin && !isCustomer && !isOwner) {
            throw new ForbiddenException("You are not authorized to view this order");
        }

        return mapToResponse(order);
    }

    public List<OrderResponse> getMyOrders(String customerId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getOwnerOrders(UserPrincipal currentUser) {
        Restaurant restaurant = restaurantService.getRestaurantEntityByOwner(currentUser.getId());
        return orderRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurant.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getAllOrdersAdmin() {
        return orderRepository.findAll().stream()
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Order getOrderEntity(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
    }

    private void validateStatusTransition(String current, String next) {
        Map<String, Set<String>> allowedTransitions = Map.of(
                "PLACED", Set.of("CONFIRMED", "REJECTED", "CANCELLED"),
                "CONFIRMED", Set.of("PREPARING", "CANCELLED"),
                "PREPARING", Set.of("READY_FOR_PICKUP"),
                "READY_FOR_PICKUP", Set.of("OUT_FOR_DELIVERY"),
                "OUT_FOR_DELIVERY", Set.of("DELIVERED"),
                "DELIVERED", Set.of(),
                "CANCELLED", Set.of(),
                "REJECTED", Set.of()
        );

        Set<String> validNext = allowedTransitions.getOrDefault(current, Set.of());
        if (!validNext.contains(next)) {
            throw new BadRequestException("Invalid status transition from " + current + " to " + next);
        }
    }

    private OrderResponse mapToResponse(Order o) {
        return OrderResponse.builder()
                .id(o.getId())
                .orderNumber(o.getOrderNumber())
                .customerId(o.getCustomerId())
                .customerName(o.getCustomerName())
                .customerEmail(o.getCustomerEmail())
                .restaurantId(o.getRestaurantId())
                .restaurantName(o.getRestaurantName())
                .items(o.getItems())
                .deliveryAddressSnapshot(o.getDeliveryAddressSnapshot())
                .pricingBreakdown(o.getPricingBreakdown())
                .couponCode(o.getCouponCode())
                .paymentMethod(o.getPaymentMethod())
                .paymentStatus(o.getPaymentStatus())
                .transactionId(o.getTransactionId())
                .orderStatus(o.getOrderStatus())
                .cancellationReason(o.getCancellationReason())
                .timeline(o.getTimeline())
                .createdAt(o.getCreatedAt())
                .build();
    }
}
