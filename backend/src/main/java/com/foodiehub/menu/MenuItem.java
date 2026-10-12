package com.foodiehub.menu;

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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "menu_items")
@CompoundIndex(name = "res_avail_idx", def = "{'restaurantId': 1, 'isAvailable': 1}")
public class MenuItem {

    @Id
    private String id;

    @Indexed
    private String restaurantId;

    @Indexed
    private String categoryId;

    private String name;

    private String description;

    private BigDecimal price;

    private String imageUrl;

    @Builder.Default
    private String foodType = "VEG"; // VEG, NON_VEG, VEGAN

    @Builder.Default
    private Boolean isAvailable = true;

    @Builder.Default
    private Integer preparationTimeMinutes = 20;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
