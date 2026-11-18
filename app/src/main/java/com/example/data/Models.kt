package com.example.data

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class VehicleType(
    val displayName: String,
    val subtitle: String,
    val baseFare: Double,
    val perKmRate: Double,
    val etaText: String,
    val capacityNote: String,
    val isAvailable: Boolean
) {
    CAR(
        displayName = "Car",
        subtitle = "AC Sedan • Comfort ride",
        baseFare = 49.0,
        perKmRate = 14.0,
        etaText = "3 min",
        capacityNote = "Up to 4 seats",
        isAvailable = true
    ),
    TAXI(
        displayName = "Taxi",
        subtitle = "City Cab • Priority pickup",
        baseFare = 59.0,
        perKmRate = 16.0,
        etaText = "4 min",
        capacityNote = "Up to 4 seats + luggage",
        isAvailable = true
    ),
    AUTO(
        displayName = "Auto",
        subtitle = "Quick 3-wheeler • Everyday",
        baseFare = 25.0,
        perKmRate = 10.0,
        etaText = "2 min",
        capacityNote = "Up to 3 seats",
        isAvailable = true
    ),
    E_RICKSHAW(
        displayName = "E-Rickshaw",
        subtitle = "Eco short-hop • Zero emission",
        baseFare = 15.0,
        perKmRate = 7.0,
        etaText = "3 min",
        capacityNote = "Up to 4 seats",
        isAvailable = true
    ),
    PICKUP(
        displayName = "Pickup",
        subtitle = "Mini utility van • Express cargo",
        baseFare = 129.0,
        perKmRate = 24.0,
        etaText = "8 min",
        capacityNote = "Up to 750 kg payload",
        isAvailable = true
    ),
    LOADER(
        displayName = "Loader",
        subtitle = "Heavy cargo • Commercial",
        baseFare = 249.0,
        perKmRate = 34.0,
        etaText = "12 min",
        capacityNote = "Up to 1.5 ton payload",
        isAvailable = true
    ),
    BIKE(
        displayName = "Bike",
        subtitle = "Service disabled",
        baseFare = 0.0,
        perKmRate = 0.0,
        etaText = "Disabled",
        capacityNote = "Unavailable",
        isAvailable = false
    );

    companion object {
        val activeVehicles: List<VehicleType>
            get() = entries.filter { it.isAvailable }

        fun fromRaw(raw: String): VehicleType =
            entries.firstOrNull { it.name == raw } ?: CAR
    }
}

enum class PaymentMethod(val label: String) {
    ZIVORA_PAY("ZIVORA Pay"),
    UPI("UPI Instant"),
    CARD("Credit / Debit Card"),
    CASH("Cash");

    companion object {
        fun fromRaw(raw: String): PaymentMethod =
            entries.firstOrNull { it.name == raw } ?: ZIVORA_PAY
    }
}

enum class RideStatus(val label: String) {
    CONFIRMED("Driver Assigned"),
    IN_PROGRESS("On Trip"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    companion object {
        fun fromRaw(raw: String): RideStatus =
            entries.firstOrNull { it.name == raw } ?: CONFIRMED
    }
}

enum class OrderType(val label: String) {
    GROCERY("Grocery"),
    FOOD("Food");

    companion object {
        fun fromRaw(raw: String): OrderType =
            entries.firstOrNull { it.name == raw } ?: GROCERY
    }
}

enum class OrderStatus(val label: String) {
    PLACED("Order Placed"),
    PREPARING("Preparing"),
    OUT_FOR_DELIVERY("Out for Delivery"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled");

    companion object {
        fun fromRaw(raw: String): OrderStatus =
            entries.firstOrNull { it.name == raw } ?: PLACED
    }
}

@Keep
@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val defaultAddress: String = "Central Plaza, Sector 14",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class SavedAddress(
    @DocumentId val id: String = "",
    val userId: String = "",
    val label: String = "",
    val fullAddress: String = "",
    val landmark: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class RideBooking(
    @DocumentId val id: String = "",
    val userId: String = "",
    val vehicleType: String = VehicleType.CAR.name,
    val pickupLocation: String = "",
    val dropLocation: String = "",
    val distanceKm: Double = 0.0,
    val fareAmount: Double = 0.0,
    val paymentMethod: String = PaymentMethod.ZIVORA_PAY.name,
    val driverName: String = "",
    val vehicleNumber: String = "",
    val otpCode: String = "",
    val status: String = RideStatus.CONFIRMED.name,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class ServiceOrder(
    @DocumentId val id: String = "",
    val userId: String = "",
    val orderType: String = OrderType.GROCERY.name,
    val storeName: String = "",
    val itemsSummary: String = "",
    val itemCount: Long = 0L,
    val totalAmount: Double = 0.0,
    val deliveryAddress: String = "",
    val deliverySlot: String = "",
    val paymentMethod: String = PaymentMethod.ZIVORA_PAY.name,
    val status: String = OrderStatus.PLACED.name,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class GroceryItem(
    val id: String,
    val name: String,
    val category: String,
    val unitWeight: String,
    val price: Double,
    val mrp: Double,
    val rating: Double,
    val badge: String = ""
)

data class Restaurant(
    val id: String,
    val name: String,
    val cuisine: String,
    val rating: Double,
    val prepTime: String,
    val priceForTwo: Int,
    val offerTag: String,
    val menuItems: List<FoodItem>
)

data class FoodItem(
    val id: String,
    val restaurantId: String,
    val restaurantName: String,
    val name: String,
    val description: String,
    val cuisine: String,
    val price: Double,
    val isVegetarian: Boolean,
    val rating: Double,
    val bestseller: Boolean = false
)

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
