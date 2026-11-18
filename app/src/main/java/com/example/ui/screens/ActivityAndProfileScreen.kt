package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.OrderStatus
import com.example.data.OrderType
import com.example.data.RideBooking
import com.example.data.RideStatus
import com.example.data.SavedAddress
import com.example.data.ServiceOrder
import com.example.data.UiState
import com.example.data.VehicleType
import com.example.ui.components.ZivoraStatusPill
import com.example.ui.components.formatRupees
import com.example.ui.theme.ZivoraError
import com.example.ui.theme.ZivoraErrorContainer
import com.example.ui.theme.ZivoraOrange
import com.example.ui.theme.ZivoraOrangeDark
import com.example.ui.theme.ZivoraPeachContainer
import com.example.ui.theme.ZivoraPeachSoft
import com.example.ui.theme.ZivoraPureWhite
import com.example.ui.theme.ZivoraSuccess
import com.example.ui.theme.ZivoraSuccessContainer

@Composable
fun ActivityAndProfileScreen(
    displayName: String,
    email: String,
    activeAddress: String,
    ridesState: UiState<List<RideBooking>>,
    ordersState: UiState<List<ServiceOrder>>,
    addressesState: UiState<List<SavedAddress>>,
    onAdvanceRideStatus: (RideBooking, RideStatus) -> Unit,
    onAdvanceOrderStatus: (ServiceOrder, OrderStatus) -> Unit,
    onOpenAddressDialog: () -> Unit,
    onSelectActiveAddress: (String) -> Unit,
    onDeleteAddress: (String) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Rides", "Grocery & Food", "Addresses")

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // User Profile Card
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = ZivoraOrange,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "User profile",
                                            tint = ZivoraPureWhite
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = displayName.ifBlank { "ZIVORA Member" },
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = email.ifBlank { "Signed in with Google" },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            OutlinedButton(
                                onClick = onSignOut,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("sign_out_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = "Sign out",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sign Out")
                            }
                        }

                        HorizontalDivider()

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = ZivoraOrange,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = activeAddress,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            TextButtonChangeAddress(onClick = onOpenAddressDialog)
                        }
                    }
                }
            }

            // Filter Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filters) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ZivoraOrange,
                                selectedLabelColor = ZivoraPureWhite
                            )
                        )
                    }
                }
            }

            if (selectedFilter == "Addresses") {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Saved Addresses", style = MaterialTheme.typography.titleLarge)
                        Button(
                            onClick = onOpenAddressDialog,
                            colors = ButtonDefaults.buttonColors(containerColor = ZivoraOrange),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New")
                        }
                    }
                }
                when (addressesState) {
                    is UiState.Loading -> item { LoadingCard() }
                    is UiState.Error -> item { ErrorCard(addressesState.message) }
                    is UiState.Success -> {
                        if (addressesState.data.isEmpty()) {
                            item {
                                EmptyStateCard(
                                    title = "No saved addresses yet",
                                    subtitle = "Tap New above to save Home, Work, or frequent locations."
                                )
                            }
                        } else {
                            items(addressesState.data, key = { it.id }) { addr ->
                                SavedAddressCard(
                                    address = addr,
                                    isActive = addr.fullAddress == activeAddress,
                                    onUse = { onSelectActiveAddress(addr.fullAddress) },
                                    onDelete = { onDeleteAddress(addr.id) }
                                )
                            }
                        }
                    }
                }
            } else {
                val showRides = selectedFilter == "All" || selectedFilter == "Rides"
                val showOrders = selectedFilter == "All" || selectedFilter == "Grocery & Food"

                if (showRides) {
                    item {
                        Text("Ride & Cargo Bookings", style = MaterialTheme.typography.titleLarge)
                    }
                    when (ridesState) {
                        is UiState.Loading -> item { LoadingCard() }
                        is UiState.Error -> item { ErrorCard(ridesState.message) }
                        is UiState.Success -> {
                            if (ridesState.data.isEmpty()) {
                                item {
                                    EmptyStateCard(
                                        title = "No rides booked yet",
                                        subtitle = "Book a Car, Taxi, Auto, E-Rickshaw, Pickup, or Loader from the Rides tab."
                                    )
                                }
                            } else {
                                items(ridesState.data, key = { it.id }) { ride ->
                                    RideHistoryCard(
                                        booking = ride,
                                        onAdvanceRideStatus = onAdvanceRideStatus
                                    )
                                }
                            }
                        }
                    }
                }

                if (showOrders) {
                    item {
                        Text("Grocery & Food Orders", style = MaterialTheme.typography.titleLarge)
                    }
                    when (ordersState) {
                        is UiState.Loading -> item { LoadingCard() }
                        is UiState.Error -> item { ErrorCard(ordersState.message) }
                        is UiState.Success -> {
                            if (ordersState.data.isEmpty()) {
                                item {
                                    EmptyStateCard(
                                        title = "No grocery or food orders yet",
                                        subtitle = "Order fresh groceries or hot meals and track live progress here."
                                    )
                                }
                            } else {
                                items(ordersState.data, key = { it.id }) { order ->
                                    OrderHistoryCard(
                                        order = order,
                                        onAdvanceOrderStatus = onAdvanceOrderStatus
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TextButtonChangeAddress(onClick: () -> Unit) {
    androidx.compose.material3.TextButton(onClick = onClick) {
        Text("Change", color = ZivoraOrange, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun RideHistoryCard(
    booking: RideBooking,
    onAdvanceRideStatus: (RideBooking, RideStatus) -> Unit
) {
    val vehicle = VehicleType.fromRaw(booking.vehicleType)
    val status = RideStatus.fromRaw(booking.status)
    val (bg, fg) = statusColors(status.name)

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(vehicleIcon(vehicle), contentDescription = null, tint = ZivoraOrange)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(vehicle.displayName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
                ZivoraStatusPill(text = status.label, containerColor = bg, contentColor = fg)
            }
            Text(
                text = "${booking.pickupLocation} → ${booking.dropLocation}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Driver: ${booking.driverName} (${booking.vehicleNumber}) • OTP ${booking.otpCode}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${booking.distanceKm} km • ${formatRupees(booking.fareAmount)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = ZivoraOrangeDark,
                    fontWeight = FontWeight.Bold
                )
                if (status == RideStatus.CONFIRMED || status == RideStatus.IN_PROGRESS) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val next = if (status == RideStatus.CONFIRMED) RideStatus.IN_PROGRESS else RideStatus.COMPLETED
                        Button(
                            onClick = { onAdvanceRideStatus(booking, next) },
                            colors = ButtonDefaults.buttonColors(containerColor = ZivoraOrange),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(if (next == RideStatus.IN_PROGRESS) "Start" else "Complete", style = MaterialTheme.typography.labelMedium)
                        }
                        OutlinedButton(
                            onClick = { onAdvanceRideStatus(booking, RideStatus.CANCELLED) },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Cancel", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderHistoryCard(
    order: ServiceOrder,
    onAdvanceOrderStatus: (ServiceOrder, OrderStatus) -> Unit
) {
    val type = OrderType.fromRaw(order.orderType)
    val status = OrderStatus.fromRaw(order.status)
    val (bg, fg) = statusColors(status.name)

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (type == OrderType.GROCERY) Icons.Default.LocalGroceryStore else Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = ZivoraOrange
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(order.storeName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
                ZivoraStatusPill(text = status.label, containerColor = bg, contentColor = fg)
            }
            Text(
                text = order.itemsSummary,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "To: ${order.deliveryAddress} • ${order.deliverySlot}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${order.itemCount} items • ${formatRupees(order.totalAmount)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = ZivoraOrangeDark,
                    fontWeight = FontWeight.Bold
                )
                when (status) {
                    OrderStatus.PLACED, OrderStatus.PREPARING, OrderStatus.OUT_FOR_DELIVERY -> {
                        val next = when (status) {
                            OrderStatus.PLACED -> OrderStatus.PREPARING
                            OrderStatus.PREPARING -> OrderStatus.OUT_FOR_DELIVERY
                            OrderStatus.OUT_FOR_DELIVERY -> OrderStatus.DELIVERED
                            else -> OrderStatus.DELIVERED
                        }
                        val nextLabel = when (next) {
                            OrderStatus.PREPARING -> "Mark Preparing"
                            OrderStatus.OUT_FOR_DELIVERY -> "Dispatch"
                            OrderStatus.DELIVERED -> "Mark Delivered"
                            OrderStatus.PLACED, OrderStatus.CANCELLED -> "Update"
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onAdvanceOrderStatus(order, next) },
                                colors = ButtonDefaults.buttonColors(containerColor = ZivoraOrange),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(nextLabel, style = MaterialTheme.typography.labelMedium)
                            }
                            if (status != OrderStatus.OUT_FOR_DELIVERY) {
                                OutlinedButton(
                                    onClick = { onAdvanceOrderStatus(order, OrderStatus.CANCELLED) },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text("Cancel", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}

@Composable
private fun SavedAddressCard(
    address: SavedAddress,
    isActive: Boolean,
    onUse: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isActive) ZivoraPeachSoft else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (isActive) ZivoraOrange else MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = ZivoraOrange)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(address.label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(address.fullAddress, style = MaterialTheme.typography.bodySmall)
                if (address.landmark.isNotBlank()) {
                    Text(
                        "Landmark: ${address.landmark}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (!isActive) {
                OutlinedButton(onClick = onUse, contentPadding = PaddingValues(horizontal = 10.dp)) {
                    Text("Use")
                }
            } else {
                ZivoraStatusPill("Active", ZivoraPeachContainer, ZivoraOrangeDark)
            }
            IconButton(onClick = onDelete, modifier = Modifier.minimumInteractiveComponentSize()) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete address", tint = ZivoraError)
            }
        }
    }
}

@Composable
private fun LoadingCard() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ZivoraOrange)
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = ZivoraErrorContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = message,
            color = ZivoraError,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun EmptyStateCard(title: String, subtitle: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = ZivoraPeachSoft,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, tint = ZivoraOrange, modifier = Modifier.size(30.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun statusColors(statusName: String): Pair<Color, Color> = when (statusName) {
    "COMPLETED", "DELIVERED" -> ZivoraSuccessContainer to ZivoraSuccess
    "CANCELLED" -> ZivoraErrorContainer to ZivoraError
    else -> ZivoraPeachContainer to ZivoraOrangeDark
}
