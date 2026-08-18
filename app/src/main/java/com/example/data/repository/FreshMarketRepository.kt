package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FreshMarketRepository(
    private val db: AppDatabase
) {
    // -------------------------------------------------------------
    // LOCAL ROOM DATA ACCESS
    // -------------------------------------------------------------
    val shoppingList: Flow<List<ShoppingListItemEntity>> = db.shoppingListDao().getAllListItems()
    val cartItems: Flow<List<CartItemEntity>> = db.cartDao().getCartItems()
    val clippedCoupons: Flow<List<ClippedCouponEntity>> = db.couponDao().getClippedCoupons()
    val orderHistory: Flow<List<OrderEntity>> = db.orderDao().getOrdersHistory()
    val pantryItems: Flow<List<PantryItemEntity>> = db.pantryDao().getPantryItems()
    val scheduledItems: Flow<List<ScheduledGroceryEntity>> = db.scheduleDao().getScheduledItems()

    suspend fun addToList(item: ShoppingListItemEntity) = db.shoppingListDao().insertListItem(item)
    suspend fun updateListItem(item: ShoppingListItemEntity) = db.shoppingListDao().updateListItem(item)
    suspend fun removeFromList(id: String) = db.shoppingListDao().deleteListItem(id)
    suspend fun clearCheckedFromList() = db.shoppingListDao().clearCheckedItems()

    suspend fun addToCart(productId: String, quantity: Int = 1) {
        db.cartDao().insertOrUpdateCartItem(CartItemEntity(productId = productId, quantity = quantity))
    }
    suspend fun updateCartQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            db.cartDao().removeCartItem(productId)
        } else {
            db.cartDao().insertOrUpdateCartItem(CartItemEntity(productId = productId, quantity = quantity))
        }
    }
    suspend fun removeFromCart(productId: String) = db.cartDao().removeCartItem(productId)
    suspend fun clearCart() = db.cartDao().clearCart()

    suspend fun clipCoupon(couponId: String) = db.couponDao().clipCoupon(ClippedCouponEntity(couponId = couponId))
    suspend fun unclipCoupon(couponId: String) = db.couponDao().unclipCoupon(couponId)

    suspend fun recordOrder(order: OrderEntity) = db.orderDao().insertOrder(order)
    suspend fun updatePantry(item: PantryItemEntity) = db.pantryDao().updatePantryItem(item)
    suspend fun updatePantryItem(item: PantryItemEntity) = db.pantryDao().updatePantryItem(item)
    suspend fun saveSchedule(item: ScheduledGroceryEntity) = db.scheduleDao().insertOrUpdateSchedule(item)
    suspend fun removeSchedule(id: String) = db.scheduleDao().deleteSchedule(id)

    // -------------------------------------------------------------
    // STORES CATALOGUE
    // -------------------------------------------------------------
    fun getStores(): List<Store> = listOf(
        Store(
            id = "store_001",
            name = "Albertsons - Market Street",
            address = "1250 Market St, San Francisco, CA",
            distanceMiles = 0.8,
            hours = "6:00 AM - 11:00 PM",
            isOpen = true,
            phone = "(415) 555-0192",
            services = listOf("DriveUp & Go™", "Delivery", "Flash 30-Min", "In-Store", "Pharmacy", "Fuel", "Starbucks", "Floral", "Deli")
        ),
        Store(
            id = "store_002",
            name = "Albertsons - Geary Blvd",
            address = "3800 Geary Blvd, San Francisco, CA",
            distanceMiles = 2.4,
            hours = "6:00 AM - Midnight",
            isOpen = true,
            phone = "(415) 555-0823",
            services = listOf("DriveUp & Go™", "Delivery", "In-Store", "Pharmacy", "Fuel")
        ),
        Store(
            id = "store_003",
            name = "Albertsons - Mission Bay",
            address = "500 Terry Francois Blvd, San Francisco, CA",
            distanceMiles = 3.1,
            hours = "7:00 AM - 10:00 PM",
            isOpen = true,
            phone = "(415) 555-0441",
            services = listOf("DriveUp & Go™", "Delivery", "In-Store", "Pharmacy")
        )
    )

    // -------------------------------------------------------------
    // DEPARTMENTS
    // -------------------------------------------------------------
    fun getDepartments(): List<String> = listOf(
        "Produce", "Meat", "Seafood", "Deli", "Bakery", "Dairy", "Eggs", "Cheese",
        "Pantry", "Canned Goods", "Snacks", "Beverages", "Frozen", "Breakfast",
        "International", "Organic", "Natural", "Baby", "Pet", "Household",
        "Cleaning", "Personal Care", "Health", "Seasonal"
    )

    // -------------------------------------------------------------
    // PRODUCTS CATALOGUE
    // -------------------------------------------------------------
    fun getAllProducts(): List<Product> = listOf(
        // PRODUCE
        Product(
            id = "prod_001",
            name = "Organic Hass Avocados (Bag of 4)",
            brand = "FreshMarket Organic",
            department = "Produce",
            category = "Fruit",
            price = 4.99,
            salePrice = 3.99,
            memberPrice = 3.49,
            unitPriceStr = "$0.87 / count",
            size = "4 ct",
            isOrganic = true,
            dealTag = "SAVE $1.00",
            aisle = "Aisle 1 - Produce",
            calories = 160,
            nutritionInfo = mapOf("Calories" to "160", "Fat" to "15g", "Sodium" to "10mg", "Carbs" to "9g", "Fiber" to "7g"),
            ingredients = "100% Organic Hass Avocados",
            origin = "California, USA"
        ),
        Product(
            id = "prod_002",
            name = "Fresh Organic Bananas",
            brand = "FreshMarket Organic",
            department = "Produce",
            category = "Fruit",
            price = 0.69,
            salePrice = 0.59,
            memberPrice = 0.49,
            unitPriceStr = "$0.59 / lb",
            size = "1 lb (approx. 3 count)",
            isOrganic = true,
            dealTag = "MEMBER PRICE",
            aisle = "Aisle 1 - Produce",
            calories = 105,
            nutritionInfo = mapOf("Calories" to "105", "Potassium" to "422mg", "Carbs" to "27g", "Sugar" to "14g"),
            ingredients = "Fresh Organic Bananas",
            origin = "Ecuador"
        ),
        Product(
            id = "prod_003",
            name = "Honeycrisp Apples",
            brand = "FreshMarket Select",
            department = "Produce",
            category = "Fruit",
            price = 2.99,
            salePrice = 1.99,
            memberPrice = 1.79,
            unitPriceStr = "$1.99 / lb",
            size = "1 lb",
            isOrganic = false,
            dealTag = "WEEKLY SPECIAL",
            aisle = "Aisle 1 - Produce",
            calories = 95,
            nutritionInfo = mapOf("Calories" to "95", "Fiber" to "4.4g", "Vitamin C" to "14%"),
            ingredients = "Honeycrisp Apples",
            origin = "Washington, USA"
        ),
        Product(
            id = "prod_004",
            name = "Organic Baby Spinach & Spring Mix 16oz",
            brand = "Organic Girl",
            department = "Produce",
            category = "Salads",
            price = 5.49,
            salePrice = 4.49,
            memberPrice = 3.99,
            unitPriceStr = "$0.28 / oz",
            size = "16 oz tub",
            isOrganic = true,
            dealTag = "BUY 1 GET 1 50% OFF",
            aisle = "Aisle 1 - Produce",
            calories = 20,
            nutritionInfo = mapOf("Calories" to "20", "Protein" to "2g", "Iron" to "15%"),
            ingredients = "Organic Baby Spinach, Organic Baby Lettuce Mix"
        ),
        Product(
            id = "prod_005",
            name = "Roma Tomatoes on the Vine",
            brand = "FreshMarket Select",
            department = "Produce",
            category = "Vegetables",
            price = 1.99,
            unitPriceStr = "$1.99 / lb",
            size = "1 lb",
            aisle = "Aisle 1 - Produce",
            calories = 22,
            nutritionInfo = mapOf("Calories" to "22", "Lycopene" to "Very High")
        ),

        // MEAT & SEAFOOD
        Product(
            id = "prod_010",
            name = "Boneless Skinless Chicken Breasts Value Pack",
            brand = "FreshMarket Farms",
            department = "Meat",
            category = "Chicken",
            price = 4.99,
            salePrice = 2.99,
            memberPrice = 2.49,
            unitPriceStr = "$2.99 / lb",
            size = "3.5 lb avg",
            dealTag = "HOT OFFER - SAVE $2/lb",
            aisle = "Aisle 5 - Meat & Seafood",
            calories = 165,
            nutritionInfo = mapOf("Calories" to "165", "Protein" to "31g", "Fat" to "3.6g"),
            ingredients = "100% Fresh All-Natural Chicken Breast",
            origin = "USA Farm Raised"
        ),
        Product(
            id = "prod_011",
            name = "USDA Choice Beef Ribeye Steak",
            brand = "FreshMarket Signature Reserve",
            department = "Meat",
            category = "Beef",
            price = 15.99,
            salePrice = 12.99,
            memberPrice = 11.99,
            unitPriceStr = "$12.99 / lb",
            size = "1.2 lb avg",
            dealTag = "STEAKHOUSE CHOICE",
            aisle = "Aisle 5 - Meat & Seafood",
            calories = 290,
            nutritionInfo = mapOf("Calories" to "290", "Protein" to "24g", "Fat" to "21g")
        ),
        Product(
            id = "prod_012",
            name = "Fresh Wild Atlantic Salmon Fillet",
            brand = "Ocean Harvest",
            department = "Seafood",
            category = "Fish",
            price = 12.99,
            salePrice = 9.99,
            memberPrice = 8.99,
            unitPriceStr = "$9.99 / lb",
            size = "1 lb avg",
            dealTag = "SUSTAINABLE CATCH",
            aisle = "Aisle 5 - Meat & Seafood",
            calories = 206,
            nutritionInfo = mapOf("Calories" to "206", "Protein" to "22g", "Omega-3" to "2.1g")
        ),

        // BAKERY & DELI
        Product(
            id = "prod_020",
            name = "Freshly Baked Artisan Sourdough Bread",
            brand = "FreshMarket Bakery",
            department = "Bakery",
            category = "Bread",
            price = 4.49,
            salePrice = 3.49,
            memberPrice = 2.99,
            unitPriceStr = "$0.22 / oz",
            size = "16 oz loaf",
            dealTag = "BAKED DAILY",
            aisle = "Bakery Counter",
            calories = 140,
            ingredients = "Unbleached Wheat Flour, Water, Sea Salt, Starter Culture"
        ),
        Product(
            id = "prod_021",
            name = "Hot Traditional Rotisserie Chicken",
            brand = "FreshMarket Deli",
            department = "Deli",
            category = "Prepared Foods",
            price = 7.99,
            salePrice = 5.99,
            memberPrice = 5.00,
            unitPriceStr = "$5.99 each",
            size = "2 lb avg",
            dealTag = "DELI HOT ITEM",
            aisle = "Deli Counter",
            calories = 220,
            nutritionInfo = mapOf("Calories" to "220", "Protein" to "26g", "Sodium" to "480mg")
        ),
        Product(
            id = "prod_022",
            name = "Boar's Head Ovengold Turkey Breast",
            brand = "Boar's Head",
            department = "Deli",
            category = "Deli Meat",
            price = 11.99,
            unitPriceStr = "$11.99 / lb",
            size = "0.5 lb order",
            aisle = "Deli Counter",
            calories = 60,
            nutritionInfo = mapOf("Calories" to "60", "Protein" to "13g", "Fat" to "1g")
        ),

        // DAIRY & EGGS
        Product(
            id = "prod_030",
            name = "FreshMarket Whole Milk 1 Gallon",
            brand = "FreshMarket Dairy",
            department = "Dairy",
            category = "Milk",
            price = 4.29,
            salePrice = 3.49,
            memberPrice = 3.19,
            unitPriceStr = "$0.03 / fl oz",
            size = "128 fl oz",
            dealTag = "ESSENTIAL SAVINGS",
            aisle = "Aisle 4 - Dairy",
            calories = 150,
            nutritionInfo = mapOf("Calories" to "150", "Protein" to "8g", "Calcium" to "30%"),
            ingredients = "Grade A Pasteurized Whole Milk, Vitamin D3"
        ),
        Product(
            id = "prod_031",
            name = "Grade A Large White Eggs 12ct",
            brand = "Lucerne",
            department = "Eggs",
            category = "Eggs",
            price = 3.99,
            salePrice = 2.99,
            memberPrice = 2.49,
            unitPriceStr = "$0.25 / egg",
            size = "12 ct carton",
            dealTag = "WEEKLY MUST-HAVE",
            aisle = "Aisle 4 - Dairy",
            calories = 70,
            nutritionInfo = mapOf("Calories" to "70", "Protein" to "6g", "Fat" to "5g")
        ),
        Product(
            id = "prod_032",
            name = "Chobani Plain Non-Fat Greek Yogurt 32oz",
            brand = "Chobani",
            department = "Dairy",
            category = "Yogurt",
            price = 5.29,
            salePrice = 4.29,
            memberPrice = 3.99,
            unitPriceStr = "$0.13 / oz",
            size = "32 oz tub",
            isGlutenFree = true,
            dealTag = "SAVE $1.00",
            aisle = "Aisle 4 - Dairy",
            calories = 90,
            nutritionInfo = mapOf("Calories" to "90", "Protein" to "16g", "Sugar" to "4g")
        ),

        // PANTRY & CANNED GOODS
        Product(
            id = "prod_040",
            name = "Barilla Spaghetti Pasta 16oz",
            brand = "Barilla",
            department = "Pantry",
            category = "Pasta",
            price = 1.99,
            salePrice = 1.25,
            memberPrice = 1.00,
            unitPriceStr = "$0.08 / oz",
            size = "16 oz box",
            dealTag = "4 FOR $5",
            aisle = "Aisle 6 - Pasta & Sauce",
            calories = 200,
            nutritionInfo = mapOf("Calories" to "200", "Carbs" to "42g", "Protein" to "7g")
        ),
        Product(
            id = "prod_041",
            name = "Rao's Homemade Marinara Sauce 24oz",
            brand = "Rao's Homemade",
            department = "Pantry",
            category = "Pasta Sauce",
            price = 8.99,
            salePrice = 6.99,
            memberPrice = 6.49,
            unitPriceStr = "$0.29 / oz",
            size = "24 oz jar",
            isGlutenFree = true,
            dealTag = "PREMIUM FAVORITE",
            aisle = "Aisle 6 - Pasta & Sauce",
            calories = 100,
            nutritionInfo = mapOf("Calories" to "100", "Sodium" to "420mg", "Sugar" to "4g")
        ),
        Product(
            id = "prod_042",
            name = "Starbucks Pike Place Roast Ground Coffee 12oz",
            brand = "Starbucks",
            department = "Breakfast",
            category = "Coffee",
            price = 9.99,
            salePrice = 7.99,
            memberPrice = 7.49,
            unitPriceStr = "$0.66 / oz",
            size = "12 oz bag",
            dealTag = "SAVE $2.00",
            aisle = "Aisle 3 - Coffee & Tea",
            calories = 0,
            ingredients = "100% Arabica Coffee"
        ),

        // SNACKS & BEVERAGES
        Product(
            id = "prod_050",
            name = "LaCroix Sparkling Water Lime 12 Pack",
            brand = "LaCroix",
            department = "Beverages",
            category = "Sparkling Water",
            price = 5.99,
            salePrice = 3.99,
            memberPrice = 3.50,
            unitPriceStr = "$0.33 / can",
            size = "12 x 12 fl oz cans",
            isGlutenFree = true,
            dealTag = "BUY 2 GET 1 FREE",
            aisle = "Aisle 2 - Beverages",
            calories = 0,
            ingredients = "Sparkling Water, Natural Flavors"
        ),
        Product(
            id = "prod_051",
            name = "Doritos Nacho Cheese Tortilla Chips 9.25oz",
            brand = "Doritos",
            department = "Snacks",
            category = "Chips",
            price = 4.99,
            salePrice = 2.99,
            memberPrice = 2.49,
            unitPriceStr = "$0.32 / oz",
            size = "9.25 oz bag",
            dealTag = "MIX & MATCH SALE",
            aisle = "Aisle 7 - Snacks",
            calories = 150,
            nutritionInfo = mapOf("Calories" to "150", "Sodium" to "210mg", "Carbs" to "18g")
        ),

        // FROZEN
        Product(
            id = "prod_060",
            name = "DiGiorno Rising Crust Pepperoni Pizza",
            brand = "DiGiorno",
            department = "Frozen",
            category = "Frozen Pizza",
            price = 7.99,
            salePrice = 5.99,
            memberPrice = 4.99,
            unitPriceStr = "$0.21 / oz",
            size = "27.5 oz box",
            dealTag = "HOT FROZEN DEAL",
            aisle = "Aisle 9 - Frozen Foods",
            calories = 310,
            nutritionInfo = mapOf("Calories" to "310", "Protein" to "13g", "Fat" to "13g")
        ),
        Product(
            id = "prod_061",
            name = "Ben & Jerry's Half Baked Ice Cream 1 Pint",
            brand = "Ben & Jerry's",
            department = "Frozen",
            category = "Ice Cream",
            price = 5.99,
            salePrice = 3.99,
            memberPrice = 3.49,
            unitPriceStr = "$0.25 / oz",
            size = "16 fl oz pint",
            dealTag = "2 FOR $7",
            aisle = "Aisle 9 - Frozen Foods",
            calories = 370
        ),

        // HOUSEHOLD & HEALTH
        Product(
            id = "prod_070",
            name = "Bounty Paper Towels Select-A-Size 6 Double Rolls",
            brand = "Bounty",
            department = "Household",
            category = "Paper Goods",
            price = 14.99,
            salePrice = 12.99,
            memberPrice = 11.99,
            unitPriceStr = "$2.16 / roll",
            size = "6 pack",
            dealTag = "CLIP $2 COUPON",
            aisle = "Aisle 11 - Paper & Cleaning",
            calories = 0
        ),
        Product(
            id = "prod_071",
            name = "Tide PODS Liquid Laundry Detergent 42ct",
            brand = "Tide",
            department = "Cleaning",
            category = "Laundry",
            price = 13.99,
            salePrice = 11.99,
            memberPrice = 10.99,
            unitPriceStr = "$0.28 / pod",
            size = "42 ct tub",
            dealTag = "CLEANING EVENT",
            aisle = "Aisle 11 - Paper & Cleaning"
        )
    )

    // -------------------------------------------------------------
    // COUPONS
    // -------------------------------------------------------------
    fun getDigitalCoupons(): List<Coupon> = listOf(
        Coupon("c_101", "SAVE $2.00 ON O ORGANICS® AVOCADOS", "for U® Deal: Save $2.00 when you buy any 2 bags of O Organics® Hass Avocados", "Produce", 2.00, "Buy 2 Bags", "Exp 09/30/2026"),
        Coupon("c_102", "SAVE $1.50 ON OPEN NATURE® CHICKEN", "for U® Deal: Save $1.50 on Open Nature® Value Pack Chicken Breasts (3lb+)", "Meat", 1.50, "Buy 1 Value Pack", "Exp 09/15/2026"),
        Coupon("c_103", "$1.00 OFF LUCERNE® MILK", "for U® Deal: Save $1.00 on any Lucerne® 1 Gallon Whole or 2% Milk", "Dairy", 1.00, "Buy 1 Gallon", "Exp 09/20/2026"),
        Coupon("c_104", "$0.75 OFF SIGNATURE SELECT® PASTA & SAUCE", "Save $0.75 when you buy Signature SELECT® Spaghetti and Pasta Sauce", "Pantry", 0.75, "Buy 2 items", "Exp 10/01/2026"),
        Coupon("c_105", "$2.00 OFF TIDE PODS", "for U® Coupon: Save $2.00 instantly at checkout on Tide Pods 42ct+", "Household", 2.00, "Buy 1 pack", "Exp 09/25/2026"),
        Coupon("c_106", "BUY 2 GET 1 FREE SOLEIL® SPARKLING", "for U® Coupon: Buy 2 Get 1 Free on all Soleil® 12-pack sparkling waters", "Beverages", 3.99, "Buy 3 packs", "Exp 09/18/2026")
    )

    // -------------------------------------------------------------
    // REWARDS OFFERS
    // -------------------------------------------------------------
    fun getRewardOffers(): List<RewardOffer> = listOf(
        RewardOffer("r_01", "$5 OFF YOUR NEXT GROCERY ORDER", 500, RewardType.GROCERY_OFF, "$5.00 OFF", "Redeem 500 for U® Points for $5 off your total basket."),
        RewardOffer("r_02", "20¢ / GALLON GAS DISCOUNT", 200, RewardType.GAS_DISCOUNT, "20¢/gal OFF", "Redeem 200 for U® Points for 20¢ per gallon savings at Albertsons Express Fuel."),
        RewardOffer("r_03", "FREE ROTISSERIE CHICKEN", 600, RewardType.FREE_ITEM, "FREE ITEM", "Redeem 600 for U® Points for 1 hot Signature SELECT® Rotisserie Chicken."),
        RewardOffer("r_04", "FREE O ORGANICS® HASS AVOCADO 4-PACK", 400, RewardType.FREE_ITEM, "FREE ITEM", "Redeem 400 for U® Points for a bag of O Organics® Avocados.")
    )

    // -------------------------------------------------------------
    // RECIPES
    // -------------------------------------------------------------
    fun getRecipes(): List<Recipe> = listOf(
        Recipe(
            id = "rec_001",
            title = "15-Minute Creamy Garlic Chicken Spaghetti",
            category = "Quick Dinners",
            prepTimeMinutes = 15,
            servings = 4,
            caloriesPerServing = 520,
            estimatedCostForFour = 11.50,
            ingredients = listOf(
                RecipeIngredient("prod_010", "Chicken Breasts", "1.5 lbs"),
                RecipeIngredient("prod_040", "Barilla Spaghetti", "16 oz box"),
                RecipeIngredient("prod_041", "Rao's Marinara Sauce", "1 jar (24 oz)"),
                RecipeIngredient("prod_005", "Roma Tomatoes", "2 medium")
            ),
            instructions = listOf(
                "Boil Barilla spaghetti in salted water for 9-11 minutes until al dente.",
                "Dice chicken breast into bite-sized pieces and sear in hot skillet with olive oil for 6 minutes.",
                "Pour Rao's Marinara Sauce over chicken and simmer for 3 minutes.",
                "Toss pasta into sauce and garnish with fresh tomatoes. Serve hot!"
            ),
            dealsCount = 3
        ),
        Recipe(
            id = "rec_002",
            title = "Fresh Organic Avocado & Spinach Protein Bowl",
            category = "Healthy",
            prepTimeMinutes = 10,
            servings = 2,
            caloriesPerServing = 380,
            estimatedCostForFour = 9.20,
            ingredients = listOf(
                RecipeIngredient("prod_001", "Organic Hass Avocados", "2 avocados"),
                RecipeIngredient("prod_004", "Organic Baby Spinach", "4 oz"),
                RecipeIngredient("prod_031", "Large Eggs", "4 poached eggs"),
                RecipeIngredient("prod_032", "Chobani Greek Yogurt", "1/2 cup for dressing")
            ),
            instructions = listOf(
                "Arrange fresh organic spinach and spring mix into serving bowls.",
                "Slice organic Hass avocados and place on top.",
                "Poach 4 eggs in simmering water with vinegar for 3 minutes.",
                "Drizzle Greek yogurt herb dressing over top and serve immediately."
            ),
            dealsCount = 2
        ),
        Recipe(
            id = "rec_003",
            title = "Classic Taco Night with Guacamole",
            category = "Family",
            prepTimeMinutes = 20,
            servings = 4,
            caloriesPerServing = 610,
            estimatedCostForFour = 14.80,
            ingredients = listOf(
                RecipeIngredient("prod_001", "Organic Hass Avocados", "3 count"),
                RecipeIngredient("prod_010", "Chicken Breasts (or Ground Beef)", "1 lb"),
                RecipeIngredient("prod_005", "Roma Tomatoes", "3 count"),
                RecipeIngredient("prod_030", "FreshMarket Cheese & Milk", "As needed")
            ),
            instructions = listOf(
                "Cook meat with taco seasoning in skillet for 10 minutes.",
                "Mash avocados with chopped Roma tomatoes, lime juice, and cilantro for fresh guacamole.",
                "Warm taco shells and serve with shredded lettuce, cheese, and salsa."
            ),
            dealsCount = 4
        )
    )

    // -------------------------------------------------------------
    // PHARMACY DEMO DATA
    // -------------------------------------------------------------
    fun getPrescriptions(): List<Prescription> = listOf(
        Prescription("rx_101", "RX-8849201", "Amoxicillin 500mg", "1 capsule 3x daily", 30, "Dr. Sarah Jenkins", 2, "07/15/2026", "Ready for Pickup", 5.00),
        Prescription("rx_102", "RX-9921043", "Atorvastatin 20mg", "1 tablet daily at bedtime", 90, "Dr. Robert Vance", 3, "06/01/2026", "Refill Due", 0.00),
        Prescription("rx_103", "RX-3301294", "Lisnopril 10mg", "1 tablet morning", 30, "Dr. Robert Vance", 1, "08/02/2026", "Processing", 2.50)
    )

    // -------------------------------------------------------------
    // FUEL STATIONS DEMO DATA
    // -------------------------------------------------------------
    fun getFuelStations(): List<FuelStation> = listOf(
        FuelStation("fuel_1", "Albertsons Express Fuel - Market St", "1250 Market St, San Francisco, CA", 0.8, 4.29, 0.20, true),
        FuelStation("fuel_2", "Albertsons Express Fuel - Geary", "3800 Geary Blvd, San Francisco, CA", 2.4, 4.25, 0.20, true)
    )

    // -------------------------------------------------------------
    // INITIAL DEMO PANTRY SEED DATA
    // -------------------------------------------------------------
    fun getInitialPantryItems(): List<PantryItemEntity> = listOf(
        PantryItemEntity("p1", "Whole Milk 1 Gal", "Dairy", "RUNNING_LOW"),
        PantryItemEntity("p2", "Grade A Eggs 12ct", "Eggs", "HAVE"),
        PantryItemEntity("p3", "Barilla Spaghetti", "Pantry", "HAVE"),
        PantryItemEntity("p4", "Starbucks Coffee", "Breakfast", "NEED"),
        PantryItemEntity("p5", "Paper Towels 6pk", "Household", "RUNNING_LOW")
    )
}
