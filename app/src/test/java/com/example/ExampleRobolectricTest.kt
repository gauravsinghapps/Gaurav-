package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.VehicleType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `zivora branding strings and active vehicles match specification`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    assertEquals("ZIVORA", context.getString(R.string.app_name))
    assertEquals("Everything You Need, One App.", context.getString(R.string.app_tagline))

    assertFalse(VehicleType.BIKE.isAvailable)
    assertFalse(VehicleType.activeVehicles.contains(VehicleType.BIKE))
    assertTrue(
      VehicleType.activeVehicles.containsAll(
        listOf(
          VehicleType.CAR,
          VehicleType.TAXI,
          VehicleType.AUTO,
          VehicleType.E_RICKSHAW,
          VehicleType.PICKUP,
          VehicleType.LOADER
        )
      )
    )
  }
}
