package com.foodiehub.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private String id;
    private String userId;
    private String userName;
    private String restaurantId;
    private String orderId;
    private Integer rating;
    private String comment;
    private Instant createdAt;
}
