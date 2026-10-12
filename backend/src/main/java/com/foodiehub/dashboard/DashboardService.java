package com.foodiehub.dashboard;

import com.foodiehub.dashboard.dto.AdminDashboardResponse;
import com.foodiehub.dashboard.dto.OwnerDashboardResponse;
import com.foodiehub.order.Order;
import com.foodiehub.order.OrderItemSnapshot;
import com.foodiehub.order.OrderRepository;
import com.foodiehub.order.OrderService;
import com.foodiehub.restaurant.Restaurant;
import com.foodiehub.restaurant.RestaurantRepository;
import com.foodiehub.restaurant.RestaurantService;
import com.foodiehub.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final RestaurantService restaurantService;

    public AdminDashboardResponse getAdminDashboard() {
        long totalCustomers = userRepository.countByRole("ROLE_CUSTOMER");
        long totalRestaurants = restaurantRepository.count();
        long totalOrders = orderRepository.count();
        long deliveredOrders = orderRepository.countByOrderStatus("DELIVERED");
        long cancelledOrders = orderRepository.countByOrderStatus("CANCELLED");

        List<Order> allOrders = orderRepository.findAll();
        BigDecimal totalRevenue = BigDecimal.ZERO;
        Map<String, AdminDashboardResponse.TopFoodItem> foodStats = new HashMap<>();

        for (Order o : allOrders) {
            if ("DELIVERED".equalsIgnoreCase(o.getOrderStatus()) || "COMPLETED".equalsIgnoreCase(o.getPaymentStatus())) {
                if (o.getPricingBreakdown() != null && o.getPricingBreakdown().getFinalTotal() != null) {
                    totalRevenue = totalRevenue.add(o.getPricingBreakdown().getFinalTotal());
                }
            }

            if (o.getItems() != null) {
                for (OrderItemSnapshot item : o.getItems()) {
                    foodStats.compute(item.getName(), (k, v) -> {
                        if (v == null) {
                            return AdminDashboardResponse.TopFoodItem.builder()
                                    .name(item.getName())
                                    .ordersCount(item.getQuantity())
                                    .totalSales(item.getSubtotal() != null ? item.getSubtotal() : BigDecimal.ZERO)
                                    .build();
                        } else {
                            v.setOrdersCount(v.getOrdersCount() + item.getQuantity());
                            v.setTotalSales(v.getTotalSales().add(item.getSubtotal() != null ? item.getSubtotal() : BigDecimal.ZERO));
                            return v;
                        }
                    });
                }
            }
        }

        List<AdminDashboardResponse.TopFoodItem> topItems = new ArrayList<>(foodStats.values());
        topItems.sort((a, b) -> Integer.compare(b.getOrdersCount(), a.getOrdersCount()));
        if (topItems.size() > 5) {
            topItems = topItems.subList(0, 5);
        }

        return AdminDashboardResponse.builder()
                .totalCustomers(totalCustomers)
                .totalRestaurants(totalRestaurants)
                .totalOrders(totalOrders)
                .deliveredOrders(deliveredOrders)
                .cancelledOrders(cancelledOrders)
                .totalRevenue(totalRevenue)
                .recentOrders(orderService.getAllOrdersAdmin().stream().limit(10).toList())
                .topSellingItems(topItems)
                .build();
    }

    public OwnerDashboardResponse getOwnerDashboard(String ownerId) {
        Restaurant restaurant = restaurantRepository.findFirstByOwnerId(ownerId)
                .orElseThrow(() -> new com.foodiehub.exception.ResourceNotFoundException("No restaurant assigned to owner ID: " + ownerId));
        List<Order> orders = orderRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurant.getId());

        long totalOrders = orders.size();
        long pendingOrders = orders.stream()
                .filter(o -> List.of("PLACED", "CONFIRMED", "PREPARING", "READY_FOR_PICKUP").contains(o.getOrderStatus()))
                .count();
        long completedOrders = orders.stream()
                .filter(o -> "DELIVERED".equalsIgnoreCase(o.getOrderStatus()))
                .count();

        BigDecimal revenue = BigDecimal.ZERO;
        for (Order o : orders) {
            if ("DELIVERED".equalsIgnoreCase(o.getOrderStatus()) || "COMPLETED".equalsIgnoreCase(o.getPaymentStatus())) {
                if (o.getPricingBreakdown() != null && o.getPricingBreakdown().getFinalTotal() != null) {
                    revenue = revenue.add(o.getPricingBreakdown().getFinalTotal());
                }
            }
        }

        return OwnerDashboardResponse.builder()
                .restaurantName(restaurant.getName())
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders)
                .completedOrders(completedOrders)
                .totalRevenue(revenue)
                .averageRating(restaurant.getAverageRating())
                .recentOrders(orders.stream().limit(10).map(o -> orderService.getOrderById(o.getId(),
                        com.foodiehub.security.UserPrincipal.builder().id(ownerId).role("ROLE_ADMIN").build())).toList())
                .build();
    }
}
