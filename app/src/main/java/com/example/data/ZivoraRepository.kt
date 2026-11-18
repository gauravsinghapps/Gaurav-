package com.example.data

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class ZivoraRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("User must be signed in with Google before accessing ZIVORA data.")
    }

    fun observeProfile(userId: String): Flow<UserProfile?> = flow {
        val path = "users/$userId"
        emitAll(
            db.collection("users").document(userId)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObject(
                        UserProfile::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    handleFlowError(error, OperationType.GET, path, userId)
                }
        )
    }

    fun observeSavedAddresses(userId: String): Flow<List<SavedAddress>> = flow {
        val path = "users/$userId/addresses"
        emitAll(
            db.collection("users").document(userId).collection("addresses")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        SavedAddress::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    handleFlowError(error, OperationType.LIST, path, userId)
                }
        )
    }

    fun observeRideBookings(userId: String): Flow<List<RideBooking>> = flow {
        val path = "users/$userId/rideBookings"
        emitAll(
            db.collection("users").document(userId).collection("rideBookings")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        RideBooking::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    handleFlowError(error, OperationType.LIST, path, userId)
                }
        )
    }

    fun observeOrders(userId: String): Flow<List<ServiceOrder>> = flow {
        val path = "users/$userId/orders"
        emitAll(
            db.collection("users").document(userId).collection("orders")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        ServiceOrder::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    handleFlowError(error, OperationType.LIST, path, userId)
                }
        )
    }

    suspend fun ensureUserProfile(
        userId: String,
        displayName: String,
        email: String
    ): Result<Unit> {
        val docPath = "users/$userId"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Signed-in user mismatch." }
            val docRef = db.collection("users").document(userId)
            val existing = docRef.get().await()
            if (!existing.exists()) {
                val safeName = displayName.trim().ifBlank { "ZIVORA Member" }.take(80)
                val safeEmail = email.trim().take(120)
                val payload = mapOf(
                    "userId" to uid,
                    "displayName" to safeName,
                    "email" to safeEmail,
                    "defaultAddress" to ZivoraCatalog.popularLocations.first(),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun updateDefaultAddress(userId: String, address: String): Result<Unit> {
        val docPath = "users/$userId"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Signed-in user mismatch." }
            val clean = address.trim()
            require(clean.length in 1..200) { "Address must be between 1 and 200 characters." }
            db.collection("users").document(userId).update(
                mapOf(
                    "defaultAddress" to clean,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun addSavedAddress(
        userId: String,
        label: String,
        fullAddress: String,
        landmark: String
    ): Result<Unit> {
        var docPath = "users/$userId/addresses"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Signed-in user mismatch." }
            val cleanLabel = label.trim()
            val cleanAddr = fullAddress.trim()
            val cleanLandmark = landmark.trim()
            require(cleanLabel.length in 1..30) { "Label must be 1 to 30 characters." }
            require(cleanAddr.length in 3..200) { "Address must be 3 to 200 characters." }
            require(cleanLandmark.length <= 100) { "Landmark must be under 100 characters." }

            val docRef = db.collection("users").document(userId).collection("addresses").document()
            docPath = docRef.path
            val payload = mutableMapOf<String, Any>(
                "userId" to uid,
                "label" to cleanLabel,
                "fullAddress" to cleanAddr,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (cleanLandmark.isNotEmpty()) {
                payload["landmark"] = cleanLandmark
            }
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun deleteSavedAddress(userId: String, addressId: String): Result<Unit> {
        val docPath = "users/$userId/addresses/$addressId"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Signed-in user mismatch." }
            require(addressId.isNotBlank()) { "Address ID required." }
            db.collection("users").document(userId)
                .collection("addresses").document(addressId)
                .delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }

    suspend fun createRideBooking(
        userId: String,
        vehicleType: VehicleType,
        pickupLocation: String,
        dropLocation: String,
        distanceKm: Double,
        fareAmount: Double,
        paymentMethod: PaymentMethod
    ): Result<String> {
        var docPath = "users/$userId/rideBookings"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Signed-in user mismatch." }
            require(vehicleType != VehicleType.BIKE && vehicleType.isAvailable) {
                "Bike booking is completely disabled on ZIVORA."
            }
            val cleanPickup = pickupLocation.trim()
            val cleanDrop = dropLocation.trim()
            require(cleanPickup.length in 2..150) { "Pickup location must be 2 to 150 characters." }
            require(cleanDrop.length in 2..150) { "Drop location must be 2 to 150 characters." }
            require(distanceKm > 0.0 && distanceKm <= 500.0) { "Invalid distance." }
            require(fareAmount > 0.0 && fareAmount <= 50000.0) { "Invalid fare amount." }

            val driverPool = listOf(
                "Rajesh Verma" to "DL 01 ZV 4821",
                "Amit Sharma" to "DL 04 ZV 7190",
                "Suresh Yadav" to "HR 26 ZV 3305",
                "Vikram Singh" to "UP 16 ZV 9012"
            )
            val (driverName, vehicleNumber) = driverPool[Random.nextInt(driverPool.size)]
            val otpCode = (1000 + Random.nextInt(9000)).toString()

            val docRef = db.collection("users").document(userId).collection("rideBookings").document()
            docPath = docRef.path
            val payload = mapOf(
                "userId" to uid,
                "vehicleType" to vehicleType.name,
                "pickupLocation" to cleanPickup,
                "dropLocation" to cleanDrop,
                "distanceKm" to distanceKm,
                "fareAmount" to fareAmount,
                "paymentMethod" to paymentMethod.name,
                "driverName" to driverName,
                "vehicleNumber" to vehicleNumber,
                "otpCode" to otpCode,
                "status" to RideStatus.CONFIRMED.name,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun updateRideStatus(
        userId: String,
        bookingId: String,
        newStatus: RideStatus
    ): Result<Unit> {
        val docPath = "users/$userId/rideBookings/$bookingId"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Signed-in user mismatch." }
            require(bookingId.isNotBlank()) { "Booking ID required." }
            db.collection("users").document(userId)
                .collection("rideBookings").document(bookingId)
                .update(
                    mapOf(
                        "status" to newStatus.name,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun createOrder(
        userId: String,
        orderType: OrderType,
        storeName: String,
        itemsSummary: String,
        itemCount: Int,
        totalAmount: Double,
        deliveryAddress: String,
        deliverySlot: String,
        paymentMethod: PaymentMethod
    ): Result<String> {
        var docPath = "users/$userId/orders"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Signed-in user mismatch." }
            val cleanStore = storeName.trim().take(100)
            val cleanSummary = itemsSummary.trim().take(500)
            val cleanAddress = deliveryAddress.trim().take(200)
            val cleanSlot = deliverySlot.trim().take(60)
            require(cleanStore.length >= 2) { "Store name required." }
            require(cleanSummary.length >= 2) { "Order items required." }
            require(itemCount in 1..100) { "Item count must be between 1 and 100." }
            require(totalAmount > 0.0 && totalAmount <= 100000.0) { "Invalid order total." }
            require(cleanAddress.length >= 3) { "Delivery address required." }

            val docRef = db.collection("users").document(userId).collection("orders").document()
            docPath = docRef.path
            val payload = mapOf(
                "userId" to uid,
                "orderType" to orderType.name,
                "storeName" to cleanStore,
                "itemsSummary" to cleanSummary,
                "itemCount" to itemCount.toLong(),
                "totalAmount" to totalAmount,
                "deliveryAddress" to cleanAddress,
                "deliverySlot" to cleanSlot,
                "paymentMethod" to paymentMethod.name,
                "status" to OrderStatus.PLACED.name,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun updateOrderStatus(
        userId: String,
        orderId: String,
        newStatus: OrderStatus
    ): Result<Unit> {
        val docPath = "users/$userId/orders/$orderId"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Signed-in user mismatch." }
            require(orderId.isNotBlank()) { "Order ID required." }
            db.collection("users").document(userId)
                .collection("orders").document(orderId)
                .update(
                    mapOf(
                        "status" to newStatus.name,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    private fun handleFlowError(
        error: Throwable,
        operationType: OperationType,
        path: String,
        expectedUid: String
    ): Nothing {
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val exceptionToLog = firestoreError ?: (error as? Exception)
        if (exceptionToLog != null && auth.currentUser?.uid == expectedUid) {
            handleFirestoreError(exceptionToLog, operationType, path)
        }
        throw error
    }
}
