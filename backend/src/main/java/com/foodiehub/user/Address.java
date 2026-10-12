package com.foodiehub.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String id;
    private String label; // "Home", "Work", "Other"
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String contactPhone;
    private boolean isDefault;
}
