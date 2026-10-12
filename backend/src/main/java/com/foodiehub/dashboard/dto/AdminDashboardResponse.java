package com.foodiehub.dashboard.dto;

import com.foodiehub.order.dto.OrderResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {
    private long totalCustomers;
    private long totalRestaurants;
    private long totalOrders;
    private long deliveredOrders;
    private long cancelledOrders;
    private BigDecimal totalRevenue;
    private List<OrderResponse> recentOrders;
    private List<TopFoodItem> topSellingItems;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopFoodItem {
        private String name;
        private int ordersCount;
        private BigDecimal totalSales;
    }
}
