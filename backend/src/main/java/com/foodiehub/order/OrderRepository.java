package com.foodiehub.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByCustomerIdOrderByCreatedAtDesc(String customerId);
    List<Order> findByRestaurantIdOrderByCreatedAtDesc(String restaurantId);
    Page<Order> findByRestaurantIdOrderByCreatedAtDesc(String restaurantId, Pageable pageable);
    long countByOrderStatus(String orderStatus);
    long countByRestaurantId(String restaurantId);
    long countByRestaurantIdAndOrderStatus(String restaurantId, String orderStatus);
}
