package com.foodiehub.init;

import com.foodiehub.category.Category;
import com.foodiehub.category.CategoryRepository;
import com.foodiehub.coupon.Coupon;
import com.foodiehub.coupon.CouponRepository;
import com.foodiehub.menu.MenuItem;
import com.foodiehub.menu.MenuItemRepository;
import com.foodiehub.order.*;
import com.foodiehub.restaurant.Restaurant;
import com.foodiehub.restaurant.RestaurantRepository;
import com.foodiehub.review.Review;
import com.foodiehub.review.ReviewRepository;
import com.foodiehub.user.Address;
import com.foodiehub.user.User;
import com.foodiehub.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final CategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final CouponRepository couponRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded. Skipping initial data seeding.");
            return;
        }

        log.info("Starting FoodieHub initial seed data insertion...");

        String defaultHash = passwordEncoder.encode("Password@123");

        // 1. Seed Users
        User admin = userRepository.save(User.builder()
                .fullName("System Administrator")
                .email("admin@foodiehub.com")
                .passwordHash(defaultHash)
                .phone("+919800000001")
                .role("ROLE_ADMIN")
                .accountStatus("ACTIVE")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        User owner1 = userRepository.save(User.builder()
                .fullName("Rajesh Verma")
                .email("rajesh@foodiehub.com")
                .passwordHash(defaultHash)
                .phone("+919800000002")
                .role("ROLE_RESTAURANT_OWNER")
                .accountStatus("ACTIVE")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        User owner2 = userRepository.save(User.builder()
                .fullName("Priya Sharma")
                .email("priya@foodiehub.com")
                .passwordHash(defaultHash)
                .phone("+919800000003")
                .role("ROLE_RESTAURANT_OWNER")
                .accountStatus("ACTIVE")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        Address customerAddress = Address.builder()
                .id(UUID.randomUUID().toString())
                .label("Home")
                .street("Flat 402, Lotus Residency, Road 10")
                .city("Hyderabad")
                .state("Telangana")
                .zipCode("500034")
                .contactPhone("+919876543210")
                .isDefault(true)
                .build();

        List<Address> customerAddresses = new ArrayList<>();
        customerAddresses.add(customerAddress);

        User customer1 = userRepository.save(User.builder()
                .fullName("Bala Krishna")
                .email("bala@example.com")
                .passwordHash(defaultHash)
                .phone("+919876543210")
                .role("ROLE_CUSTOMER")
                .accountStatus("ACTIVE")
                .addresses(customerAddresses)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        User customer2 = userRepository.save(User.builder()
                .fullName("Ananya Reddy")
                .email("ananya@example.com")
                .passwordHash(defaultHash)
                .phone("+919876543211")
                .role("ROLE_CUSTOMER")
                .accountStatus("ACTIVE")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        // 2. Seed Categories
        Category catBiryani = categoryRepository.save(Category.builder().name("Biryani").description("Fragrant aromatic rice with spiced meats & vegetables").isActive(true).build());
        Category catNorthIndian = categoryRepository.save(Category.builder().name("North Indian").description("Rich buttery gravies, paneer, and tandoori breads").isActive(true).build());
        Category catSouthIndian = categoryRepository.save(Category.builder().name("South Indian").description("Steamed idlis, crispy dosas, and filter coffee").isActive(true).build());
        Category catPizza = categoryRepository.save(Category.builder().name("Pizza & Italian").description("Woodfired pizzas and artisan pasta").isActive(true).build());
        Category catDesserts = categoryRepository.save(Category.builder().name("Desserts & Sweets").description("Traditional and modern desserts").isActive(true).build());

        // 3. Seed Restaurants
        Restaurant res1 = restaurantRepository.save(Restaurant.builder()
                .name("Hyderabadi Spice Villa")
                .description("Authentic royal dum biryani, kebabs, and Mughlai curries.")
                .cuisineTypes(List.of("Biryani", "Mughlai", "North Indian"))
                .address("Road No 1, Banjara Hills")
                .city("Hyderabad")
                .phone("+919876500001")
                .ownerId(owner1.getId())
                .imageUrl("https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=600&auto=format&fit=crop&q=60")
                .averageRating(4.8)
                .totalReviews(142)
                .deliveryFee(new BigDecimal("40.00"))
                .estimatedDeliveryMinutes(35)
                .isActive(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        Restaurant res2 = restaurantRepository.save(Restaurant.builder()
                .name("Flavors of Punjab")
                .description("Rich buttery gravies, charcoal tandoori grills, and Amritsari breads.")
                .cuisineTypes(List.of("North Indian", "Punjabi"))
                .address("Plot 42, Jubilee Hills")
                .city("Hyderabad")
                .phone("+919876500002")
                .ownerId(owner2.getId())
                .imageUrl("https://images.unsplash.com/photo-1589302168068-964664d93dc0?w=600&auto=format&fit=crop&q=60")
                .averageRating(4.5)
                .totalReviews(89)
                .deliveryFee(new BigDecimal("45.00"))
                .estimatedDeliveryMinutes(40)
                .isActive(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        Restaurant res3 = restaurantRepository.save(Restaurant.builder()
                .name("Dakshin Kitchen")
                .description("Crispy ghee podi dosas, fluffy idlis, and authentic filter coffee.")
                .cuisineTypes(List.of("South Indian", "Tiffins"))
                .address("Madhapur Main Road")
                .city("Hyderabad")
                .phone("+919876500003")
                .ownerId(owner1.getId())
                .imageUrl("https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=600&auto=format&fit=crop&q=60")
                .averageRating(4.7)
                .totalReviews(215)
                .deliveryFee(new BigDecimal("30.00"))
                .estimatedDeliveryMinutes(25)
                .isActive(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        Restaurant res4 = restaurantRepository.save(Restaurant.builder()
                .name("Bella Napoli Pizza")
                .description("Artisan woodfired pizzas with San Marzano tomatoes and mozzarella.")
                .cuisineTypes(List.of("Pizza", "Italian"))
                .address("Gachibowli High Street")
                .city("Hyderabad")
                .phone("+919876500004")
                .ownerId(owner2.getId())
                .imageUrl("https://images.unsplash.com/photo-1513104890138-7c749659a591?w=600&auto=format&fit=crop&q=60")
                .averageRating(4.6)
                .totalReviews(98)
                .deliveryFee(new BigDecimal("50.00"))
                .estimatedDeliveryMinutes(30)
                .isActive(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        // 4. Seed 20+ Menu Items
        MenuItem m1 = menuItemRepository.save(MenuItem.builder().restaurantId(res1.getId()).categoryId(catBiryani.getId()).name("Special Chicken Dum Biryani").description("Fragrant basmati rice layered with slow cooked tender chicken & saffron.").price(new BigDecimal("290.00")).imageUrl("https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=500&auto=format&fit=crop&q=60").foodType("NON_VEG").isAvailable(true).preparationTimeMinutes(30).build());
        MenuItem m2 = menuItemRepository.save(MenuItem.builder().restaurantId(res1.getId()).categoryId(catBiryani.getId()).name("Hyderabadi Mutton Biryani").description("Classic spiced goat meat dum biryani served with mirchi ka salan.").price(new BigDecimal("380.00")).imageUrl("https://images.unsplash.com/photo-1633945274405-b6c8069047b0?w=500&auto=format&fit=crop&q=60").foodType("NON_VEG").isAvailable(true).preparationTimeMinutes(35).build());
        MenuItem m3 = menuItemRepository.save(MenuItem.builder().restaurantId(res1.getId()).categoryId(catBiryani.getId()).name("Paneer Tikka Biryani").description("Charcoal roasted cottage cheese cubes dum cooked in spiced basmati rice.").price(new BigDecimal("250.00")).imageUrl("https://images.unsplash.com/photo-1645177628172-a94c1f96e6db?w=500&auto=format&fit=crop&q=60").foodType("VEG").isAvailable(true).preparationTimeMinutes(25).build());
        MenuItem m4 = menuItemRepository.save(MenuItem.builder().restaurantId(res1.getId()).categoryId(catNorthIndian.getId()).name("Butter Garlic Naan").description("Tandoor baked flatbread glazed with melted garlic butter.").price(new BigDecimal("65.00")).imageUrl("https://images.unsplash.com/photo-1601050690597-df0568f70950?w=500&auto=format&fit=crop&q=60").foodType("VEG").isAvailable(true).preparationTimeMinutes(15).build());
        MenuItem m5 = menuItemRepository.save(MenuItem.builder().restaurantId(res1.getId()).categoryId(catNorthIndian.getId()).name("Chicken Tikka Kebab (6 Pcs)").description("Smoked boneless chicken chunks marinated in yogurt & Kashmiri red chili.").price(new BigDecimal("310.00")).imageUrl("https://images.unsplash.com/photo-1599488615731-7e5c2823ff28?w=500&auto=format&fit=crop&q=60").foodType("NON_VEG").isAvailable(true).preparationTimeMinutes(20).build());
        MenuItem m6 = menuItemRepository.save(MenuItem.builder().restaurantId(res1.getId()).categoryId(catDesserts.getId()).name("Double Ka Meetha").description("Traditional Hyderabadi fried bread pudding steeped in saffron milk & nuts.").price(new BigDecimal("120.00")).imageUrl("https://images.unsplash.com/photo-1517244683847-7456b63c5969?w=500&auto=format&fit=crop&q=60").foodType("VEG").isAvailable(true).preparationTimeMinutes(10).build());

        // Restaurant 2 Items (Flavors of Punjab)
        menuItemRepository.save(MenuItem.builder().restaurantId(res2.getId()).categoryId(catNorthIndian.getId()).name("Paneer Butter Masala").description("Tender paneer cubes simmered in creamy tomato-cashew satin gravy.").price(new BigDecimal("240.00")).imageUrl("https://images.unsplash.com/photo-1631452180519-c014fe946bc7?w=500&auto=format&fit=crop&q=60").foodType("VEG").isAvailable(true).preparationTimeMinutes(20).build());
        menuItemRepository.save(MenuItem.builder().restaurantId(res2.getId()).categoryId(catNorthIndian.getId()).name("Dal Makhani").description("Black lentils slow cooked overnight with churned butter and cream.").price(new BigDecimal("210.00")).imageUrl("https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=500&auto=format&fit=crop&q=60").foodType("VEG").isAvailable(true).preparationTimeMinutes(20).build());
        menuItemRepository.save(MenuItem.builder().restaurantId(res2.getId()).categoryId(catNorthIndian.getId()).name("Amritsari Kulcha with Chole").description("Crispy layered potato-onion stuffed bread served with spicy chickpea curry.").price(new BigDecimal("180.00")).imageUrl("https://images.unsplash.com/photo-1626777552726-4a6b54c97e46?w=500&auto=format&fit=crop&q=60").foodType("VEG").isAvailable(true).preparationTimeMinutes(15).build());
        menuItemRepository.save(MenuItem.builder().restaurantId(res2.getId()).categoryId(catNorthIndian.getId()).name("Butter Chicken").description("Tandoori chicken pieces in velvety, mildly spiced buttery tomato curry.").price(new BigDecimal("320.00")).imageUrl("https://images.unsplash.com/photo-1588166524941-3bf61a9c41db?w=500&auto=format&fit=crop&q=60").foodType("NON_VEG").isAvailable(true).preparationTimeMinutes(25).build());
        menuItemRepository.save(MenuItem.builder().restaurantId(res2.getId()).categoryId(catDesserts.getId()).name("Gulab Jamun (2 Pcs)").description("Warm fried milk dumplings soaked in cardamom sugar syrup.").price(new BigDecimal("80.00")).imageUrl("https://images.unsplash.com/photo-1601050690117-94f5f6fa8bd7?w=500&auto=format&fit=crop&q=60").foodType("VEG").isAvailable(true).preparationTimeMinutes(5).build());

        // Restaurant 3 Items (Dakshin Kitchen)
        menuItemRepository.save(MenuItem.builder().restaurantId(res3.getId()).categoryId(catSouthIndian.getId()).name("Ghee Karam Dosa").description("Crisp golden rice crepe coated with spicy Guntur lentil podi and pure ghee.").price(new BigDecimal("110.00")).imageUrl("https://images.unsplash.com/photo-1668236543090-82eba5ee5976?w=500&auto=format&fit=crop&q=60").foodType("VEG").isAvailable(true).preparationTimeMinutes(15).build());
        menuItemRepository.save(MenuItem.builder().restaurantId(res3.getId()).categoryId(catSouthIndian.getId()).name("Button Idli Sambar (12 Pcs)").description("Mini steamed rice cakes immersed in piping hot aromatic lentil stew.").price(new BigDecimal("90.00")).imageUrl("https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=500&auto=format&fit=crop&q=60").foodType("VEG").isAvailable(true).preparationTimeMinutes(10).build());
        menuItemRepository.save(MenuItem.builder().restaurantId(res3.getId()).categoryId(catSouthIndian.getId()).name("Medu Vada (2 Pcs)").description("Golden fried lentil donuts served with coconut chutney & sambar.").price(new BigDecimal("80.00")).imageUrl("https://images.unsplash.com/photo-1626074353765-517a681e40be?w=500&auto=format&fit=crop&q=60").foodType("VEG").isAvailable(true).preparationTimeMinutes(10).build());
        menuItemRepository.save(MenuItem.builder().restaurantId(res3.getId()).categoryId(catSouthIndian.getId()).name("South Indian Filter Coffee").description("Freshly brewed chicory blend frothed with hot full-cream milk.").price(new BigDecimal("50.00")).imageUrl("https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=500&auto=format&fit=crop&q=60").foodType("VEG").isAvailable(true).preparationTimeMinutes(5).build());

        // Restaurant 4 Items (Bella Napoli Pizza)
        menuItemRepository.save(MenuItem.builder().restaurantId(res4.getId()).categoryId(catPizza.getId()).name("Margherita Pizza").description("San Marzano tomato sauce, fresh mozzarella, extra virgin olive oil & basil.").price(new BigDecimal("340.00")).imageUrl("https://images.unsplash.com/photo-1604382354936-07c5d9983bd3?w=500&auto=format&fit=crop&q=60").foodType("VEG").isAvailable(true).preparationTimeMinutes(20).build());
        menuItemRepository.save(MenuItem.builder().restaurantId(res4.getId()).categoryId(catPizza.getId()).name("Pepperoni Woodfired Pizza").description("Crispy smoked pork pepperoni slices, mozzarella, and chili flakes.").price(new BigDecimal("420.00")).imageUrl("https://images.unsplash.com/photo-1628840042765-356cda07504e?w=500&auto=format&fit=crop&q=60").foodType("NON_VEG").isAvailable(true).preparationTimeMinutes(20).build());
        menuItemRepository.save(MenuItem.builder().restaurantId(res4.getId()).categoryId(catPizza.getId()).name("Truffle Mushroom Pizza").description("Wild mushrooms, black truffle oil, thyme, and fontina cheese.").price(new BigDecimal("390.00")).imageUrl("https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=500&auto=format&fit=crop&q=60").foodType("VEG").isAvailable(true).preparationTimeMinutes(20).build());
        menuItemRepository.save(MenuItem.builder().restaurantId(res4.getId()).categoryId(catPizza.getId()).name("Cheesy Garlic Breadsticks").description("Baked sourdough sticks stuffed with garlic herb butter & melted cheese.").price(new BigDecimal("160.00")).imageUrl("https://images.unsplash.com/photo-1619895092538-128341789043?w=500&auto=format&fit=crop&q=60").foodType("VEG").isAvailable(true).preparationTimeMinutes(15).build());

        // 5. Seed Coupons
        couponRepository.save(Coupon.builder()
                .code("FOODIE50")
                .description("10% OFF up to ₹60 on orders above ₹300")
                .discountType("PERCENTAGE")
                .discountValue(new BigDecimal("10.00"))
                .minimumOrderValue(new BigDecimal("300.00"))
                .maximumDiscount(new BigDecimal("60.00"))
                .startDate(Instant.now().minusSeconds(86400))
                .expiryDate(Instant.now().plusSeconds(86400 * 60))
                .usageLimit(500)
                .usedCount(1)
                .isActive(true)
                .build());

        couponRepository.save(Coupon.builder()
                .code("WELCOME20")
                .description("Flat ₹50 OFF on orders above ₹250")
                .discountType("FLAT")
                .discountValue(new BigDecimal("50.00"))
                .minimumOrderValue(new BigDecimal("250.00"))
                .startDate(Instant.now().minusSeconds(86400))
                .expiryDate(Instant.now().plusSeconds(86400 * 60))
                .usageLimit(1000)
                .usedCount(0)
                .isActive(true)
                .build());

        couponRepository.save(Coupon.builder()
                .code("TASTY100")
                .description("Flat ₹100 OFF on orders above ₹500")
                .discountType("FLAT")
                .discountValue(new BigDecimal("100.00"))
                .minimumOrderValue(new BigDecimal("500.00"))
                .startDate(Instant.now().minusSeconds(86400))
                .expiryDate(Instant.now().plusSeconds(86400 * 60))
                .usageLimit(200)
                .usedCount(0)
                .isActive(true)
                .build());

        // 6. Seed Sample Order & Review
        OrderItemSnapshot snapshotItem = OrderItemSnapshot.builder()
                .menuItemId(m1.getId())
                .name(m1.getName())
                .price(m1.getPrice())
                .quantity(2)
                .subtotal(m1.getPrice().multiply(BigDecimal.valueOf(2)))
                .imageUrl(m1.getImageUrl())
                .foodType(m1.getFoodType())
                .build();

        PricingBreakdown samplePricing = PricingBreakdown.builder()
                .subtotal(new BigDecimal("580.00"))
                .discount(new BigDecimal("58.00"))
                .tax(new BigDecimal("26.10"))
                .deliveryFee(new BigDecimal("40.00"))
                .finalTotal(new BigDecimal("588.10"))
                .build();

        List<StatusLog> sampleTimeline = new ArrayList<>();
        sampleTimeline.add(StatusLog.builder().status("PLACED").timestamp(Instant.now().minusSeconds(3600)).updatedBy("Bala Krishna").note("Order placed").build());
        sampleTimeline.add(StatusLog.builder().status("CONFIRMED").timestamp(Instant.now().minusSeconds(3000)).updatedBy("Rajesh Verma").note("Kitchen confirmed").build());
        sampleTimeline.add(StatusLog.builder().status("PREPARING").timestamp(Instant.now().minusSeconds(2400)).updatedBy("Rajesh Verma").note("Chef cooking").build());
        sampleTimeline.add(StatusLog.builder().status("READY_FOR_PICKUP").timestamp(Instant.now().minusSeconds(1800)).updatedBy("Rajesh Verma").note("Packaged").build());
        sampleTimeline.add(StatusLog.builder().status("OUT_FOR_DELIVERY").timestamp(Instant.now().minusSeconds(1200)).updatedBy("Rajesh Verma").note("Delivery rider assigned").build());
        sampleTimeline.add(StatusLog.builder().status("DELIVERED").timestamp(Instant.now().minusSeconds(300)).updatedBy("Rajesh Verma").note("Delivered to customer").build());

        Order deliveredOrder = orderRepository.save(Order.builder()
                .orderNumber("FH-20261012-1001")
                .customerId(customer1.getId())
                .customerName(customer1.getFullName())
                .customerEmail(customer1.getEmail())
                .restaurantId(res1.getId())
                .restaurantName(res1.getName())
                .items(List.of(snapshotItem))
                .deliveryAddressSnapshot(customerAddress)
                .pricingBreakdown(samplePricing)
                .couponCode("FOODIE50")
                .paymentMethod("SIMULATED_ONLINE")
                .paymentStatus("COMPLETED")
                .transactionId("SIM-TXN-SEED-9912")
                .orderStatus("DELIVERED")
                .timeline(sampleTimeline)
                .createdAt(Instant.now().minusSeconds(3600))
                .updatedAt(Instant.now().minusSeconds(300))
                .build());

        reviewRepository.save(Review.builder()
                .userId(customer1.getId())
                .userName(customer1.getFullName())
                .restaurantId(res1.getId())
                .orderId(deliveredOrder.getId())
                .rating(5)
                .comment("Incredible authentic dum biryani! Arrived piping hot in less than 30 minutes.")
                .createdAt(Instant.now().minusSeconds(180))
                .build());

        log.info("FoodieHub database successfully seeded with Users, Restaurants, Menus, Coupons, Orders and Reviews!");
    }
}
