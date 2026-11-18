package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AirportShuttle
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricRickshaw
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.OrderStatus
import com.example.data.RideBooking
import com.example.data.RideStatus
import com.example.data.ServiceOrder
import com.example.data.VehicleType
import com.example.ui.components.ZivoraLogoBadge
import com.example.ui.components.ZivoraStatusPill
import com.example.ui.components.formatRupees
import com.example.ui.theme.ZivoraDisabledBg
import com.example.ui.theme.ZivoraDisabledText
import com.example.ui.theme.ZivoraOrange
import com.example.ui.theme.ZivoraOrangeDark
import com.example.ui.theme.ZivoraPeachContainer
import com.example.ui.theme.ZivoraPeachSoft
import com.example.ui.theme.ZivoraPureWhite
import com.example.ui.theme.ZivoraSuccess
import com.example.ui.theme.ZivoraSuccessContainer

@Composable
fun HomeScreen(
    displayName: String,
    activeAddress: String,
    activeRides: List<RideBooking>,
    activeOrders: List<ServiceOrder>,
    onChangeAddressClick: () -> Unit,
    onSelectVehicleAndBook: (VehicleType) -> Unit,
    onOpenGrocery: () -> Unit,
    onOpenFood: () -> Unit,
    onOpenActivity: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Top ZIVORA Brand & Address Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        ZivoraLogoBadge(size = 42.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = stringResource(R.string.app_name),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = ZivoraOrange,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                ZivoraStatusPill(
                                    text = "LIVE",
                                    containerColor = ZivoraPeachContainer,
                                    contentColor = ZivoraOrangeDark
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable(onClick = onChangeAddressClick)
                                    .padding(vertical = 2.dp)
                                    .testTag("home_address_picker")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Active address",
                                    tint = ZivoraOrange,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = activeAddress,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                Icon(
                                    imageVector = Icons.Default.ExpandMore,
                                    contentDescription = "Change address",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onOpenActivity,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(CircleShape)
                            .background(ZivoraPeachContainer)
                            .testTag("home_profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Account and Activity",
                            tint = ZivoraOrangeDark
                        )
                    }
                }
            }

            // Hero Promo Banner
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_zivora_banner),
                            contentDescription = "ZIVORA super app banner",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xE6D95300),
                                            Color(0x99FF6B00),
                                            Color(0x11000000)
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Hi, ${displayName.substringBefore(" ").ifBlank { "Explorer" }}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = ZivoraPureWhite.copy(alpha = 0.9f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.app_tagline),
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = ZivoraPureWhite,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Rides, Cargo, Groceries & Hot Meals",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ZivoraPureWhite.copy(alpha = 0.88f)
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = { onSelectVehicleAndBook(VehicleType.CAR) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ZivoraPureWhite,
                                        contentColor = ZivoraOrangeDark
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                    modifier = Modifier.testTag("hero_book_ride_btn")
                                ) {
                                    Text("Book Ride", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = onOpenGrocery,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ZivoraPureWhite.copy(alpha = 0.22f),
                                        contentColor = ZivoraPureWhite
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                    modifier = Modifier.testTag("hero_shop_grocery_btn")
                                ) {
                                    Text("Shop Fresh", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }

            // Active Live Ride or Order Summary Card
            val latestActiveRide = activeRides.firstOrNull {
                it.status == RideStatus.CONFIRMED.name || it.status == RideStatus.IN_PROGRESS.name
            }
            val latestActiveOrder = activeOrders.firstOrNull {
                it.status != OrderStatus.DELIVERED.name && it.status != OrderStatus.CANCELLED.name
            }
            if (latestActiveRide != null || latestActiveOrder != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = ZivoraPeachSoft,
                        border = BorderStroke(1.dp, ZivoraOrange.copy(alpha = 0.45f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onOpenActivity)
                            .testTag("home_live_activity_card")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = ZivoraOrange,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (latestActiveRide != null) Icons.Default.DirectionsCar else Icons.Default.LocalGroceryStore,
                                        contentDescription = "Active trip or order",
                                        tint = ZivoraPureWhite,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                if (latestActiveRide != null) {
                                    val vehicle = VehicleType.fromRaw(latestActiveRide.vehicleType)
                                    Text(
                                        text = "${vehicle.displayName} • OTP ${latestActiveRide.otpCode}",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${latestActiveRide.pickupLocation} → ${latestActiveRide.dropLocation}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                } else if (latestActiveOrder != null) {
                                    Text(
                                        text = "${latestActiveOrder.storeName} • ${OrderStatus.fromRaw(latestActiveOrder.status).label}",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${latestActiveOrder.itemCount} items • ${formatRupees(latestActiveOrder.totalAmount)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "View details",
                                tint = ZivoraOrange
                            )
                        }
                    }
                }
            }

            // Section: Ride Booking (Car, Taxi, Auto, E-Rickshaw active; Bike completely disabled)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ride Booking",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Instant pickup",
                            style = MaterialTheme.typography.labelMedium,
                            color = ZivoraOrange
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ServiceTileCard(
                            title = "Car",
                            subtitle = "2-3 min • AC Sedan",
                            icon = Icons.Default.DirectionsCar,
                            badge = "Popular",
                            onClick = { onSelectVehicleAndBook(VehicleType.CAR) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("home_service_car")
                        )
                        ServiceTileCard(
                            title = "Taxi",
                            subtitle = "Priority Cab",
                            icon = Icons.Default.LocalTaxi,
                            onClick = { onSelectVehicleAndBook(VehicleType.TAXI) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("home_service_taxi")
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ServiceTileCard(
                            title = "Auto",
                            subtitle = "Quick 3-Wheeler",
                            icon = Icons.Default.AirportShuttle,
                            onClick = { onSelectVehicleAndBook(VehicleType.AUTO) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("home_service_auto")
                        )
                        ServiceTileCard(
                            title = "E-Rickshaw",
                            subtitle = "Eco Short-Hop",
                            icon = Icons.Default.ElectricRickshaw,
                            badge = "Eco",
                            onClick = { onSelectVehicleAndBook(VehicleType.E_RICKSHAW) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("home_service_erickshaw")
                        )
                    }

                    // Disabled Bike Booking Card (Explicitly Non-Interactive)
                    DisabledBikeCard(modifier = Modifier.testTag("home_service_bike_disabled"))
                }
            }

            // Section: Cargo & Goods Transport (Pickup & Loader)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Cargo & Goods Transport",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ServiceTileCard(
                            title = "Pickup",
                            subtitle = "Mini Van • 750 kg",
                            icon = Icons.Default.LocalShipping,
                            onClick = { onSelectVehicleAndBook(VehicleType.PICKUP) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("home_service_pickup")
                        )
                        ServiceTileCard(
                            title = "Loader",
                            subtitle = "Cargo Truck • 1.5T",
                            icon = Icons.Default.LocalShipping,
                            badge = "Heavy",
                            onClick = { onSelectVehicleAndBook(VehicleType.LOADER) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("home_service_loader")
                        )
                    }
                }
            }

            // Section: Grocery Delivery & Food Ordering
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Grocery & Food Ordering",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DeliveryHighlightCard(
                            title = "Grocery Delivery",
                            subtitle = "Fresh produce & essentials in 15 min",
                            badge = "ZIVORA Mart",
                            imageRes = R.drawable.img_grocery_promo,
                            icon = Icons.Default.LocalGroceryStore,
                            onClick = onOpenGrocery,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("home_service_grocery")
                        )
                        DeliveryHighlightCard(
                            title = "Food Ordering",
                            subtitle = "Hot dishes from top local kitchens",
                            badge = "20% OFF",
                            imageRes = R.drawable.img_food_promo,
                            icon = Icons.Default.Restaurant,
                            onClick = onOpenFood,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("home_service_food")
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun ServiceTileCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badge: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shadowElevation = 1.dp,
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ZivoraPeachContainer,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = ZivoraOrange,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                if (badge != null) {
                    ZivoraStatusPill(
                        text = badge,
                        containerColor = if (badge == "Eco") ZivoraSuccessContainer else ZivoraPeachContainer,
                        contentColor = if (badge == "Eco") ZivoraSuccess else ZivoraOrangeDark
                    )
                }
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun DisabledBikeCard(modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = ZivoraDisabledBg,
        border = BorderStroke(1.dp, ZivoraDisabledText.copy(alpha = 0.35f)),
        modifier = modifier
            .fillMaxWidth()
            .alpha(0.78f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = ZivoraDisabledText.copy(alpha = 0.2f),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.TwoWheeler,
                        contentDescription = "Bike booking disabled",
                        tint = ZivoraDisabledText,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Bike Booking",
                    style = MaterialTheme.typography.titleSmall,
                    color = ZivoraDisabledText
                )
                Text(
                    text = "Currently disabled • Use Auto, E-Rickshaw, Car, or Taxi",
                    style = MaterialTheme.typography.bodySmall,
                    color = ZivoraDisabledText
                )
            }
            Surface(
                shape = RoundedCornerShape(50),
                color = ZivoraDisabledText.copy(alpha = 0.18f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Block,
                        contentDescription = "Disabled",
                        tint = ZivoraDisabledText,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Disabled",
                        style = MaterialTheme.typography.labelSmall,
                        color = ZivoraDisabledText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun DeliveryHighlightCard(
    title: String,
    subtitle: String,
    badge: String,
    imageRes: Int,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shadowElevation = 2.dp,
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clickable(onClick = onClick)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(104.dp)
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                ZivoraStatusPill(
                    text = badge,
                    containerColor = ZivoraOrange,
                    contentColor = ZivoraPureWhite,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                )
            }
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = ZivoraOrange,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
