# FoodieHub — Comprehensive REST API Specification (Phase 1)

All endpoints conform to a standardized JSON response wrapper:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2026-10-12T00:30:00Z"
}
```

In case of validation or system errors:

```json
{
  "success": false,
  "message": "Validation failed",
  "errors": {
    "email": "Must be a valid email format",
    "password": "Password must be at least 6 characters"
  },
  "timestamp": "2026-10-12T00:30:00Z"
}
```

---

## 1. Authentication & User API

### 1.1 Register User
- **Method & Path**: `POST /api/auth/register`
- **Role**: Public
- **Request DTO**: `RegisterRequest`
  ```json
  {
    "fullName": "Bala Krishna",
    "email": "bala@example.com",
    "password": "Password@123",
    "phone": "+919876543210",
    "role": "ROLE_CUSTOMER"
  }
  ```
- **Validation**:
  - `fullName`: `@NotBlank`, 2-100 chars
  - `email`: `@NotBlank`, `@Email`, must be unique
  - `password`: `@NotBlank`, `@Size(min=6, max=40)`
  - `role`: Optional in payload, defaults to `ROLE_CUSTOMER`. Valid values: `ROLE_CUSTOMER`, `ROLE_RESTAURANT_OWNER` (Admin creation via secure seed / internal API).
- **Responses**:
  - `201 Created`: User registered successfully with JWT and user summary.
  - `400 Bad Request`: Validation failure or email already taken.

### 1.2 Login
- **Method & Path**: `POST /api/auth/login`
- **Role**: Public
- **Request DTO**: `LoginRequest`
  ```json
  {
    "email": "bala@example.com",
    "password": "Password@123"
  }
  ```
- **Response DTO**: `AuthResponse`
  ```json
  {
    "accessToken": "eyJhbGciOiJIUzI1NiIsIn...",
    "tokenType": "Bearer",
    "expiresIn": 86400000,
    "user": {
      "id": "67098e91f1a5b8234a9b1011",
      "fullName": "Bala Krishna",
      "email": "bala@example.com",
      "role": "ROLE_CUSTOMER"
    }
  }
  ```
- **Responses**:
  - `200 OK`: Successful authentication.
  - `401 Unauthorized`: Bad credentials or deactivated account.

### 1.3 Get Current User Session
- **Method & Path**: `GET /api/auth/me`
- **Role**: Authenticated (`ROLE_CUSTOMER`, `ROLE_RESTAURANT_OWNER`, `ROLE_ADMIN`)
- **Responses**:
  - `200 OK`: Returns authenticated `UserProfileResponse`.
  - `401 Unauthorized`: Invalid or expired JWT.

---

## 2. Restaurant & Menu API

### 2.1 List / Search Restaurants
- **Method & Path**: `GET /api/restaurants`
- **Role**: Public
- **Query Parameters**:
  - `search`: String (optional search across name, description)
  - `cuisine`: String (optional cuisine filter e.g. "Biryani")
  - `city`: String (optional city filter e.g. "Hyderabad")
  - `page`: Integer (default 0)
  - `size`: Integer (default 10)
  - `sortBy`: String (`averageRating`, `estimatedDeliveryMinutes`, `name` — default: `averageRating`)
  - `direction`: String (`asc`, `desc` — default: `desc`)
- **Response DTO**: `PagedResponse<RestaurantResponse>`
  ```json
  {
    "content": [
      {
        "id": "67098e91f1a5b8234a9b1022",
        "name": "Hyderabadi Spice Villa",
        "description": "Authentic dum biryani, kebabs and curries.",
        "cuisineTypes": ["Biryani", "North Indian", "Mughlai"],
        "address": "12 Banjara Hills, Road 1",
        "city": "Hyderabad",
        "phone": "+919876543211",
        "imageUrl": "https://images.unsplash.com/photo-1589302168068-964664d93dc0",
        "averageRating": 4.6,
        "totalReviews": 142,
        "deliveryFee": 40.00,
        "estimatedDeliveryMinutes": 35,
        "isActive": true
      }
    ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 24,
    "totalPages": 3,
    "last": false
  }
  ```

### 2.2 Get Restaurant Details & Menu
- **Method & Path**: `GET /api/restaurants/{id}`
- **Role**: Public
- **Response DTO**: `RestaurantDetailResponse` (includes restaurant metadata + categorized menu items)

### 2.3 Create Restaurant
- **Method & Path**: `POST /api/restaurants`
- **Role**: `ROLE_ADMIN`
- **Request DTO**: `RestaurantRequest`
  ```json
  {
    "name": "Flavors of Punjab",
    "description": "Hearty Punjabi butter chicken and garlic naan.",
    "cuisineTypes": ["North Indian", "Punjabi"],
    "address": "45 Jubilee Hills",
    "city": "Hyderabad",
    "phone": "+919876543212",
    "ownerId": "67098e91f1a5b8234a9b1099",
    "imageUrl": "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4",
    "deliveryFee": 50.00,
    "estimatedDeliveryMinutes": 40
  }
  ```
- **Responses**:
  - `201 Created`: Restaurant entity saved.
  - `400 Bad Request`: Validation failure or ownerId not found.
  - `403 Forbidden`: Non-admin caller.

### 2.4 Add Menu Item
- **Method & Path**: `POST /api/menu-items`
- **Role**: `ROLE_RESTAURANT_OWNER` (must own the restaurant), `ROLE_ADMIN`
- **Request DTO**: `MenuItemRequest`
  ```json
  {
    "restaurantId": "67098e91f1a5b8234a9b1022",
    "categoryId": "67098e91f1a5b8234a9b1055",
    "name": "Chicken Dum Biryani",
    "description": "Slow cooked fragrant basmati rice with marinated chicken & saffron.",
    "price": 280.00,
    "foodType": "NON_VEG",
    "imageUrl": "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8",
    "preparationTimeMinutes": 25,
    "isAvailable": true
  }
  ```
- **Responses**:
  - `201 Created`: Menu item created.
  - `403 Forbidden`: Owner does not own this restaurantId.

---

## 3. Cart & Pricing API

### 3.1 Get Cart
- **Method & Path**: `GET /api/cart`
- **Role**: `ROLE_CUSTOMER`
- **Response DTO**: `CartResponse`
  ```json
  {
    "id": "67098e91f1a5b8234a9b1066",
    "restaurantId": "67098e91f1a5b8234a9b1022",
    "restaurantName": "Hyderabadi Spice Villa",
    "items": [
      {
        "menuItemId": "67098e91f1a5b8234a9b1077",
        "name": "Chicken Dum Biryani",
        "price": 280.00,
        "quantity": 2,
        "subtotal": 560.00,
        "imageUrl": "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8"
      }
    ],
    "subtotal": 560.00,
    "deliveryFee": 40.00,
    "estimatedTax": 28.00,
    "estimatedTotal": 628.00
  }
  ```

### 3.2 Add Item to Cart
- **Method & Path**: `POST /api/cart/items`
- **Role**: `ROLE_CUSTOMER`
- **Request DTO**: `AddToCartRequest`
  ```json
  {
    "menuItemId": "67098e91f1a5b8234a9b1077",
    "quantity": 1
  }
  ```
- **Validation Rules**:
  - `menuItemId`: Must exist and `isAvailable == true`.
  - Single-restaurant check: If cart already contains items from a different restaurant, return `409 Conflict` with message `"Cart contains items from another restaurant. Please confirm to replace."`
  - `quantity`: `@Min(1) @Max(20)`.

---

## 4. Coupons API

### 4.1 Validate Coupon
- **Method & Path**: `POST /api/coupons/validate`
- **Role**: `ROLE_CUSTOMER`
- **Request DTO**: `ValidateCouponRequest`
  ```json
  {
    "code": "FOODIE50",
    "subtotal": 560.00
  }
  ```
- **Response DTO**: `CouponValidationResult`
  ```json
  {
    "valid": true,
    "code": "FOODIE50",
    "discountType": "PERCENTAGE",
    "discountValue": 10.00,
    "discountAmount": 56.00,
    "message": "Coupon FOODIE50 applied! You saved $56.00"
  }
  ```

---

## 5. Order Management & Lifecycle API

### 5.1 Create Order (Checkout)
- **Method & Path**: `POST /api/orders`
- **Role**: `ROLE_CUSTOMER`
- **Request DTO**: `CreateOrderRequest`
  ```json
  {
    "deliveryAddress": {
      "street": "Flat 302, Green Valley Apts",
      "city": "Hyderabad",
      "state": "Telangana",
      "zipCode": "500034",
      "contactPhone": "+919876543210"
    },
    "couponCode": "FOODIE50",
    "paymentMethod": "SIMULATED_ONLINE"
  }
  ```
- **Process**:
  1. Retrieves customer's current cart. Rejects if cart is empty.
  2. Verifies restaurant is open and active.
  3. Verifies each menu item is available and reads latest price from database.
  4. Calculates Subtotal, Coupon Discount, 5% Tax, and Delivery Fee using `BigDecimal`.
  5. If `SIMULATED_ONLINE`, executes simulated payment transaction.
  6. Creates snapshot `Order` document with status `PLACED`.
  7. Clears user's cart.
  8. Increments coupon usage count if used.
- **Response DTO**: `OrderResponse` (HTTP 201 Created)

### 5.2 Cancel Order
- **Method & Path**: `PATCH /api/orders/{id}/cancel`
- **Role**: `ROLE_CUSTOMER` (own order), `ROLE_ADMIN`
- **Request DTO**:
  ```json
  {
    "cancellationReason": "Changed my mind"
  }
  ```
- **Rules**:
  - Customer can only cancel if `orderStatus == 'PLACED'`. Once `CONFIRMED` or `PREPARING`, customer cannot cancel (`400 Bad Request`).

### 5.3 Update Order Status
- **Method & Path**: `PATCH /api/owner/orders/{id}/status`
- **Role**: `ROLE_RESTAURANT_OWNER` (must own restaurant), `ROLE_ADMIN`
- **Request DTO**: `UpdateOrderStatusRequest`
  ```json
  {
    "status": "PREPARING",
    "note": "Chef started cooking"
  }
  ```
- **Valid Transitions**:
  - `PLACED` -> `CONFIRMED` or `REJECTED`
  - `CONFIRMED` -> `PREPARING`
  - `PREPARING` -> `READY_FOR_PICKUP`
  - `READY_FOR_PICKUP` -> `OUT_FOR_DELIVERY`
  - `OUT_FOR_DELIVERY` -> `DELIVERED`

---

## 6. Reviews & Favorites API

### 6.1 Create Review
- **Method & Path**: `POST /api/reviews`
- **Role**: `ROLE_CUSTOMER`
- **Request DTO**: `ReviewRequest`
  ```json
  {
    "orderId": "67098e91f1a5b8234a9b1088",
    "rating": 5,
    "comment": "Hot, crispy, and arrived in under 30 minutes! Highly recommended."
  }
  ```
- **Rules**:
  - `order.customerId` must match the authenticated user.
  - `order.orderStatus` must be `DELIVERED`.
  - Only one review allowed per order (enforced by DB index and service check).
  - Automatically updates restaurant's `averageRating` and `totalReviews`.

---

## 7. Dashboard Analytics API

### 7.1 Admin Analytics
- **Method & Path**: `GET /api/admin/dashboard`
- **Role**: `ROLE_ADMIN`
- **Response**:
  ```json
  {
    "totalCustomers": 128,
    "totalRestaurants": 12,
    "totalOrders": 450,
    "deliveredOrders": 380,
    "cancelledOrders": 24,
    "totalRevenue": 154200.00,
    "recentOrders": [ ... ],
    "topSellingItems": [
      { "name": "Chicken Dum Biryani", "ordersCount": 124, "totalSales": 34720.00 }
    ]
  }
  ```

### 7.2 Restaurant Owner Analytics
- **Method & Path**: `GET /api/owner/dashboard`
- **Role**: `ROLE_RESTAURANT_OWNER`
- **Response**:
  ```json
  {
    "restaurantName": "Hyderabadi Spice Villa",
    "totalOrders": 142,
    "pendingOrders": 3,
    "completedOrders": 130,
    "totalRevenue": 48200.00,
    "recentOrders": [ ... ],
    "popularMenuItems": [ ... ]
  }
  ```
