package com.example.data

object ZivoraCatalog {

    val popularLocations = listOf(
        "Central Plaza, Sector 14",
        "Cyber Hub, Tower B",
        "Metro Station Gate 2",
        "City Square Mall",
        "Green Park Residency",
        "Tech Park Phase 3",
        "Railway Terminus East",
        "Domestic Airport T1"
    )

    fun estimateDistanceKm(pickup: String, drop: String): Double {
        if (pickup.isBlank() || drop.isBlank() || pickup.equals(drop, ignoreCase = true)) {
            return 2.5
        }
        val hash = kotlin.math.abs((pickup.trim().lowercase() + "|" + drop.trim().lowercase()).hashCode())
        val raw = 2.4 + (hash % 180) / 10.0
        return kotlin.math.round(raw * 10.0) / 10.0
    }

    val groceryCategories = listOf(
        "All",
        "Fresh Produce",
        "Dairy & Bakery",
        "Staples & Grains",
        "Snacks & Drinks"
    )

    val groceryItems = listOf(
        GroceryItem(
            id = "g1",
            name = "Organic Hass Avocados",
            category = "Fresh Produce",
            unitWeight = "2 pcs (350g)",
            price = 189.0,
            mrp = 220.0,
            rating = 4.8,
            badge = "Farm Fresh"
        ),
        GroceryItem(
            id = "g2",
            name = "Vine-Ripened Tomatoes",
            category = "Fresh Produce",
            unitWeight = "500 g",
            price = 42.0,
            mrp = 55.0,
            rating = 4.6,
            badge = "24% OFF"
        ),
        GroceryItem(
            id = "g3",
            name = "Baby Spinach & Kale Mix",
            category = "Fresh Produce",
            unitWeight = "250 g",
            price = 79.0,
            mrp = 95.0,
            rating = 4.7
        ),
        GroceryItem(
            id = "g4",
            name = "A2 Gir Cow Fresh Milk",
            category = "Dairy & Bakery",
            unitWeight = "1 Litre",
            price = 92.0,
            mrp = 98.0,
            rating = 4.9,
            badge = "Daily Essential"
        ),
        GroceryItem(
            id = "g5",
            name = "Artisan Sourdough Loaf",
            category = "Dairy & Bakery",
            unitWeight = "400 g",
            price = 145.0,
            mrp = 175.0,
            rating = 4.8,
            badge = "Baked Today"
        ),
        GroceryItem(
            id = "g6",
            name = "Greek Strained Yogurt",
            category = "Dairy & Bakery",
            unitWeight = "400 g",
            price = 135.0,
            mrp = 150.0,
            rating = 4.7
        ),
        GroceryItem(
            id = "g7",
            name = "Royal Aged Basmati Rice",
            category = "Staples & Grains",
            unitWeight = "5 kg",
            price = 549.0,
            mrp = 650.0,
            rating = 4.8,
            badge = "Best Seller"
        ),
        GroceryItem(
            id = "g8",
            name = "Cold-Pressed Extra Virgin Olive Oil",
            category = "Staples & Grains",
            unitWeight = "500 ml",
            price = 599.0,
            mrp = 749.0,
            rating = 4.9,
            badge = "20% OFF"
        ),
        GroceryItem(
            id = "g9",
            name = "Whole Wheat Chakki Atta",
            category = "Staples & Grains",
            unitWeight = "5 kg",
            price = 265.0,
            mrp = 310.0,
            rating = 4.6
        ),
        GroceryItem(
            id = "g10",
            name = "Cold Brew Arabica Coffee",
            category = "Snacks & Drinks",
            unitWeight = "250 ml x 2",
            price = 190.0,
            mrp = 220.0,
            rating = 4.8,
            badge = "Chilled"
        ),
        GroceryItem(
            id = "g11",
            name = "Roasted California Almonds",
            category = "Snacks & Drinks",
            unitWeight = "200 g",
            price = 245.0,
            mrp = 299.0,
            rating = 4.7
        ),
        GroceryItem(
            id = "g12",
            name = "Fresh Pressed Valencia Orange Juice",
            category = "Snacks & Drinks",
            unitWeight = "1 Litre",
            price = 160.0,
            mrp = 195.0,
            rating = 4.9,
            badge = "No Added Sugar"
        )
    )

    val foodCuisines = listOf(
        "All",
        "North Indian",
        "Pizza & Italian",
        "Bowls & Healthy",
        "South Indian",
        "Desserts"
    )

    val restaurants = listOf(
        Restaurant(
            id = "r1",
            name = "Saffron Spice Kitchen",
            cuisine = "North Indian",
            rating = 4.8,
            prepTime = "22-28 mins",
            priceForTwo = 550,
            offerTag = "20% OFF up to ₹120",
            menuItems = listOf(
                FoodItem(
                    id = "f1",
                    restaurantId = "r1",
                    restaurantName = "Saffron Spice Kitchen",
                    name = "Smoked Paneer Tikka Masala Bowl",
                    description = "Char-grilled cottage cheese in velvety tomato gravy with jeera pulao",
                    cuisine = "North Indian",
                    price = 279.0,
                    isVegetarian = true,
                    rating = 4.8,
                    bestseller = true
                ),
                FoodItem(
                    id = "f2",
                    restaurantId = "r1",
                    restaurantName = "Saffron Spice Kitchen",
                    name = "Royal Butter Chicken & Garlic Naan",
                    description = "Slow-simmered tandoori chicken with two crisp butter garlic naans",
                    cuisine = "North Indian",
                    price = 349.0,
                    isVegetarian = false,
                    rating = 4.9,
                    bestseller = true
                ),
                FoodItem(
                    id = "f3",
                    restaurantId = "r1",
                    restaurantName = "Saffron Spice Kitchen",
                    name = "Dal Makhani & Laccha Paratha",
                    description = "24-hour slow cooked black lentils finished with white butter",
                    cuisine = "North Indian",
                    price = 245.0,
                    isVegetarian = true,
                    rating = 4.7
                )
            )
        ),
        Restaurant(
            id = "r2",
            name = "Forno Napoli Woodfire",
            cuisine = "Pizza & Italian",
            rating = 4.7,
            prepTime = "25-30 mins",
            priceForTwo = 680,
            offerTag = "Free Garlic Bread above ₹499",
            menuItems = listOf(
                FoodItem(
                    id = "f4",
                    restaurantId = "r2",
                    restaurantName = "Forno Napoli Woodfire",
                    name = "Margherita Burrata Sourdough Pizza",
                    description = "San Marzano tomatoes, fresh basil, creamy burrata, 48h fermented crust",
                    cuisine = "Pizza & Italian",
                    price = 389.0,
                    isVegetarian = true,
                    rating = 4.9,
                    bestseller = true
                ),
                FoodItem(
                    id = "f5",
                    restaurantId = "r2",
                    restaurantName = "Forno Napoli Woodfire",
                    name = "Smoky Pepperoni & Hot Honey Pizza",
                    description = "Wood-fired mozzarella, crispy pepperoni cups, chili honey drizzle",
                    cuisine = "Pizza & Italian",
                    price = 449.0,
                    isVegetarian = false,
                    rating = 4.8
                ),
                FoodItem(
                    id = "f6",
                    restaurantId = "r2",
                    restaurantName = "Forno Napoli Woodfire",
                    name = "Truffle Mushroom Penne Alfredo",
                    description = "Wild mushrooms, aged parmesan cream, garlic herb pangrattato",
                    cuisine = "Pizza & Italian",
                    price = 319.0,
                    isVegetarian = true,
                    rating = 4.6
                )
            )
        ),
        Restaurant(
            id = "r3",
            name = "Zest & Grain Bowls",
            cuisine = "Bowls & Healthy",
            rating = 4.9,
            prepTime = "18-24 mins",
            priceForTwo = 480,
            offerTag = "High Protein • Zero Seed Oils",
            menuItems = listOf(
                FoodItem(
                    id = "f7",
                    restaurantId = "r3",
                    restaurantName = "Zest & Grain Bowls",
                    name = "Teriyaki Avocado & Edamame Bowl",
                    description = "Steamed jasmine rice, grilled tofu, crisp cucumbers, sesame dressing",
                    cuisine = "Bowls & Healthy",
                    price = 295.0,
                    isVegetarian = true,
                    rating = 4.9,
                    bestseller = true
                ),
                FoodItem(
                    id = "f8",
                    restaurantId = "r3",
                    restaurantName = "Zest & Grain Bowls",
                    name = "Mediterranean Herb Chicken Quinoa",
                    description = "Peri-peri grilled chicken, hummus, pickled red cabbage, olives",
                    cuisine = "Bowls & Healthy",
                    price = 335.0,
                    isVegetarian = false,
                    rating = 4.8
                )
            )
        ),
        Restaurant(
            id = "r4",
            name = "Madras Filter & Dosa Co.",
            cuisine = "South Indian",
            rating = 4.7,
            prepTime = "15-20 mins",
            priceForTwo = 320,
            offerTag = "Express 20-min Delivery",
            menuItems = listOf(
                FoodItem(
                    id = "f9",
                    restaurantId = "r4",
                    restaurantName = "Madras Filter & Dosa Co.",
                    name = "Ghee Roast Mysore Masala Dosa",
                    description = "Crisp golden crepe with spiced potato mash and three chutneys",
                    cuisine = "South Indian",
                    price = 165.0,
                    isVegetarian = true,
                    rating = 4.8,
                    bestseller = true
                ),
                FoodItem(
                    id = "f10",
                    restaurantId = "r4",
                    restaurantName = "Madras Filter & Dosa Co.",
                    name = "Mini Podi Idli & Filter Coffee Combo",
                    description = "10 button idlis tossed in gun-powder ghee with brass tumbler coffee",
                    cuisine = "South Indian",
                    price = 185.0,
                    isVegetarian = true,
                    rating = 4.7
                )
            )
        ),
        Restaurant(
            id = "r5",
            name = "Velvet Crumb Patisserie",
            cuisine = "Desserts",
            rating = 4.9,
            prepTime = "20-25 mins",
            priceForTwo = 360,
            offerTag = "Flat ₹50 OFF",
            menuItems = listOf(
                FoodItem(
                    id = "f11",
                    restaurantId = "r5",
                    restaurantName = "Velvet Crumb Patisserie",
                    name = "Belgian Dark Chocolate Hazelnut Tart",
                    description = "70% dark chocolate ganache with roasted Piedmont hazelnuts",
                    cuisine = "Desserts",
                    price = 215.0,
                    isVegetarian = true,
                    rating = 4.9,
                    bestseller = true
                ),
                FoodItem(
                    id = "f12",
                    restaurantId = "r5",
                    restaurantName = "Velvet Crumb Patisserie",
                    name = "Saffron Pistachio Tres Leches",
                    description = "Sponge soaked in saffron cardamom three-milk blend",
                    cuisine = "Desserts",
                    price = 235.0,
                    isVegetarian = true,
                    rating = 4.8
                )
            )
        )
    )
}
