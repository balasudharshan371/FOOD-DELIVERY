package com.foodiehub.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String fullName;

    @Indexed(unique = true)
    private String email;

    @JsonIgnore
    private String passwordHash;

    private String phone;

    @Builder.Default
    private String role = "ROLE_CUSTOMER"; // ROLE_CUSTOMER, ROLE_RESTAURANT_OWNER, ROLE_ADMIN

    @Builder.Default
    private String accountStatus = "ACTIVE"; // ACTIVE, DEACTIVATED, SUSPENDED

    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
