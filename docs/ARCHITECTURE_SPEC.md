# FoodieHub — Architecture & System Design Specification (Phase 1)

## 1. System Overview & Core Assumptions

FoodieHub is a full-stack online food ordering and restaurant management platform. It connects customers, restaurant owners, and platform administrators through a clean, decoupled 3-tier architecture:

```
[ React 18+ (Vite) Single Page Application ]
                   │  (HTTPS / JSON REST API with Bearer JWT)
                   ▼
[ Spring Boot 3.x Backend (Controller -> Service -> Repository) ]
                   │  (Spring Data MongoDB Driver)
                   ▼
[ MongoDB Database (Atlas Cloud or Local Instance) ]
```

### Core Business Rules & Invariants
1. **Single-Restaurant Cart Invariant**: A shopping cart can only hold items from one restaurant at a time. Attempting to add an item from a different restaurant triggers a client-side prompt confirming whether to clear the current cart and start fresh.
2. **Snapshot-Based Order Isolation**: When an order is placed, full snapshots of menu item names, prices, quantities, and the user's delivery address are embedded directly in the `Order` document. Future updates to menu items (e.g. price increases, name edits) or the user's profile address will never mutate historical order records.
3. **Server-Authoritative Pricing**: All financial calculations (subtotal, discounts, taxes, delivery fees, and total payable amounts) are computed strictly on the backend using Java `BigDecimal` with `RoundingMode.HALF_UP`. Client-submitted totals are never trusted.
4. **Role-Based & Resource-Level Ownership**: Access control is enforced in Spring Security using role checks (`ROLE_CUSTOMER`, `ROLE_RESTAURANT_OWNER`, `ROLE_ADMIN`) and verified in service methods (e.g. restaurant owners can only update their assigned restaurant and orders; customers can only access their own orders and reviews).

---

## 2. Technology Stack & Key Dependencies

### Backend (Java 21 / Spring Boot 3.3.x)
- **Framework**: Spring Boot 3.3.x (Java 21 LTS)
- **Web & Security**: `spring-boot-starter-web`, `spring-boot-starter-security`, `spring-boot-starter-validation`
- **Data Access**: `spring-boot-starter-data-mongodb`
- **Authentication**: `io.jsonwebtoken:jjwt-api:0.12.5`, `jjwt-impl:0.12.5`, `jjwt-jackson:0.12.5`
- **Productivity**: `org.projectlombok:lombok`
- **Documentation**: `org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0`
- **Testing**: `spring-boot-starter-test` (JUnit 5, Mockito, AssertJ)

### Frontend (React 18+ / Vite)
- **Build Tool**: Vite (ES6+ JavaScript)
- **Routing**: `react-router-dom` (v6)
- **HTTP Client**: `axios` (centralized instance with interceptors)
- **State Management**: React Context API (`AuthContext`, `CartContext`)
- **Styling**: Tailwind CSS (with warm orange accent `#FF5200` / `#E23744`, subtle shadows, responsive grid)
- **Icons**: `lucide-react`
- **Charts / Analytics**: `recharts`
- **Notifications**: Reusable Toast notification system

### Database
- **Engine**: MongoDB (Document store)
- **Driver**: Spring Data MongoDB with automated indexing and aggregation pipeline support

---

## 3. Project Directory Structure

```
foodiehub/
├── .gitignore
├── README.md
├── docs/
│   ├── ARCHITECTURE_SPEC.md
│   └── API_SPECIFICATION.md
├── postman/
│   └── FoodieHub_Postman_Collection.json
├── backend/
│   ├── pom.xml
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/foodiehub/
│   │   │   │   ├── FoodieHubApplication.java
│   │   │   │   ├── config/
│   │   │   │   │   ├── SecurityConfig.java
│   │   │   │   │   ├── CorsConfig.java
│   │   │   │   │   ├── MongoConfig.java
│   │   │   │   │   └── SwaggerConfig.java
│   │   │   │   ├── security/
│   │   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   │   ├── CustomUserDetailsService.java
│   │   │   │   │   ├── UserPrincipal.java
│   │   │   │   │   └── AuthEntryPointJwt.java
│   │   │   │   ├── common/
│   │   │   │   │   ├── ApiResponse.java
│   │   │   │   │   ├── PagedResponse.java
│   │   │   │   │   └── AppConstants.java
│   │   │   │   ├── exception/
│   │   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   │   ├── BadRequestException.java
│   │   │   │   │   ├── UnauthorizedException.java
│   │   │   │   │   └── ForbiddenException.java
│   │   │   │   ├── auth/
│   │   │   │   │   ├── AuthController.java
│   │   │   │   │   ├── AuthService.java
│   │   │   │   │   └── dto/ (LoginRequest, RegisterRequest, AuthResponse)
│   │   │   │   ├── user/
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── Address.java
│   │   │   │   │   ├── UserRepository.java
│   │   │   │   │   ├── UserService.java
│   │   │   │   │   ├── UserController.java
│   │   │   │   │   └── dto/ (UserProfileResponse, AddressRequest, UserStatusUpdateRequest)
│   │   │   │   ├── restaurant/
│   │   │   │   │   ├── Restaurant.java
│   │   │   │   │   ├── RestaurantRepository.java
│   │   │   │   │   ├── RestaurantService.java
│   │   │   │   │   ├── RestaurantController.java
│   │   │   │   │   └── dto/ (RestaurantRequest, RestaurantResponse, RestaurantFilterRequest)
│   │   │   │   ├── category/
│   │   │   │   │   ├── Category.java
│   │   │   │   │   ├── CategoryRepository.java
│   │   │   │   │   ├── CategoryService.java
│   │   │   │   │   ├── CategoryController.java
│   │   │   │   │   └── dto/ (CategoryRequest, CategoryResponse)
│   │   │   │   ├── menu/
│   │   │   │   │   ├── MenuItem.java
│   │   │   │   │   ├── MenuItemRepository.java
│   │   │   │   │   ├── MenuItemService.java
│   │   │   │   │   ├── MenuItemController.java
│   │   │   │   │   └── dto/ (MenuItemRequest, MenuItemResponse)
│   │   │   │   ├── cart/
│   │   │   │   │   ├── Cart.java
│   │   │   │   │   ├── CartItem.java
│   │   │   │   │   ├── CartRepository.java
│   │   │   │   │   ├── CartService.java
│   │   │   │   │   ├── CartController.java
│   │   │   │   │   └── dto/ (AddToCartRequest, UpdateCartItemRequest, CartResponse)
│   │   │   │   ├── coupon/
│   │   │   │   │   ├── Coupon.java
│   │   │   │   │   ├── CouponRepository.java
│   │   │   │   │   ├── CouponService.java
│   │   │   │   │   ├── CouponController.java
│   │   │   │   │   └── dto/ (CouponRequest, CouponResponse, ValidateCouponRequest, CouponValidationResult)
│   │   │   │   ├── order/
│   │   │   │   │   ├── Order.java
│   │   │   │   │   ├── OrderItemSnapshot.java
│   │   │   │   │   ├── DeliveryAddressSnapshot.java
│   │   │   │   │   ├── PricingBreakdown.java
│   │   │   │   │   ├── OrderStatus.java
│   │   │   │   │   ├── PaymentMethod.java
│   │   │   │   │   ├── PaymentStatus.java
│   │   │   │   │   ├── OrderRepository.java
│   │   │   │   │   ├── OrderService.java
│   │   │   │   │   ├── OrderController.java
│   │   │   │   │   └── dto/ (CreateOrderRequest, OrderResponse, UpdateOrderStatusRequest)
│   │   │   │   ├── payment/
│   │   │   │   │   ├── PaymentService.java
│   │   │   │   │   └── dto/ (PaymentProcessRequest, PaymentProcessResult)
│   │   │   │   ├── review/
│   │   │   │   │   ├── Review.java
│   │   │   │   │   ├── ReviewRepository.java
│   │   │   │   │   ├── ReviewService.java
│   │   │   │   │   ├── ReviewController.java
│   │   │   │   │   └── dto/ (ReviewRequest, ReviewResponse)
│   │   │   │   ├── favorite/
│   │   │   │   │   ├── Favorite.java
│   │   │   │   │   ├── FavoriteRepository.java
│   │   │   │   │   ├── FavoriteService.java
│   │   │   │   │   └── FavoriteController.java
│   │   │   │   └── dashboard/
│   │   │   │       ├── DashboardService.java
│   │   │   │       ├── DashboardController.java
│   │   │   │       └── dto/ (AdminDashboardResponse, OwnerDashboardResponse)
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── application-dev.yml
│   │   └── test/
│   │       └── java/com/foodiehub/
│   │           ├── auth/AuthServiceTest.java
│   │           ├── order/OrderServiceTest.java
│   │           └── cart/CartServiceTest.java
└── frontend/
    ├── package.json
    ├── vite.config.js
    ├── index.html
    ├── src/
    │   ├── main.jsx
    │   ├── App.jsx
    │   ├── index.css
    │   ├── api/
    │   │   ├── axiosClient.js
    │   │   ├── authApi.js
    │   │   ├── restaurantApi.js
    │   │   ├── cartApi.js
    │   │   ├── orderApi.js
    │   │   ├── couponApi.js
    │   │   └── dashboardApi.js
    │   ├── components/
    │   │   ├── common/ (Navbar, Footer, Modal, Loader, Badge, Toast, Button)
    │   │   ├── restaurant/ (RestaurantCard, RestaurantFilters, RatingBadge)
    │   │   ├── menu/ (MenuItemCard, VegNonVegBadge)
    │   │   └── order/ (OrderTimeline, StatusPill)
    │   ├── context/
    │   │   ├── AuthContext.jsx
    │   │   └── CartContext.jsx
    │   ├── hooks/
    │   │   ├── useAuth.js
    │   │   └── useCart.js
    │   ├── routes/
    │   │   ├── AppRoutes.jsx
    │   │   ├── ProtectedRoute.jsx
    │   │   └── RoleRoute.jsx
    │   ├── pages/
    │   │   ├── public/ (HomePage, RestaurantListPage, RestaurantDetailPage, LoginPage, RegisterPage, NotFoundPage)
    │   │   ├── customer/ (CartPage, CheckoutPage, OrderHistoryPage, OrderTrackingPage, ProfilePage, FavoritesPage)
    │   │   ├── owner/ (OwnerDashboardPage, OwnerMenuPage, OwnerOrdersPage, OwnerRestaurantProfilePage)
    │   │   └── admin/ (AdminDashboardPage, AdminUsersPage, AdminRestaurantsPage, AdminCategoriesPage, AdminCouponsPage, AdminOrdersPage)
    │   └── utils/
    │       ├── formatters.js
    │       └── constants.js
```

---

## 4. MongoDB Database Design & Indexing

### Collections Schema Specifications

#### 1. `users`
| Field | Type | Required | Description / Constraints |
|---|---|---|---|
| `_id` | ObjectId / String | Yes | MongoDB primary key |
| `fullName` | String | Yes | 2 to 100 characters |
| `email` | String | Yes | Unique index, lowercase, validated email format |
| `passwordHash` | String | Yes | BCrypt salted hash (excluded in responses) |
| `phone` | String | Yes | E.164 or 10-digit format |
| `role` | String | Yes | `ROLE_CUSTOMER`, `ROLE_RESTAURANT_OWNER`, `ROLE_ADMIN` |
| `accountStatus` | String | Yes | `ACTIVE`, `DEACTIVATED`, `SUSPENDED` |
| `addresses` | Array of Address | No | Embedded address list (street, city, state, zipCode, isDefault) |
| `createdAt` | Instant | Yes | Auto-generated timestamp |
| `updatedAt` | Instant | Yes | Auto-generated timestamp |

*Indexes*:
- `{ "email": 1 }` (Unique)
- `{ "role": 1 }`

---

#### 2. `restaurants`
| Field | Type | Required | Description / Constraints |
|---|---|---|---|
| `_id` | ObjectId / String | Yes | Primary key |
| `name` | String | Yes | Restaurant trade name (e.g., "Spice Route") |
| `description` | String | Yes | Bio / description |
| `cuisineTypes` | List\<String\> | Yes | e.g. `["Indian", "Mughlai", "Biryani"]` |
| `address` | String | Yes | Full physical address |
| `city` | String | Yes | City name for local filtering |
| `phone` | String | Yes | Contact number |
| `ownerId` | String | Yes | Foreign reference to `users._id` (Owner) |
| `imageUrl` | String | No | Banner/Display image URL |
| `averageRating` | Double | Yes | Maintained rating (0.0 to 5.0, default 0.0) |
| `totalReviews` | Integer | Yes | Count of approved ratings |
| `deliveryFee` | BigDecimal | Yes | Fixed delivery charge |
| `estimatedDeliveryMinutes` | Integer | Yes | e.g. 30 (mins) |
| `isActive` | Boolean | Yes | Operational toggle (open/closed) |
| `createdAt` | Instant | Yes | Timestamp |
| `updatedAt` | Instant | Yes | Timestamp |

*Indexes*:
- `{ "ownerId": 1 }`
- `{ "city": 1, "isActive": 1 }`
- `{ "cuisineTypes": 1 }`
- `{ "name": "text", "description": "text" }` (Text search index)

---

#### 3. `categories`
| Field | Type | Required | Description |
|---|---|---|---|
| `_id` | ObjectId / String | Yes | Primary key |
| `name` | String | Yes | Category name (e.g. "Main Course", "Beverages", "Desserts") |
| `description` | String | No | Overview |
| `imageUrl` | String | No | Category icon/image |
| `isActive` | Boolean | Yes | Toggle status |

*Indexes*:
- `{ "name": 1 }` (Unique)

---

#### 4. `menu_items`
| Field | Type | Required | Description / Constraints |
|---|---|---|---|
| `_id` | ObjectId / String | Yes | Primary key |
| `restaurantId` | String | Yes | Reference to `restaurants._id` |
| `categoryId` | String | Yes | Reference to `categories._id` |
| `name` | String | Yes | Dish name |
| `description` | String | No | Ingredients & description |
| `price` | BigDecimal | Yes | Base price in currency unit |
| `imageUrl` | String | No | Food photo URL |
| `foodType` | String | Yes | `VEG`, `NON_VEG`, `VEGAN` |
| `isAvailable` | Boolean | Yes | Availability toggle |
| `preparationTimeMinutes`| Integer | Yes | Estimated prep duration |
| `createdAt` | Instant | Yes | Timestamp |
| `updatedAt` | Instant | Yes | Timestamp |

*Indexes*:
- `{ "restaurantId": 1, "isAvailable": 1 }`
- `{ "categoryId": 1 }`
- `{ "name": "text" }`

---

#### 5. `carts`
| Field | Type | Required | Description |
|---|---|---|---|
| `_id` | ObjectId / String | Yes | Primary key |
| `userId` | String | Yes | Reference to `users._id` (Unique per customer) |
| `restaurantId` | String | No | Active restaurant being ordered from (null if cart empty) |
| `items` | List\<CartItem\> | Yes | Embedded items: `[menuItemId, name, price, quantity, imageUrl]` |
| `updatedAt` | Instant | Yes | Last modified timestamp |

*Indexes*:
- `{ "userId": 1 }` (Unique)

---

#### 6. `coupons`
| Field | Type | Required | Description / Constraints |
|---|---|---|---|
| `_id` | ObjectId / String | Yes | Primary key |
| `code` | String | Yes | Uppercase coupon identifier (e.g., `FOODIE50`, `WELCOME20`) |
| `discountType` | String | Yes | `PERCENTAGE` or `FLAT` |
| `discountValue` | BigDecimal | Yes | Percentage rate (e.g. 20%) or Flat value (e.g. $5.00) |
| `minimumOrderValue` | BigDecimal | Yes | Threshold to apply |
| `maximumDiscount` | BigDecimal | No | Maximum cap for percentage discounts |
| `startDate` | Instant | Yes | Valid from |
| `expiryDate` | Instant | Yes | Valid till |
| `usageLimit` | Integer | Yes | Platform-wide redemption pool |
| `usedCount` | Integer | Yes | Times redeemed |
| `perUserUsageLimit` | Integer | Yes | Max redemption per customer |
| `isActive` | Boolean | Yes | Manual toggle |

*Indexes*:
- `{ "code": 1 }` (Unique)
- `{ "isActive": 1, "expiryDate": 1 }`

---

#### 7. `orders`
| Field | Type | Required | Description |
|---|---|---|---|
| `_id` | ObjectId / String | Yes | Primary key |
| `orderNumber` | String | Yes | Unique human-friendly ID (e.g. `FH-20261012-8492`) |
| `customerId` | String | Yes | Reference to `users._id` |
| `restaurantId` | String | Yes | Reference to `restaurants._id` |
| `restaurantName`| String | Yes | Snapshot of restaurant name |
| `items` | List\<OrderItemSnapshot\> | Yes | Snapshot list: `[menuItemId, name, price, quantity, subtotal]` |
| `deliveryAddressSnapshot` | Object | Yes | Snapshot of delivery address at checkout time |
| `pricingBreakdown` | Object | Yes | `{ subtotal, discount, tax, deliveryFee, finalTotal }` |
| `couponCode` | String | No | Applied coupon code (if any) |
| `paymentMethod` | String | Yes | `CASH_ON_DELIVERY`, `SIMULATED_ONLINE` |
| `paymentStatus` | String | Yes | `PENDING`, `COMPLETED`, `FAILED` |
| `orderStatus` | String | Yes | State Machine status |
| `cancellationReason` | String | No | Reason when CANCELLED or REJECTED |
| `timeline` | List\<StatusLog\> | Yes | Array of `{ status, timestamp, updatedBy, note }` |
| `createdAt` | Instant | Yes | Timestamp |
| `updatedAt` | Instant | Yes | Timestamp |

*Indexes*:
- `{ "orderNumber": 1 }` (Unique)
- `{ "customerId": 1, "createdAt": -1 }`
- `{ "restaurantId": 1, "orderStatus": 1 }`
- `{ "createdAt": -1 }`

---

#### 8. `reviews`
| Field | Type | Required | Description |
|---|---|---|---|
| `_id` | ObjectId / String | Yes | Primary key |
| `userId` | String | Yes | Reference to `users._id` |
| `userName` | String | Yes | Customer name snapshot |
| `restaurantId` | String | Yes | Reference to `restaurants._id` |
| `orderId` | String | Yes | Reference to `orders._id` (Verifies verified purchase) |
| `rating` | Integer | Yes | 1 to 5 stars |
| `comment` | String | No | Review text (max 500 chars) |
| `createdAt` | Instant | Yes | Timestamp |

*Indexes*:
- `{ "userId": 1, "orderId": 1 }` (Unique compound: 1 review per delivered order)
- `{ "restaurantId": 1, "createdAt": -1 }`

---

#### 9. `favorites`
| Field | Type | Required | Description |
|---|---|---|---|
| `_id` | ObjectId / String | Yes | Primary key |
| `userId` | String | Yes | Customer who saved |
| `restaurantId` | String | Yes | Saved restaurant |
| `createdAt` | Instant | Yes | Timestamp |

*Indexes*:
- `{ "userId": 1, "restaurantId": 1 }` (Unique compound: prevents duplicate favorites)

---

## 5. Architectural Trade-offs: Embedding vs. Referencing

1. **Why Embed `items` and `deliveryAddress` in `orders`?**
   - High data integrity & immutability: Menu items change their prices, dishes get discontinued, and users move to new apartments. An invoice/order must reflect the exact pricing and shipping details agreed upon at transaction time.
   - High read performance: Querying an order retrieves the entire receipt in a single index lookup without expensive `$lookup` joins.

2. **Why Reference `restaurantId` and `categoryId` in `menu_items`?**
   - A single restaurant can offer hundreds of dishes. Embedding all menu items within the restaurant document would bloat document sizes and make individual dish updates prone to concurrency conflicts.
   - Separate collection allows direct pagination, search filtering (veg/non-veg, price range), and atomic updates.

3. **Why Separate `reviews` Collection?**
   - Customer reviews grow unboundedly over time. In MongoDB, documents have a 16MB limit. An independent `reviews` collection enables easy pagination, average score aggregations, and avoids unbounded array growth.

---

## 6. Role-Based Access Control (RBAC) Matrix

| Resource / Action | Customer | Restaurant Owner | Administrator |
|---|:---:|:---:|:---:|
| Register / Login / View Profile | Yes | Yes | Yes |
| Browse Restaurants & Menus | Yes | Yes | Yes |
| Search, Filter & Sort Foods | Yes | Yes | Yes |
| Manage Own Addresses | Yes | No | No |
| Add to Cart / Clear Cart | Yes | No | No |
| Apply Coupons | Yes | No | No |
| Place Order & Simulated Checkout | Yes | No | No |
| View Own Orders | Yes | No | All Orders |
| Cancel Order (If status is PLACED) | Yes (own) | No | Yes (any) |
| Track Order Status | Yes (own) | Yes (own restaurant) | Yes (all) |
| Review Restaurant (Delivered Order) | Yes | No | Moderate/Delete |
| Add / Remove Favorites | Yes | No | No |
| Update Restaurant Info | No | Yes (assigned restaurant) | Yes (any) |
| Add / Edit / Remove Menu Items | No | Yes (assigned restaurant) | Yes (any) |
| Toggle Food Availability | No | Yes (assigned restaurant) | Yes (any) |
| Accept / Reject Incoming Orders | No | Yes (assigned restaurant) | Yes |
| Progress Order Preparation Status | No | Yes (assigned restaurant) | Yes |
| View Restaurant Dashboard & Sales | No | Yes (assigned restaurant) | Yes (all) |
| Create / Deactivate Restaurants | No | No | Yes |
| Assign Owners to Restaurants | No | No | Yes |
| Manage Categories & Global Coupons | No | No | Yes |
| Manage Users & Account Status | No | No | Yes |
| View System Analytics & Reports | No | No | Yes |

---

## 7. Order Lifecycle & State Machine

```
               [Customer Places Order]
                          │
                          ▼
                       PLACED
                      /      \
      (Owner Rejects)/        \(Owner Confirms)
                    ▼          ▼
                REJECTED    CONFIRMED
                               │
                               ▼
                           PREPARING
                               │
                               ▼
                        READY_FOR_PICKUP
                               │
                               ▼
                        OUT_FOR_DELIVERY
                               │
                               ▼
                           DELIVERED
```

*State Transitions Rules*:
- **PLACED**: Order created and pending review. Customer can cancel; Owner can REJECT or CONFIRM; Admin can CANCEL.
- **CONFIRMED**: Owner has accepted the order. Customer cancellation is now locked.
- **PREPARING**: Kitchen has started food preparation. Owner transition only.
- **READY_FOR_PICKUP**: Food packaged and ready for delivery agent.
- **OUT_FOR_DELIVERY**: Order dispatched to delivery address.
- **DELIVERED**: Order handed over. Final state. Triggers eligibility for Customer Review.
- **CANCELLED / REJECTED**: Terminal failure states. Any simulated online payment is marked for simulated refund.

---

## 8. Pricing & Calculation Formula

All calculations execute on the backend:

$$\text{Subtotal} = \sum_{i=1}^{n} (\text{itemPrice}_i \times \text{quantity}_i)$$

$$\text{Discount} = \begin{cases} 
0, & \text{if no coupon or Subtotal} < \text{minimumOrderValue} \\
\min(\text{Subtotal} \times \frac{\text{discountValue}}{100}, \text{maxDiscount}), & \text{if PERCENTAGE} \\
\min(\text{discountValue}, \text{Subtotal}), & \text{if FLAT}
\end{cases}$$

$$\text{Tax} = (\text{Subtotal} - \text{Discount}) \times 0.05 \quad (\text{5\% GST/Tax, RoundingMode.HALF\_UP})$$

$$\text{Final Total} = \max(0.00, \text{Subtotal} - \text{Discount} + \text{Tax} + \text{DeliveryFee})$$

---

## 9. Comprehensive REST API Contract

### Authentication & User Endpoints
- `POST /api/auth/register` — Register a customer or owner (Public)
- `POST /api/auth/login` — Authenticate and return JWT + User Profile (Public)
- `GET /api/auth/me` — Return current authenticated session (Authenticated)
- `GET /api/users/profile` — Get profile + saved addresses (Authenticated)
- `PUT /api/users/profile` — Update name/phone (Authenticated)
- `POST /api/users/addresses` — Add saved delivery address (Customer)
- `DELETE /api/users/addresses/{id}` — Delete delivery address (Customer)

### Restaurants & Menus
- `GET /api/restaurants` — Search, filter by cuisine/city/rating with pagination (Public)
- `GET /api/restaurants/{id}` — Restaurant details + menu items grouped by category (Public)
- `POST /api/restaurants` — Create restaurant (Admin)
- `PUT /api/restaurants/{id}` — Update restaurant details (Admin, or Owner if owned)
- `PATCH /api/restaurants/{id}/status` — Toggle active status (Admin, or Owner if owned)
- `GET /api/categories` — List active categories (Public)
- `POST /api/categories` — Create category (Admin)
- `GET /api/menu-items` — Filter menu items (Public)
- `POST /api/menu-items` — Create menu item (Admin, or Owner for owned restaurant)
- `PUT /api/menu-items/{id}` — Edit menu item (Admin, or Owner for owned restaurant)
- `PATCH /api/menu-items/{id}/availability` — Toggle availability (Owner/Admin)
- `DELETE /api/menu-items/{id}` — Soft delete / remove dish (Owner/Admin)

### Cart & Checkout
- `GET /api/cart` — Get current customer's cart with computed subtotal (Customer)
- `POST /api/cart/items` — Add item to cart (validates single restaurant) (Customer)
- `PATCH /api/cart/items/{itemId}` — Update item quantity (Customer)
- `DELETE /api/cart/items/{itemId}` — Remove item (Customer)
- `DELETE /api/cart` — Clear entire cart (Customer)

### Coupons
- `POST /api/coupons/validate` — Validate coupon against cart subtotal (Customer)
- `GET /api/admin/coupons` — List all coupons (Admin)
- `POST /api/admin/coupons` — Create coupon (Admin)
- `PUT /api/admin/coupons/{id}` — Update coupon limits/dates (Admin)

### Orders & Payments
- `POST /api/orders` — Checkout: validates cart, applies coupon, creates snapshot order, clears cart (Customer)
- `GET /api/orders/my` — Customer's paginated order history (Customer)
- `GET /api/orders/{id}` — Order details + tracking timeline (Customer owner, Restaurant owner, Admin)
- `PATCH /api/orders/{id}/cancel` — Cancel order if PLACED (Customer)
- `PATCH /api/owner/orders/{id}/status` — Update lifecycle state (Owner)
- `GET /api/owner/orders` — Owner's incoming orders with status filters (Owner)
- `GET /api/admin/orders` — Platform-wide orders (Admin)

### Reviews & Favorites
- `POST /api/reviews` — Submit review for delivered order (Customer)
- `GET /api/restaurants/{id}/reviews` — Get restaurant reviews (Public)
- `GET /api/favorites` — Get customer's saved restaurants (Customer)
- `POST /api/favorites/{restaurantId}` — Add to favorites (Customer)
- `DELETE /api/favorites/{restaurantId}` — Remove from favorites (Customer)

### Dashboards & Analytics
- `GET /api/admin/dashboard` — Platform stats, revenue, order distribution, top foods (Admin)
- `GET /api/owner/dashboard` — Restaurant stats, pending orders, sales, popular items (Owner)
