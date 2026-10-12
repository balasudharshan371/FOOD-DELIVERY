package com.foodiehub.restaurant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "restaurants")
@CompoundIndex(name = "city_active_idx", def = "{'city': 1, 'isActive': 1}")
public class Restaurant {

    @Id
    private String id;

    private String name;

    private String description;

    @Builder.Default
    private List<String> cuisineTypes = new ArrayList<>();

    private String address;

    @Indexed
    private String city;

    private String phone;

    @Indexed
    private String ownerId;

    private String imageUrl;

    @Builder.Default
    private Double averageRating = 0.0;

    @Builder.Default
    private Integer totalReviews = 0;

    @Builder.Default
    private BigDecimal deliveryFee = new BigDecimal("40.00");

    @Builder.Default
    private Integer estimatedDeliveryMinutes = 30;

    @Builder.Default
    private Boolean isActive = true;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
