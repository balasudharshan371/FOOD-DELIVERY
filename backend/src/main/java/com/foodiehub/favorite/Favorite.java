package com.foodiehub.favorite;

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
@Document(collection = "favorites")
@CompoundIndex(name = "user_restaurant_idx", def = "{'userId': 1, 'restaurantId': 1}", unique = true)
public class Favorite {

    @Id
    private String id;

    @Indexed
    private String userId;

    @Indexed
    private String restaurantId;

    @CreatedDate
    private Instant createdAt;
}
