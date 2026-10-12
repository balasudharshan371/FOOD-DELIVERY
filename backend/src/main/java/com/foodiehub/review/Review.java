package com.foodiehub.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reviews")
@CompoundIndex(name = "user_order_idx", def = "{'userId': 1, 'orderId': 1}", unique = true)
public class Review {

    @Id
    private String id;

    @Indexed
    private String userId;

    private String userName;

    @Indexed
    private String restaurantId;

    @Indexed
    private String orderId;

    private Integer rating; // 1 to 5

    private String comment;

    @CreatedDate
    private Instant createdAt;
}
