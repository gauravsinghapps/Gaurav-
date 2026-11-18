package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirportShuttle
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricRickshaw
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.TwoWheeler
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.PaymentMethod
import com.example.data.RideBooking
import com.example.data.RideStatus
import com.example.data.VehicleType
import com.example.data.ZivoraCatalog
import com.example.ui.RideDraftState
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
fun RideBookingScreen(
    draft: RideDraftState,
    activeRides: List<RideBooking>,
    onSelectVehicle: (VehicleType) -> Unit,
    onPickupChange: (String) -> Unit,
    onDropChange: (String) -> Unit,
    onSwapLocations: () -> Unit,
    onPaymentChange: (PaymentMethod) -> Unit,
    onTogglePromo: () -> Unit,
    onConfirmBooking: () -> Unit,
    onAdvanceRideStatus: (RideBooking, RideStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    val ongoingRide = activeRides.firstOrNull {
        it.status == RideStatus.CONFIRMED.name || it.status == RideStatus.IN_PROGRESS.name
    }
    val listState = rememberLazyListState()

    LaunchedEffect(ongoingRide?.id) {
        if (ongoingRide != null) {
            listState.animateScrollToItem(0)
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "ZIVORA Rides & Cargo",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Car, Taxi, Auto, E-Rickshaw, Pickup & Loader • Bike disabled",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (ongoingRide != null) {
                item {
                    OngoingRideCard(
                        booking = ongoingRide,
                        onAdvanceRideStatus = onAdvanceRideStatus
                    )
                }
            }

            // Route Inputs Card
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = draft.pickupLocation,
                                    onValueChange = { if (it.length <= 150) onPickupChange(it) },
                                    label = { Text("Pickup Location") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.MyLocation,
                                            contentDescription = null,
                                            tint = ZivoraSuccess
                                        )
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("ride_pickup_input")
                                )
                                OutlinedTextField(
                                    value = draft.dropLocation,
                                    onValueChange = { if (it.length <= 150) onDropChange(it) },
                                    label = { Text("Drop-off Location") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Place,
                                            contentDescription = null,
                                            tint = ZivoraOrange
                                        )
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("ride_drop_input")
                                )
                            }
                            IconButton(
                                onClick = onSwapLocations,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .background(ZivoraPeachContainer, CircleShape)
                                    .testTag("swap_locations_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapVert,
                                    contentDescription = "Swap pickup and drop-off",
                                    tint = ZivoraOrangeDark
                                )
                            }
                        }

                        Text(
                            text = "Quick Drop Hubs (${draft.distanceKm} km est.)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(ZivoraCatalog.popularLocations) { hub ->
                                FilterChip(
                                    selected = draft.dropLocation == hub,
                                    onClick = { onDropChange(hub) },
                                    label = { Text(hub, maxLines = 1) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ZivoraPeachContainer,
                                        selectedLabelColor = ZivoraOrangeDark
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Vehicle Selection Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Choose Ride or Loader",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "${draft.distanceKm} km",
                        style = MaterialTheme.typography.labelLarge,
                        color = ZivoraOrange
                    )
                }
            }

            // Active Vehicle Options (Car, Taxi, Auto, E-Rickshaw, Pickup, Loader)
            items(VehicleType.activeVehicles, key = { it.name }) { vehicle ->
                val isSelected = draft.selectedVehicle == vehicle
                val estimatedFare = kotlin.math.max(
                    15.0,
                    vehicle.baseFare + draft.distanceKm * vehicle.perKmRate
                )
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = if (isSelected) ZivoraPeachSoft else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) ZivoraOrange else MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .clickable { onSelectVehicle(vehicle) }
                        .testTag("vehicle_option_${vehicle.name.lowercase()}")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) ZivoraOrange else ZivoraPeachContainer,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = vehicleIcon(vehicle),
                                    contentDescription = vehicle.displayName,
                                    tint = if (isSelected) ZivoraPureWhite else ZivoraOrangeDark,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = vehicle.displayName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                ZivoraStatusPill(
                                    text = vehicle.etaText,
                                    containerColor = ZivoraPeachContainer,
                                    contentColor = ZivoraOrangeDark
                                )
                            }
                            Text(
                                text = "${vehicle.subtitle} • ${vehicle.capacityNote}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = formatRupees(estimatedFare),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) ZivoraOrangeDark else MaterialTheme.colorScheme.onSurface
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = ZivoraOrange,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Explicitly Disabled Bike Row (Cannot be selected or booked)
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = ZivoraDisabledBg,
                    border = BorderStroke(1.dp, ZivoraDisabledText.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(0.75f)
                        .testTag("vehicle_option_bike_disabled")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = ZivoraDisabledText.copy(alpha = 0.2f),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.TwoWheeler,
                                    contentDescription = "Bike disabled",
                                    tint = ZivoraDisabledText,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Bike",
                                style = MaterialTheme.typography.titleMedium,
                                color = ZivoraDisabledText
                            )
                            Text(
                                text = "Bike booking is completely disabled on ZIVORA",
                                style = MaterialTheme.typography.bodySmall,
                                color = ZivoraDisabledText
                            )
                        }
                        ZivoraStatusPill(
                            text = "Disabled",
                            containerColor = ZivoraDisabledText.copy(alpha = 0.2f),
                            contentColor = ZivoraDisabledText
                        )
                    }
                }
            }

            // Promo & Payment Card
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onTogglePromo)
                                .testTag("promo_toggle_row"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocalOffer,
                                    contentDescription = "Promo code",
                                    tint = ZivoraOrange
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "ZIVORA15 Promo",
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = if (draft.promoApplied) "Applied: Saving ${formatRupees(draft.discountAmount)}" else "Tap to save 15% up to ₹60",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (draft.promoApplied) ZivoraSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            ZivoraStatusPill(
                                text = if (draft.promoApplied) "Applied" else "Apply",
                                containerColor = if (draft.promoApplied) ZivoraSuccessContainer else ZivoraPeachContainer,
                                contentColor = if (draft.promoApplied) ZivoraSuccess else ZivoraOrangeDark
                            )
                        }

                        HorizontalDivider()

                        Text(
                            text = "Payment Method",
                            style = MaterialTheme.typography.labelLarge
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(PaymentMethod.entries) { method ->
                                FilterChip(
                                    selected = draft.paymentMethod == method,
                                    onClick = { onPaymentChange(method) },
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Total Fare (${draft.selectedVehicle.displayName})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = formatRupees(draft.finalFare),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ZivoraOrangeDark
                                )
                            }
                            Button(
                                onClick = onConfirmBooking,
                                enabled = !draft.isSubmitting && draft.selectedVehicle.isAvailable,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ZivoraOrange,
                                    contentColor = ZivoraPureWhite
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .height(52.dp)
                                    .minimumInteractiveComponentSize()
                                    .testTag("confirm_ride_button")
                            ) {
                                if (draft.isSubmitting) {
                                    CircularProgressIndicator(
                                        color = ZivoraPureWhite,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Text(
                                        text = "Book ${draft.selectedVehicle.displayName}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }
}

@Composable
private fun OngoingRideCard(
    booking: RideBooking,
    onAdvanceRideStatus: (RideBooking, RideStatus) -> Unit
) {
    val vehicle = VehicleType.fromRaw(booking.vehicleType)
    val status = RideStatus.fromRaw(booking.status)

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = ZivoraPeachSoft,
        border = BorderStroke(1.5.dp, ZivoraOrange),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ongoing_ride_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = vehicleIcon(vehicle),
                        contentDescription = null,
                        tint = ZivoraOrange
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${vehicle.displayName} • ${status.label}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                ZivoraStatusPill(
                    text = "OTP ${booking.otpCode}",
                    containerColor = ZivoraOrange,
                    contentColor = ZivoraPureWhite
                )
            }
            Text(
                text = "Driver: ${booking.driverName} (${booking.vehicleNumber})",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${booking.pickupLocation} → ${booking.dropLocation} (${booking.distanceKm} km • ${formatRupees(booking.fareAmount)})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (status == RideStatus.CONFIRMED) {
                    Button(
                        onClick = { onAdvanceRideStatus(booking, RideStatus.IN_PROGRESS) },
                        colors = ButtonDefaults.buttonColors(containerColor = ZivoraOrange),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Start Trip")
                    }
                } else if (status == RideStatus.IN_PROGRESS) {
                    Button(
                        onClick = { onAdvanceRideStatus(booking, RideStatus.COMPLETED) },
                        colors = ButtonDefaults.buttonColors(containerColor = ZivoraSuccess),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Complete Trip")
                    }
                }
                OutlinedButton(
                    onClick = { onAdvanceRideStatus(booking, RideStatus.CANCELLED) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

fun vehicleIcon(vehicle: VehicleType): ImageVector = when (vehicle) {
    VehicleType.CAR -> Icons.Default.DirectionsCar
    VehicleType.TAXI -> Icons.Default.LocalTaxi
    VehicleType.AUTO -> Icons.Default.AirportShuttle
    VehicleType.E_RICKSHAW -> Icons.Default.ElectricRickshaw
    VehicleType.PICKUP -> Icons.Default.LocalShipping
    VehicleType.LOADER -> Icons.Default.LocalShipping
    VehicleType.BIKE -> Icons.Default.Block
}
