package com.foodiehub.common;

import java.math.BigDecimal;

public final class AppConstants {

    private AppConstants() {}

    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "desc";

    public static final BigDecimal TAX_RATE = new BigDecimal("0.05"); // 5% GST/Tax
    public static final BigDecimal DEFAULT_DELIVERY_FEE = new BigDecimal("40.00");
}
