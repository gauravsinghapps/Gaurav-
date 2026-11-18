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
import androidx.compose.material.icons.filled.LocalGroceryStore
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.GroceryItem
import com.example.data.PaymentMethod
import com.example.data.ZivoraCatalog
import com.example.ui.GroceryCartState
import com.example.ui.components.ZivoraQuantityStepper
import com.example.ui.components.ZivoraStatusPill
import com.example.ui.components.formatRupees
import com.example.ui.theme.ZivoraOrange
import com.example.ui.theme.ZivoraOrangeDark
import com.example.ui.theme.ZivoraPeachContainer
import com.example.ui.theme.ZivoraPeachSoft
import com.example.ui.theme.ZivoraPureWhite
import com.example.ui.theme.ZivoraSuccess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryScreen(
    cartState: GroceryCartState,
    activeAddress: String,
    onCategorySelect: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onAdjustQuantity: (String, Int) -> Unit,
    onSlotSelect: (String) -> Unit,
    onPaymentSelect: (PaymentMethod) -> Unit,
    onPlaceOrder: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCheckoutSheet by remember { mutableStateOf(false) }

    val filteredItems = remember(cartState.selectedCategory, cartState.searchQuery) {
        ZivoraCatalog.groceryItems.filter { item ->
            val catMatch = cartState.selectedCategory == "All" || item.category == cartState.selectedCategory
            val queryMatch = cartState.searchQuery.isBlank() ||
                item.name.contains(cartState.searchQuery, ignoreCase = true) ||
                item.category.contains(cartState.searchQuery, ignoreCase = true)
            catMatch && queryMatch
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
            // Promo Banner
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
                            painter = painterResource(id = R.drawable.img_grocery_promo),
                            contentDescription = "ZIVORA Grocery Delivery",
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
                                text = "EXPRESS 15 MIN",
                                containerColor = ZivoraOrange,
                                contentColor = ZivoraPureWhite
                            )
                            Column {
                                Text(
                                    text = "ZIVORA Fresh Mart",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = ZivoraPureWhite,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Free delivery above ₹399 • $activeAddress",
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
                    placeholder = { Text("Search avocados, milk, bread, staples...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search groceries", tint = ZivoraOrange)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("grocery_search_input")
                )
            }

            // Category Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ZivoraCatalog.groceryCategories) { category ->
                        val selected = cartState.selectedCategory == category
                        FilterChip(
                            selected = selected,
                            onClick = { onCategorySelect(category) },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ZivoraOrange,
                                selectedLabelColor = ZivoraPureWhite
                            )
                        )
                    }
                }
            }

            if (filteredItems.isEmpty()) {
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
                                Icons.Default.LocalGroceryStore,
                                contentDescription = null,
                                tint = ZivoraOrange,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No grocery items match your search.", style = MaterialTheme.typography.titleSmall)
                        }
                    }
                }
            } else {
                items(filteredItems, key = { it.id }) { item ->
                    GroceryItemRow(
                        item = item,
                        quantity = cartState.quantities[item.id] ?: 0,
                        onIncrement = { onAdjustQuantity(item.id, 1) },
                        onDecrement = { onAdjustQuantity(item.id, -1) }
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = "Grocery Cart",
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
                                text = if (cartState.deliveryFee == 0.0) "Free delivery unlocked" else "Includes ₹25 delivery fee",
                                style = MaterialTheme.typography.bodySmall,
                                color = ZivoraPureWhite.copy(alpha = 0.88f)
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
                        modifier = Modifier.testTag("grocery_checkout_button")
                    ) {
                        Text("Checkout", fontWeight = FontWeight.Bold)
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
                    text = "Grocery Checkout",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Delivering to: $activeAddress",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text("Delivery Slot", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Express 15 mins", "Standard 45 mins").forEach { slot ->
                        FilterChip(
                            selected = cartState.deliverySlot == slot,
                            onClick = { onSlotSelect(slot) },
                            label = { Text(slot) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ZivoraPeachContainer,
                                selectedLabelColor = ZivoraOrangeDark
                            )
                        )
                    }
                }

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
                    Text("Items Subtotal (${cartState.totalItemCount})", style = MaterialTheme.typography.bodyMedium)
                    Text(formatRupees(cartState.itemSubtotal), style = MaterialTheme.typography.bodyMedium)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Delivery Fee", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = if (cartState.deliveryFee == 0.0) "FREE" else formatRupees(cartState.deliveryFee),
                        color = if (cartState.deliveryFee == 0.0) ZivoraSuccess else MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
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
                        onPlaceOrder()
                        showCheckoutSheet = false
                    },
                    enabled = !cartState.isSubmitting,
                    colors = ButtonDefaults.buttonColors(containerColor = ZivoraOrange),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("place_grocery_order_button")
                ) {
                    if (cartState.isSubmitting) {
                        CircularProgressIndicator(color = ZivoraPureWhite, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Place Grocery Order • ${formatRupees(cartState.grandTotal)}", fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun GroceryItemRow(
    item: GroceryItem,
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = ZivoraPeachSoft,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.LocalGroceryStore,
                        contentDescription = item.name,
                        tint = ZivoraOrange,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                if (item.badge.isNotBlank()) {
                    ZivoraStatusPill(
                        text = item.badge,
                        containerColor = ZivoraPeachContainer,
                        contentColor = ZivoraOrangeDark
                    )
                }
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.unitWeight,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = ZivoraOrange,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = " ${item.rating}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatRupees(item.price),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = ZivoraOrangeDark
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = formatRupees(item.mrp),
                        style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.LineThrough),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            ZivoraQuantityStepper(
                quantity = quantity,
                onIncrement = onIncrement,
                onDecrement = onDecrement,
                itemTagPrefix = "grocery_${item.id}"
            )
        }
    }
}
