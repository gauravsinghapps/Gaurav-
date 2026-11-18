package com.example.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FoodItem
import com.example.data.GroceryItem
import com.example.data.OrderStatus
import com.example.data.OrderType
import com.example.data.PaymentMethod
import com.example.data.RideBooking
import com.example.data.RideStatus
import com.example.data.SavedAddress
import com.example.data.ServiceOrder
import com.example.data.UiState
import com.example.data.UserProfile
import com.example.data.VehicleType
import com.example.data.ZivoraCatalog
import com.example.data.ZivoraRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class RideDraftState(
    val selectedVehicle: VehicleType = VehicleType.CAR,
    val pickupLocation: String = ZivoraCatalog.popularLocations[0],
    val dropLocation: String = ZivoraCatalog.popularLocations[1],
    val paymentMethod: PaymentMethod = PaymentMethod.ZIVORA_PAY,
    val promoApplied: Boolean = false,
    val isSubmitting: Boolean = false
) {
    val distanceKm: Double
        get() = ZivoraCatalog.estimateDistanceKm(pickupLocation, dropLocation)

    val rawFare: Double
        get() {
            val v = if (selectedVehicle.isAvailable) selectedVehicle else VehicleType.CAR
            return ((v.baseFare + distanceKm * v.perKmRate) * 100.0).roundToInt() / 100.0
        }

    val discountAmount: Double
        get() = if (promoApplied) kotlin.math.min(rawFare * 0.15, 60.0).roundToInt().toDouble() else 0.0

    val finalFare: Double
        get() = kotlin.math.max(15.0, rawFare - discountAmount)
}

data class GroceryCartState(
    val quantities: Map<String, Int> = emptyMap(),
    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val deliverySlot: String = "Express 15 mins",
    val paymentMethod: PaymentMethod = PaymentMethod.ZIVORA_PAY,
    val isSubmitting: Boolean = false
) {
    val totalItemCount: Int
        get() = quantities.values.sum()

    val itemSubtotal: Double
        get() = quantities.entries.sumOf { (id, qty) ->
            val item = ZivoraCatalog.groceryItems.firstOrNull { it.id == id }
            (item?.price ?: 0.0) * qty
        }

    val deliveryFee: Double
        get() = if (itemSubtotal >= 399.0 || itemSubtotal == 0.0) 0.0 else 25.0

    val grandTotal: Double
        get() = itemSubtotal + deliveryFee
}

data class FoodCartState(
    val quantities: Map<String, Int> = emptyMap(),
    val selectedCuisine: String = "All",
    val searchQuery: String = "",
    val paymentMethod: PaymentMethod = PaymentMethod.ZIVORA_PAY,
    val isSubmitting: Boolean = false
) {
    private val allFoodItems: List<FoodItem>
        get() = ZivoraCatalog.restaurants.flatMap { it.menuItems }

    val totalItemCount: Int
        get() = quantities.values.sum()

    val activeRestaurantName: String?
        get() {
            val firstId = quantities.entries.firstOrNull { it.value > 0 }?.key ?: return null
            return allFoodItems.firstOrNull { it.id == firstId }?.restaurantName
        }

    val itemSubtotal: Double
        get() = quantities.entries.sumOf { (id, qty) ->
            val item = allFoodItems.firstOrNull { it.id == id }
            (item?.price ?: 0.0) * qty
        }

    val deliveryFee: Double
        get() = if (itemSubtotal == 0.0) 0.0 else 29.0

    val grandTotal: Double
        get() = itemSubtotal + deliveryFee
}

class ZivoraViewModel(
    private val repository: ZivoraRepository,
    private val currentUserId: String,
    userDisplayName: String,
    userEmail: String
) : ViewModel() {

    val profileState: StateFlow<UiState<UserProfile?>> = repository.observeProfile(currentUserId)
        .map<UserProfile?, UiState<UserProfile?>> { UiState.Success(it) }
        .catch { error ->
            emit(UiState.Error(unwrapFirestoreError(error, "Failed to load profile")))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    val addressesState: StateFlow<UiState<List<SavedAddress>>> = repository.observeSavedAddresses(currentUserId)
        .map<List<SavedAddress>, UiState<List<SavedAddress>>> { UiState.Success(it) }
        .catch { error ->
            emit(UiState.Error(unwrapFirestoreError(error, "Failed to load addresses")))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    val rideBookingsState: StateFlow<UiState<List<RideBooking>>> = repository.observeRideBookings(currentUserId)
        .map<List<RideBooking>, UiState<List<RideBooking>>> { UiState.Success(it) }
        .catch { error ->
            emit(UiState.Error(unwrapFirestoreError(error, "Failed to load ride bookings")))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    val ordersState: StateFlow<UiState<List<ServiceOrder>>> = repository.observeOrders(currentUserId)
        .map<List<ServiceOrder>, UiState<List<ServiceOrder>>> { UiState.Success(it) }
        .catch { error ->
            emit(UiState.Error(unwrapFirestoreError(error, "Failed to load orders")))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    private val _rideDraft = MutableStateFlow(RideDraftState())
    val rideDraft: StateFlow<RideDraftState> = _rideDraft.asStateFlow()

    private val _groceryCart = MutableStateFlow(GroceryCartState())
    val groceryCart: StateFlow<GroceryCartState> = _groceryCart.asStateFlow()

    private val _foodCart = MutableStateFlow(FoodCartState())
    val foodCart: StateFlow<FoodCartState> = _foodCart.asStateFlow()

    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureUserProfile(currentUserId, userDisplayName, userEmail)
        }
    }

    fun clearFeedback() {
        _feedbackMessage.value = null
    }

    fun selectVehicle(vehicle: VehicleType) {
        if (!vehicle.isAvailable || vehicle == VehicleType.BIKE) {
            _feedbackMessage.value = "Bike booking is disabled on ZIVORA. Please choose Car, Taxi, Auto, E-Rickshaw, Pickup, or Loader."
            return
        }
        _rideDraft.update { it.copy(selectedVehicle = vehicle) }
    }

    fun updateRidePickup(pickup: String) {
        _rideDraft.update { it.copy(pickupLocation = pickup) }
    }

    fun updateRideDrop(drop: String) {
        _rideDraft.update { it.copy(dropLocation = drop) }
    }

    fun swapRideLocations() {
        _rideDraft.update {
            it.copy(pickupLocation = it.dropLocation, dropLocation = it.pickupLocation)
        }
    }

    fun updateRidePayment(method: PaymentMethod) {
        _rideDraft.update { it.copy(paymentMethod = method) }
    }

    fun toggleRidePromo() {
        _rideDraft.update { it.copy(promoApplied = !it.promoApplied) }
    }

    fun confirmRideBooking(onSuccess: () -> Unit = {}) {
        val draft = _rideDraft.value
        if (draft.isSubmitting) return
        if (draft.selectedVehicle == VehicleType.BIKE || !draft.selectedVehicle.isAvailable) {
            _feedbackMessage.value = "Bike booking is disabled on ZIVORA."
            return
        }
        if (draft.pickupLocation.trim().length < 2 || draft.dropLocation.trim().length < 2) {
            _feedbackMessage.value = "Enter both pickup and drop-off locations."
            return
        }
        if (draft.pickupLocation.trim().equals(draft.dropLocation.trim(), ignoreCase = true)) {
            _feedbackMessage.value = "Pickup and drop-off locations must be different."
            return
        }

        _rideDraft.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            val result = repository.createRideBooking(
                userId = currentUserId,
                vehicleType = draft.selectedVehicle,
                pickupLocation = draft.pickupLocation,
                dropLocation = draft.dropLocation,
                distanceKm = draft.distanceKm,
                fareAmount = draft.finalFare,
                paymentMethod = draft.paymentMethod
            )
            _rideDraft.update { it.copy(isSubmitting = false) }
            result.fold(
                onSuccess = {
                    _feedbackMessage.value = "${draft.selectedVehicle.displayName} booked! Driver assigned."
                    onSuccess()
                },
                onFailure = { err ->
                    _feedbackMessage.value = err.localizedMessage ?: "Could not complete ride booking."
                }
            )
        }
    }

    fun advanceRideStatus(booking: RideBooking, nextStatus: RideStatus) {
        viewModelScope.launch {
            val result = repository.updateRideStatus(currentUserId, booking.id, nextStatus)
            result.fold(
                onSuccess = {
                    _feedbackMessage.value = "Ride status updated to ${nextStatus.label}."
                },
                onFailure = { err ->
                    _feedbackMessage.value = err.localizedMessage ?: "Failed to update ride status."
                }
            )
        }
    }

    // --- Grocery Actions ---
    fun setGroceryCategory(category: String) {
        _groceryCart.update { it.copy(selectedCategory = category) }
    }

    fun setGrocerySearch(query: String) {
        _groceryCart.update { it.copy(searchQuery = query) }
    }

    fun setGrocerySlot(slot: String) {
        _groceryCart.update { it.copy(deliverySlot = slot) }
    }

    fun setGroceryPayment(method: PaymentMethod) {
        _groceryCart.update { it.copy(paymentMethod = method) }
    }

    fun adjustGroceryQuantity(itemId: String, delta: Int) {
        _groceryCart.update { state ->
            val current = state.quantities[itemId] ?: 0
            val next = (current + delta).coerceIn(0, 20)
            val updated = state.quantities.toMutableMap()
            if (next == 0) updated.remove(itemId) else updated[itemId] = next
            state.copy(quantities = updated)
        }
    }

    fun placeGroceryOrder(deliveryAddress: String, onSuccess: () -> Unit = {}) {
        val state = _groceryCart.value
        if (state.isSubmitting || state.totalItemCount == 0) return
        val address = deliveryAddress.trim().ifBlank { ZivoraCatalog.popularLocations.first() }
        val summary = state.quantities.entries
            .filter { it.value > 0 }
            .mapNotNull { (id, qty) ->
                ZivoraCatalog.groceryItems.firstOrNull { it.id == id }?.let { "${qty}x ${it.name}" }
            }
            .joinToString(", ")
            .take(500)

        _groceryCart.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            val result = repository.createOrder(
                userId = currentUserId,
                orderType = OrderType.GROCERY,
                storeName = "ZIVORA Fresh Mart",
                itemsSummary = summary,
                itemCount = state.totalItemCount.coerceAtMost(100),
                totalAmount = state.grandTotal,
                deliveryAddress = address,
                deliverySlot = state.deliverySlot,
                paymentMethod = state.paymentMethod
            )
            _groceryCart.update { it.copy(isSubmitting = false) }
            result.fold(
                onSuccess = {
                    _groceryCart.update { it.copy(quantities = emptyMap()) }
                    _feedbackMessage.value = "Grocery order placed! Delivering in ${state.deliverySlot}."
                    onSuccess()
                },
                onFailure = { err ->
                    _feedbackMessage.value = err.localizedMessage ?: "Could not place grocery order."
                }
            )
        }
    }

    // --- Food Actions ---
    fun setFoodCuisine(cuisine: String) {
        _foodCart.update { it.copy(selectedCuisine = cuisine) }
    }

    fun setFoodSearch(query: String) {
        _foodCart.update { it.copy(searchQuery = query) }
    }

    fun setFoodPayment(method: PaymentMethod) {
        _foodCart.update { it.copy(paymentMethod = method) }
    }

    fun adjustFoodQuantity(item: FoodItem, delta: Int) {
        _foodCart.update { state ->
            val currentRestaurant = state.activeRestaurantName
            val cleanedQuantities = if (delta > 0 && currentRestaurant != null && currentRestaurant != item.restaurantName) {
                // Switch restaurant cleanly if adding from a different restaurant
                _feedbackMessage.value = "Cart updated for ${item.restaurantName}."
                mutableMapOf()
            } else {
                state.quantities.toMutableMap()
            }
            val current = cleanedQuantities[item.id] ?: 0
            val next = (current + delta).coerceIn(0, 20)
            if (next == 0) cleanedQuantities.remove(item.id) else cleanedQuantities[item.id] = next
            state.copy(quantities = cleanedQuantities)
        }
    }

    fun placeFoodOrder(deliveryAddress: String, onSuccess: () -> Unit = {}) {
        val state = _foodCart.value
        if (state.isSubmitting || state.totalItemCount == 0) return
        val address = deliveryAddress.trim().ifBlank { ZivoraCatalog.popularLocations.first() }
        val allFood = ZivoraCatalog.restaurants.flatMap { it.menuItems }
        val restaurant = state.activeRestaurantName ?: "ZIVORA Kitchen"
        val summary = state.quantities.entries
            .filter { it.value > 0 }
            .mapNotNull { (id, qty) ->
                allFood.firstOrNull { it.id == id }?.let { "${qty}x ${it.name}" }
            }
            .joinToString(", ")
            .take(500)

        _foodCart.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            val result = repository.createOrder(
                userId = currentUserId,
                orderType = OrderType.FOOD,
                storeName = restaurant,
                itemsSummary = summary,
                itemCount = state.totalItemCount.coerceAtMost(100),
                totalAmount = state.grandTotal,
                deliveryAddress = address,
                deliverySlot = "25-30 mins",
                paymentMethod = state.paymentMethod
            )
            _foodCart.update { it.copy(isSubmitting = false) }
            result.fold(
                onSuccess = {
                    _foodCart.update { it.copy(quantities = emptyMap()) }
                    _feedbackMessage.value = "Food order placed with $restaurant!"
                    onSuccess()
                },
                onFailure = { err ->
                    _feedbackMessage.value = err.localizedMessage ?: "Could not place food order."
                }
            )
        }
    }

    fun advanceOrderStatus(order: ServiceOrder, nextStatus: OrderStatus) {
        viewModelScope.launch {
            val result = repository.updateOrderStatus(currentUserId, order.id, nextStatus)
            result.fold(
                onSuccess = {
                    _feedbackMessage.value = "Order updated to ${nextStatus.label}."
                },
                onFailure = { err ->
                    _feedbackMessage.value = err.localizedMessage ?: "Could not update order."
                }
            )
        }
    }

    // --- Address Actions ---
    fun setActiveAddress(address: String) {
        viewModelScope.launch {
            val result = repository.updateDefaultAddress(currentUserId, address)
            result.fold(
                onSuccess = {
                    _rideDraft.update { it.copy(pickupLocation = address) }
                    _feedbackMessage.value = "Active location updated."
                },
                onFailure = { err ->
                    _feedbackMessage.value = err.localizedMessage ?: "Failed to update location."
                }
            )
        }
    }

    fun addAddress(label: String, fullAddress: String, landmark: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            val result = repository.addSavedAddress(currentUserId, label, fullAddress, landmark)
            result.fold(
                onSuccess = {
                    repository.updateDefaultAddress(currentUserId, fullAddress)
                    _feedbackMessage.value = "Saved address \"$label\"."
                    onDone()
                },
                onFailure = { err ->
                    _feedbackMessage.value = err.localizedMessage ?: "Failed to save address."
                }
            )
        }
    }

    fun deleteAddress(addressId: String) {
        viewModelScope.launch {
            repository.deleteSavedAddress(currentUserId, addressId)
        }
    }

    private fun unwrapFirestoreError(error: Throwable, fallback: String): String {
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val rootError = firestoreError ?: error
        Log.w(TAG, "Stream error", rootError)
        return rootError.localizedMessage ?: fallback
    }

    private companion object {
        const val TAG = "ZivoraVM"
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
