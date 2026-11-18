package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.FoodItem
import com.example.data.PaymentMethod
import com.example.data.Restaurant
import com.example.data.ZivoraCatalog
import com.example.ui.FoodCartState
import com.example.ui.components.VegIndicator
import com.example.ui.components.ZivoraQuantityStepper
import com.example.ui.components.ZivoraStatusPill
import com.example.ui.components.formatRupees
import com.example.ui.theme.ZivoraOrange
import com.example.ui.theme.ZivoraOrangeDark
import com.example.ui.theme.ZivoraPeachContainer
import com.example.ui.theme.ZivoraPeachSoft
import com.example.ui.theme.ZivoraPureWhite
import com.example.ui.theme.ZivoraSuccess
import com.example.ui.theme.ZivoraSuccessContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodScreen(
    cartState: FoodCartState,
    activeAddress: String,
    onCuisineSelect: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onAdjustFoodQuantity: (FoodItem, Int) -> Unit,
    onPaymentSelect: (PaymentMethod) -> Unit,
    onPlaceFoodOrder: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCheckoutSheet by remember { mutableStateOf(false) }

    val filteredRestaurants = remember(cartState.selectedCuisine, cartState.searchQuery) {
        ZivoraCatalog.restaurants.mapNotNull { restaurant ->
            val cuisineMatches = cartState.selectedCuisine == "All" || restaurant.cuisine == cartState.selectedCuisine
            if (!cuisineMatches) return@mapNotNull null

            if (cartState.searchQuery.isBlank()) {
                restaurant
            } else {
                val restaurantMatch = restaurant.name.contains(cartState.searchQuery, ignoreCase = true) ||
                    restaurant.cuisine.contains(cartState.searchQuery, ignoreCase = true)
                val matchingMenu = restaurant.menuItems.filter {
                    it.name.contains(cartState.searchQuery, ignoreCase = true) ||
                        it.description.contains(cartState.searchQuery, ignoreCase = true)
                }
                if (restaurantMatch) restaurant
                else if (matchingMenu.isNotEmpty()) restaurant.copy(menuItems = matchingMenu)
                else null
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 14.dp,
                bottom = if (cartState.totalItemCount > 0) 92.dp else 18.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Food Hero Banner
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    shadowElevation = 3.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(152.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_food_promo),
                            contentDescription = "ZIVORA Food Ordering",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(Color(0xD91A1614), Color(0x22000000))
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            ZivoraStatusPill(
                                text = "HOT & FRESH",
                                containerColor = ZivoraOrange,
                                contentColor = ZivoraPureWhite
                            )
                            Column {
                                Text(
                                    text = "ZIVORA Food Ordering",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = ZivoraPureWhite,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Top kitchens near $activeAddress",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ZivoraPureWhite.copy(alpha = 0.88f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = cartState.searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Search pizza, dosa, biryani, bowls...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search food", tint = ZivoraOrange)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("food_search_input")
                )
            }

            // Cuisine Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ZivoraCatalog.foodCuisines) { cuisine ->
                        FilterChip(
                            selected = cartState.selectedCuisine == cuisine,
                            onClick = { onCuisineSelect(cuisine) },
                            label = { Text(cuisine) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ZivoraOrange,
                                selectedLabelColor = ZivoraPureWhite
                            )
                        )
                    }
                }
            }

            if (filteredRestaurants.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = ZivoraPeachSoft,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Restaurant,
                                contentDescription = null,
                                tint = ZivoraOrange,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No dishes match your filter.", style = MaterialTheme.typography.titleSmall)
                        }
                    }
                }
            } else {
                items(filteredRestaurants, key = { it.id }) { restaurant ->
                    RestaurantMenuCard(
                        restaurant = restaurant,
                        quantities = cartState.quantities,
                        onAdjustFoodQuantity = onAdjustFoodQuantity
                    )
                }
            }
        }

        // Persistent Floating Cart Bar
        if (cartState.totalItemCount > 0) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = ZivoraOrange,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .widthIn(max = 688.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = "Food Cart",
                            tint = ZivoraPureWhite
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "${cartState.totalItemCount} item(s) • ${formatRupees(cartState.grandTotal)}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = ZivoraPureWhite
                            )
                            Text(
                                text = cartState.activeRestaurantName ?: "ZIVORA Kitchen",
                                style = MaterialTheme.typography.bodySmall,
                                color = ZivoraPureWhite.copy(alpha = 0.9f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Button(
                        onClick = { showCheckoutSheet = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ZivoraPureWhite,
                            contentColor = ZivoraOrangeDark
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("food_checkout_button")
                    ) {
                        Text("View Cart", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showCheckoutSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCheckoutSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Order from ${cartState.activeRestaurantName ?: "Kitchen"}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Delivering to: $activeAddress • 25-30 mins",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text("Payment Method", style = MaterialTheme.typography.labelLarge)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(PaymentMethod.entries) { method ->
                        FilterChip(
                            selected = cartState.paymentMethod == method,
                            onClick = { onPaymentSelect(method) },
                            label = { Text(method.label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ZivoraOrange,
                                selectedLabelColor = ZivoraPureWhite
                            )
                        )
                    }
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Food Subtotal (${cartState.totalItemCount})", style = MaterialTheme.typography.bodyMedium)
                    Text(formatRupees(cartState.itemSubtotal), style = MaterialTheme.typography.bodyMedium)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Delivery & Packaging", style = MaterialTheme.typography.bodyMedium)
                    Text(formatRupees(cartState.deliveryFee), style = MaterialTheme.typography.bodyMedium)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Payable", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        formatRupees(cartState.grandTotal),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = ZivoraOrangeDark
                    )
                }

                Button(
                    onClick = {
                        onPlaceFoodOrder()
                        showCheckoutSheet = false
                    },
                    enabled = !cartState.isSubmitting,
                    colors = ButtonDefaults.buttonColors(containerColor = ZivoraOrange),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("place_food_order_button")
                ) {
                    if (cartState.isSubmitting) {
                        CircularProgressIndicator(color = ZivoraPureWhite, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Place Food Order • ${formatRupees(cartState.grandTotal)}", fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun RestaurantMenuCard(
    restaurant: Restaurant,
    quantities: Map<String, Int>,
    onAdjustFoodQuantity: (FoodItem, Int) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = restaurant.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${restaurant.cuisine} • ₹${restaurant.priceForTwo} for two",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                ZivoraStatusPill(
                    text = "${restaurant.rating} ★",
                    containerColor = ZivoraSuccessContainer,
                    contentColor = ZivoraSuccess
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = ZivoraOrange,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = restaurant.prepTime,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                ZivoraStatusPill(
                    text = restaurant.offerTag,
                    containerColor = ZivoraPeachContainer,
                    contentColor = ZivoraOrangeDark
                )
            }

            HorizontalDivider()

            restaurant.menuItems.forEachIndexed { index, dish ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            VegIndicator(isVeg = dish.isVegetarian)
                            if (dish.bestseller) {
                                Spacer(modifier = Modifier.width(8.dp))
                                ZivoraStatusPill(
                                    text = "Bestseller",
                                    containerColor = ZivoraPeachContainer,
                                    contentColor = ZivoraOrangeDark
                                )
                            }
                        }
                        Text(
                            text = dish.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = dish.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = formatRupees(dish.price),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = ZivoraOrangeDark
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    ZivoraQuantityStepper(
                        quantity = quantities[dish.id] ?: 0,
                        onIncrement = { onAdjustFoodQuantity(dish, 1) },
                        onDecrement = { onAdjustFoodQuantity(dish, -1) },
                        itemTagPrefix = "food_${dish.id}"
                    )
                }
                if (index < restaurant.menuItems.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                }
            }
        }
    }
}
