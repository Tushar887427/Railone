package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.StationCatalog
import com.example.data.TicketCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("RailOne", appName)
  }

  @Test
  fun `wardha to pulgaon distance and fare match screenshot`() {
    val distance =
      StationCatalog.estimateDistanceKm("WARDHA JN.", "PULGAON JN.", TicketCategory.UNRESERVED)
    assertEquals(30, distance)

    val fare =
      StationCatalog.calculateFare(
        distanceKm = distance,
        adults = 1,
        children = 0,
        category = TicketCategory.UNRESERVED,
        ticketType = "JOURNEY",
        trainType = "ORDINARY",
        travelClass = "SECOND",
      )
    assertEquals(10.0, fare, 0.01)
  }

  @Test
  fun `generated uts code has expected format`() {
    val code = StationCatalog.generateUtsCode(123456789L)
    assertEquals(10, code.length)
    assertTrue(code.startsWith("X"))
  }
}
