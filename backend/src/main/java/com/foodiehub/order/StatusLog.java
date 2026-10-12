package com.foodiehub.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusLog {
    private String status;
    private Instant timestamp;
    private String updatedBy;
    private String note;
}
