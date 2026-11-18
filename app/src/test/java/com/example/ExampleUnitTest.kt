package com.example

import com.example.data.VehicleType
import com.example.data.ZivoraCatalog
import com.example.ui.FoodCartState
import com.example.ui.GroceryCartState
import com.example.ui.RideDraftState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun `active vehicles exclude bike`() {
    val active = VehicleType.activeVehicles
    assertEquals(6, active.size)
    assertFalse(active.contains(VehicleType.BIKE))
    assertFalse(VehicleType.BIKE.isAvailable)
  }

  @Test
  fun `ride draft calculates fare and promo discount accurately`() {
    val draft = RideDraftState(
      selectedVehicle = VehicleType.CAR,
      pickupLocation = "Central Plaza, Sector 14",
      dropLocation = "Cyber Hub, Tower B",
      promoApplied = false
    )
    assertTrue(draft.distanceKm >= 2.4)
    assertTrue(draft.finalFare > VehicleType.CAR.baseFare)

    val discounted = draft.copy(promoApplied = true)
    assertTrue(discounted.discountAmount > 0.0)
    assertTrue(discounted.finalFare < draft.finalFare)
  }

  @Test
  fun `grocery cart unlocks free delivery at 399 or above`() {
    val smallOrder = GroceryCartState(quantities = mapOf("g2" to 1)) // ₹42
    assertEquals(25.0, smallOrder.deliveryFee, 0.01)
    assertEquals(67.0, smallOrder.grandTotal, 0.01)

    val freeOrder = GroceryCartState(quantities = mapOf("g7" to 1)) // ₹549
    assertEquals(0.0, freeOrder.deliveryFee, 0.01)
    assertEquals(549.0, freeOrder.grandTotal, 0.01)
  }

  @Test
  fun `food cart resolves restaurant and totals`() {
    val cart = FoodCartState(quantities = mapOf("f1" to 2))
    assertEquals("Saffron Spice Kitchen", cart.activeRestaurantName)
    assertEquals(2, cart.totalItemCount)
    assertTrue(cart.grandTotal > ZivoraCatalog.restaurants.first().menuItems.first().price)
  }
}
