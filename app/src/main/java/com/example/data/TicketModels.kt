package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

enum class TicketCategory(val displayLabel: String) {
  UNRESERVED("Unreserved"),
  RESERVED("Reserved"),
  PLATFORM("Platform"),
}

enum class TicketStatus(val displayLabel: String) {
  UPCOMING("Upcoming"),
  COMPLETED("Completed"),
  CANCELLED("Cancelled"),
}

@Entity(tableName = "tickets")
data class TicketEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val utsNumber: String,
  val category: String, // UNRESERVED, RESERVED, PLATFORM
  val ticketType: String, // JOURNEY, RETURN, PLATFORM, SEASON
  val trainType: String, // ORDINARY, MAIL/EXP, SUPERFAST
  val travelClass: String, // SECOND, SLEEPER, FIRST, AC CHAIR
  val sourceStation: String,
  val destinationStation: String,
  val viaRoute: String,
  val distanceKm: Int,
  val adults: Int,
  val children: Int,
  val fareAmount: Double,
  val bookingTimestampMillis: Long,
  val bookingDateLabel: String, // e.g., "Tue, 27 Oct 26"
  val bookingDateTimeFull: String, // e.g., "27 Oct 2026, 09:55"
  val securityCode: String, // e.g., "R18103"
  val irCode: String, // e.g., "IR:27AAAGM0289C2ZI"
  val mobileNumber: String,
  val passengerName: String,
  val status: String, // UPCOMING, COMPLETED, CANCELLED
)

data class StationInfo(val code: String, val name: String, val kmMarker: Int) {
  val displayUpper: String
    get() = name.uppercase(Locale.ENGLISH)
}

object StationCatalog {
  val stations: List<StationInfo> =
    listOf(
      StationInfo("WR", "WARDHA JN.", 0),
      StationInfo("PLO", "PULGAON JN.", 30),
      StationInfo("SEGM", "SEWAGRAM", 8),
      StationInfo("NGP", "NAGPUR", 79),
      StationInfo("HGT", "HINGANGHAT", 34),
      StationInfo("WRR", "WARORA", 72),
      StationInfo("DMN", "DHAMANGAON", 50),
      StationInfo("CND", "CHANDUR", 68),
      StationInfo("BD", "BADNERA JN.", 96),
      StationInfo("AMI", "AMRAVATI", 106),
      StationInfo("AK", "AKOLA JN.", 174),
      StationInfo("CD", "CHANDRAPUR", 119),
      StationInfo("BPQ", "BALLARSHAH", 133),
      StationInfo("PUNE", "PUNE JN.", 660),
      StationInfo("CSMT", "CSMT MUMBAI", 760),
    )

  fun estimateDistanceKm(source: String, destination: String, category: TicketCategory): Int {
    if (category == TicketCategory.PLATFORM) return 0
    val s = source.trim().uppercase(Locale.ENGLISH)
    val d = destination.trim().uppercase(Locale.ENGLISH)
    if (s == d) return 0
    if ((s.contains("HINGANGHAT") && d.contains("WARORA")) || (s.contains("WARORA") && d.contains("HINGANGHAT"))) {
      return 38
    }
    if ((s.contains("WARDHA") && d.contains("PULGAON")) || (s.contains("PULGAON") && d.contains("WARDHA"))) {
      return 30
    }
    if ((s.contains("WARDHA") && d.contains("NAGPUR")) || (s.contains("NAGPUR") && d.contains("WARDHA"))) {
      return 79
    }
    val srcInfo = stations.find { it.displayUpper == s || it.code == s }
    val dstInfo = stations.find { it.displayUpper == d || it.code == d }
    return if (srcInfo != null && dstInfo != null) {
      max(10, abs(srcInfo.kmMarker - dstInfo.kmMarker))
    } else {
      val hash = abs((s + d).hashCode()) % 110
      max(15, hash + 15)
    }
  }

  fun calculateFare(
    distanceKm: Int,
    adults: Int,
    children: Int,
    category: TicketCategory,
    ticketType: String,
    trainType: String,
    travelClass: String,
  ): Double {
    if (category == TicketCategory.PLATFORM) {
      return 10.0 * max(1, adults + children)
    }
    val basePerAdult =
      when {
        distanceKm <= 35 -> 10.0
        distanceKm <= 60 -> 15.0
        distanceKm <= 90 -> 20.0
        distanceKm <= 140 -> 30.0
        else -> (distanceKm * 0.25).roundToInt().coerceAtLeast(35).toDouble()
      }
    val trainMultiplier =
      when (trainType.uppercase(Locale.ENGLISH)) {
        "MAIL/EXP", "EXPRESS" -> 1.5
        "SUPERFAST" -> 2.0
        else -> 1.0
      }
    val classMultiplier =
      when (travelClass.uppercase(Locale.ENGLISH)) {
        "SLEEPER" -> 2.4
        "FIRST" -> 4.0
        "AC CHAIR" -> 5.0
        else -> 1.0
      }
    val typeMultiplier =
      when (ticketType.uppercase(Locale.ENGLISH)) {
        "RETURN" -> 2.0
        "SEASON" -> 15.0
        else -> 1.0
      }
    val singleAdult =
      ((basePerAdult * trainMultiplier * classMultiplier * typeMultiplier) / 5.0).roundToInt() * 5.0
    val finalAdult = max(10.0, singleAdult)
    val childFare = finalAdult * 0.5
    return (adults * finalAdult) + (children * childFare)
  }

  fun generateUtsCode(seed: Long = System.currentTimeMillis()): String {
    val letters = "ABCDEFGHJKLMNPQRSTUVWXYZ"
    val digits = "0123456789"
    var x = abs(seed)
    val sb = StringBuilder("X")
    val pattern = "LLDLLLLDLL"
    for ((idx, ch) in pattern.withIndex()) {
      x = (x * 1103515245L + 12345L + idx) and 0x7fffffffL
      val pool = if (ch == 'L') letters else digits
      sb.append(pool[(x % pool.length).toInt()])
    }
    return sb.toString().take(10)
  }

  fun generateSecurityCode(seed: Long = System.currentTimeMillis()): String {
    val num = 10000 + (abs(seed) % 89999).toInt()
    return "R$num"
  }

  fun formatShortDate(millis: Long): String =
    SimpleDateFormat("EEE, d MMM yy", Locale.ENGLISH).format(Date(millis))

  fun formatFullDateTime(millis: Long): String =
    SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ENGLISH).format(Date(millis))

  fun formatRupees(amount: Double): String = String.format(Locale.ENGLISH, "₹%.2f", amount)
}
