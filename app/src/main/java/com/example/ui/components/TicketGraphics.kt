package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TicketCategory
import com.example.data.TicketEntity
import com.example.data.TicketStatus
import com.example.ui.theme.BookingTimeAmber
import com.example.ui.theme.CancelledRed
import com.example.ui.theme.CompletedGreen
import com.example.ui.theme.CountdownOrangeRed
import com.example.ui.theme.MontserratFamily
import com.example.ui.theme.PlatformBadgeBg
import com.example.ui.theme.PlatformBadgeText
import com.example.ui.theme.RailBlue
import com.example.ui.theme.ReservedBadgeBg
import com.example.ui.theme.ReservedBadgeText
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UnreservedBadgeBg
import com.example.ui.theme.UnreservedBadgeText
import com.example.ui.theme.UpcomingBadgeGreen
import com.example.ui.theme.UpcomingOrange
import com.example.ui.theme.UpcomingPurpleEnd
import com.example.ui.theme.UpcomingPurpleStart
import java.util.Locale

fun buildHorizontalNotchedPath(
  width: Float,
  height: Float,
  cornerPx: Float,
  notchRadiusPx: Float,
  tearY: Float,
): Path =
  Path().apply {
    moveTo(cornerPx, 0f)
    lineTo(width - cornerPx, 0f)
    arcTo(Rect(width - 2 * cornerPx, 0f, width, 2 * cornerPx), -90f, 90f, false)
    lineTo(width, tearY - notchRadiusPx)
    arcTo(
      Rect(width - notchRadiusPx, tearY - notchRadiusPx, width + notchRadiusPx, tearY + notchRadiusPx),
      -90f,
      -180f,
      false,
    )
    lineTo(width, height - cornerPx)
    arcTo(
      Rect(width - 2 * cornerPx, height - 2 * cornerPx, width, height),
      0f,
      90f,
      false,
    )
    lineTo(cornerPx, height)
    arcTo(Rect(0f, height - 2 * cornerPx, 2 * cornerPx, height), 90f, 90f, false)
    lineTo(0f, tearY + notchRadiusPx)
    arcTo(
      Rect(-notchRadiusPx, tearY - notchRadiusPx, notchRadiusPx, tearY + notchRadiusPx),
      90f,
      -180f,
      false,
    )
    lineTo(0f, cornerPx)
    arcTo(Rect(0f, 0f, 2 * cornerPx, 2 * cornerPx), 180f, 90f, false)
    close()
  }

fun horizontalTicketShape(
  density: Density,
  cornerRadius: Dp,
  notchRadius: Dp,
  notchOffsetFromBottom: Dp,
): Shape = GenericShape { size, _ ->
  with(density) {
    val cornerPx = cornerRadius.toPx()
    val notchPx = notchRadius.toPx()
    val tearY = (size.height - notchOffsetFromBottom.toPx()).coerceIn(cornerPx + notchPx, size.height - cornerPx - notchPx)
    addPath(buildHorizontalNotchedPath(size.width, size.height, cornerPx, notchPx, tearY))
  }
}

fun verticalNotchedTicketShape(
  density: Density,
  cornerRadius: Dp = 18.dp,
  notchRadius: Dp = 11.dp,
  notchFractionX: Float = 0.72f,
): Shape = GenericShape { size, _ ->
  with(density) {
    val c = cornerRadius.toPx()
    val r = notchRadius.toPx()
    val cx = size.width * notchFractionX
    val w = size.width
    val h = size.height
    moveTo(c, 0f)
    lineTo(cx - r, 0f)
    arcTo(Rect(cx - r, -r, cx + r, r), 180f, -180f, false)
    lineTo(w - c, 0f)
    arcTo(Rect(w - 2 * c, 0f, w, 2 * c), -90f, 90f, false)
    lineTo(w, h - c)
    arcTo(Rect(w - 2 * c, h - 2 * c, w, h), 0f, 90f, false)
    lineTo(cx + r, h)
    arcTo(Rect(cx - r, h - r, cx + r, h + r), 0f, -180f, false)
    lineTo(c, h)
    arcTo(Rect(0f, h - 2 * c, 2 * c, h), 90f, 90f, false)
    lineTo(0f, c)
    arcTo(Rect(0f, 0f, 2 * c, 2 * c), 180f, 90f, false)
    close()
  }
}

@Composable
fun HomeUpcomingTicketCard(
  ticket: TicketEntity,
  onBookAgain: () -> Unit,
  onViewDetails: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val density = LocalDensity.current
  val ticketShape = remember(density) { verticalNotchedTicketShape(density) }

  Box(
    modifier =
      modifier
        .fillMaxWidth()
        .clip(ticketShape)
        .background(
          brush = Brush.horizontalGradient(colors = listOf(UpcomingPurpleStart, UpcomingPurpleEnd))
        )
        .clickable(onClick = onViewDetails)
        .testTag("home_upcoming_ticket_card")
  ) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 18.dp)) {
      Text(
        text = ticket.bookingDateLabel,
        color = Color.White,
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
      )
      Spacer(modifier = Modifier.height(10.dp))
      HorizontalDivider(color = Color.White.copy(alpha = 0.25f), thickness = 1.dp)
      Spacer(modifier = Modifier.height(12.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = ticket.sourceStation,
          color = Color.White,
          fontSize = 15.sp,
          fontWeight = FontWeight.Medium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f),
        )
        Text(
          text = ticket.destinationStation,
          color = Color.White,
          fontSize = 15.sp,
          fontWeight = FontWeight.Medium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }
      Spacer(modifier = Modifier.height(12.dp))
      HorizontalDivider(color = Color.White.copy(alpha = 0.25f), thickness = 1.dp)
      Spacer(modifier = Modifier.height(14.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          text = categoryDisplayLabel(ticket.category),
          color = UpcomingBadgeGreen,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedButton(
            onClick = onBookAgain,
            shape = RoundedCornerShape(50),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.75f)),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            modifier = Modifier.height(36.dp).testTag("home_book_again_button"),
          ) {
            Text(
              text = "Book Again",
              color = Color.White,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
            )
          }
          OutlinedButton(
            onClick = onViewDetails,
            shape = RoundedCornerShape(50),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.75f)),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            modifier = Modifier.height(36.dp).testTag("home_view_details_button"),
          ) {
            Text(
              text = "View Details",
              color = Color.White,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
            )
          }
        }
      }
    }
  }
}

@Composable
fun BookingTicketCard(
  ticket: TicketEntity,
  onBookAgain: () -> Unit,
  onViewDetails: () -> Unit,
  onEditClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val borderColor =
    when (ticket.status) {
      TicketStatus.UPCOMING.name -> UpcomingOrange
      TicketStatus.COMPLETED.name -> CompletedGreen
      else -> CancelledRed
    }
  val (badgeBg, badgeText) =
    when (ticket.category) {
      TicketCategory.RESERVED.name -> ReservedBadgeBg to ReservedBadgeText
      TicketCategory.PLATFORM.name -> PlatformBadgeBg to PlatformBadgeText
      else -> UnreservedBadgeBg to UnreservedBadgeText
    }
  val tearYOffset = 52.dp
  val notchRadius = 11.dp
  val cornerRadius = 14.dp
  val density = LocalDensity.current
  val ticketShape =
    remember(density) {
      horizontalTicketShape(density, cornerRadius, notchRadius, tearYOffset)
    }

  Surface(
    shape = ticketShape,
    color = Color.White,
    shadowElevation = 2.dp,
    modifier =
      modifier
        .fillMaxWidth()
        .drawWithContent {
          drawContent()
          val cornerPx = cornerRadius.toPx()
          val r = notchRadius.toPx()
          val tearY = size.height - tearYOffset.toPx()
          val path = buildHorizontalNotchedPath(size.width, size.height, cornerPx, r, tearY)
          drawPath(path = path, color = borderColor, style = Stroke(width = 1.4.dp.toPx()))
          drawLine(
            color = Color(0xFFD5DAE2),
            start = Offset(r + 6.dp.toPx(), tearY),
            end = Offset(size.width - r - 6.dp.toPx(), tearY),
            strokeWidth = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f),
          )
        }
        .testTag("ticket_card_${ticket.utsNumber}"),
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier =
                Modifier.clip(RoundedCornerShape(6.dp))
                  .background(badgeBg)
                  .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(
                text = categoryDisplayLabel(ticket.category),
                color = badgeText,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
            IconButton(
              onClick = onEditClick,
              modifier = Modifier.size(32.dp).testTag("edit_ticket_icon_${ticket.utsNumber}"),
            ) {
              Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Edit ticket ${ticket.utsNumber}",
                tint = RailBlue,
                modifier = Modifier.size(16.dp),
              )
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "UTS: ", color = TextSecondary, fontSize = 13.sp)
            Text(
              text = ticket.utsNumber,
              color = TextPrimary,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Column {
            Text(text = "Ticket Type", color = TextMuted, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = ticket.ticketType,
              color = TextPrimary,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
            )
          }
          Column(horizontalAlignment = Alignment.End) {
            Text(text = "Booking Date", color = TextMuted, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = ticket.bookingDateLabel,
              color = TextPrimary,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = ticket.sourceStation,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
          )
          Text(
            text = if (ticket.distanceKm > 0) "— ${ticket.distanceKm} km —" else "— Platform —",
            color = TextMuted,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp),
          )
          Text(
            text = ticket.destinationStation,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }
      }

      // Bottom action bar ("Book Again | View Details")
      Row(
        modifier = Modifier.fillMaxWidth().height(tearYOffset).padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Box(
          modifier =
            Modifier.weight(1f)
              .fillMaxHeight()
              .clickable(onClick = onBookAgain)
              .testTag("card_book_again_${ticket.utsNumber}"),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            text = "Book Again",
            color = RailBlue,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
          )
        }
        Box(modifier = Modifier.width(1.dp).height(20.dp).background(Color(0xFFD0D5DD)))
        Box(
          modifier =
            Modifier.weight(1f)
              .fillMaxHeight()
              .clickable(onClick = onViewDetails)
              .testTag("card_view_details_${ticket.utsNumber}"),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            text = "View Details",
            color = RailBlue,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
          )
        }
      }
    }
  }
}

@Composable
fun DynamicPreviewHeader(
  remainingSeconds: Int,
  bookingDateTimeFull: String,
  securityCode: String,
  modifier: Modifier = Modifier,
) {
  val minutes = remainingSeconds / 60
  val seconds = remainingSeconds % 60
  val formattedTimer = String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds)
  val textMeasurer = rememberTextMeasurer()

  Box(
    modifier =
      modifier
        .fillMaxWidth()
        .height(194.dp)
        .clipToBounds()
        .background(Color(0xFF1A1312)),
    contentAlignment = Alignment.Center,
  ) {
    Canvas(modifier = Modifier.fillMaxSize().clipToBounds()) {
      // Gapless staggered diamond tessellation matching the screenshot's security pattern:
      // For center (cx, cy) with step (tileW, tileH), vertices (0, +-tileH) and (+-tileW/2, 0)
      // tessellate the entire canvas cleanly with zero gaps.
      val tileW = 74.dp.toPx()
      val tileH = 54.dp.toPx()
      val cols = (size.width / tileW).toInt() + 3
      val rows = (size.height / tileH).toInt() + 3

      val shades =
        listOf(
          Color(0xFF16100F),
          Color(0xFF231A18),
          Color(0xFF2B211F),
          Color(0xFF1C1413),
        )

      for (r in -1..rows) {
        for (c in -1..cols) {
          val cx = c * tileW + if ((r and 1) != 0) tileW * 0.5f else 0f
          val cy = r * tileH
          val diamond =
            Path().apply {
              moveTo(cx, cy - tileH)
              lineTo(cx + tileW * 0.5f, cy)
              lineTo(cx, cy + tileH)
              lineTo(cx - tileW * 0.5f, cy)
              close()
            }
          val shadeIndex = ((r * 3 + c * 5) and 3)
          drawPath(path = diamond, color = shades[shadeIndex])
        }
      }

      // Subtle dark vignette overlay so text stays super crisp
      drawRect(
        brush =
          Brush.horizontalGradient(
            colors =
              listOf(
                Color.Black.copy(alpha = 0.25f),
                Color.Transparent,
                Color.Black.copy(alpha = 0.25f),
              )
          )
      )

      // Vertical dashed lines inside left and right margins
      val marginX = 34.dp.toPx()
      val dash = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
      drawLine(
        color = Color.White.copy(alpha = 0.72f),
        start = Offset(marginX, 0f),
        end = Offset(marginX, size.height),
        strokeWidth = 1.dp.toPx(),
        pathEffect = dash,
      )
      drawLine(
        color = Color.White.copy(alpha = 0.72f),
        start = Offset(size.width - marginX, 0f),
        end = Offset(size.width - marginX, size.height),
        strokeWidth = 1.dp.toPx(),
        pathEffect = dash,
      )

      // Rotated side labels: "INDIAN RAILWAYS" (left) & "भारतीय रेल" (right)
      val leftResult =
        textMeasurer.measure(
          text = "INDIAN RAILWAYS",
          style =
            TextStyle(
              fontFamily = MontserratFamily,
              fontSynthesis = FontSynthesis.All,
              color = Color.White.copy(alpha = 0.42f),
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              letterSpacing = 1.6.sp,
            ),
        )
      rotate(degrees = -90f, pivot = Offset(marginX / 2f, size.height / 2f)) {
        drawText(
          textLayoutResult = leftResult,
          topLeft =
            Offset(
              marginX / 2f - leftResult.size.width / 2f,
              size.height / 2f - leftResult.size.height / 2f,
            ),
        )
      }

      val rightResult =
        textMeasurer.measure(
          text = "भारतीय रेल",
          style =
            TextStyle(
              fontFamily = MontserratFamily,
              fontSynthesis = FontSynthesis.All,
              color = Color.White.copy(alpha = 0.42f),
              fontSize = 15.sp,
              fontWeight = FontWeight.Medium,
            ),
        )
      val rightCenter = size.width - marginX / 2f
      rotate(degrees = -90f, pivot = Offset(rightCenter, size.height / 2f)) {
        drawText(
          textLayoutResult = rightResult,
          topLeft =
            Offset(
              rightCenter - rightResult.size.width / 2f,
              size.height / 2f - rightResult.size.height / 2f,
            ),
        )
      }
    }

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.padding(horizontal = 42.dp, vertical = 10.dp),
    ) {
      Text(
        text = "Dynamic preview will close in",
        color = Color.White,
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.1.sp,
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = formattedTimer,
        color = CountdownOrangeRed,
        fontSize = 42.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.5.sp,
        style =
          TextStyle(
            fontFamily = MontserratFamily,
            fontSynthesis = FontSynthesis.All,
            fontFeatureSettings = "tnum",
          ),
        modifier = Modifier.testTag("dynamic_preview_timer"),
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = "Ticket Booking Date & Time",
        color = Color(0xFFC8CDD5),
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal,
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = bookingDateTimeFull,
        color = BookingTimeAmber,
        fontSize = 23.sp,
        fontWeight = FontWeight.Bold,
        style =
          TextStyle(
            fontFamily = MontserratFamily,
            fontSynthesis = FontSynthesis.All,
            fontFeatureSettings = "tnum",
          ),
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = securityCode,
        color = Color.White,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = "Ticket is Non-Transferable",
        color = Color.White,
        fontSize = 13.5.sp,
        fontWeight = FontWeight.Normal,
      )
    }
  }
}

@Composable
fun NotchedDetailsCard(
  notchOffsetFromBottom: Dp = 84.dp,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  val cornerRadius = 15.dp
  val notchRadius = 10.dp
  val density = LocalDensity.current
  val ticketShape =
    remember(density, notchOffsetFromBottom) {
      horizontalTicketShape(density, cornerRadius, notchRadius, notchOffsetFromBottom)
    }

  Surface(
    shape = ticketShape,
    color = Color.White,
    shadowElevation = 5.dp,
    modifier =
      modifier
        .fillMaxWidth()
        .drawWithContent {
          drawContent()
          val r = notchRadius.toPx()
          val tearY = size.height - notchOffsetFromBottom.toPx()
          drawLine(
            color = Color(0xFFDCE6F2),
            start = Offset(r + 6.dp.toPx(), tearY),
            end = Offset(size.width - r - 6.dp.toPx(), tearY),
            strokeWidth = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f),
          )
        },
  ) {
    Column(modifier = Modifier.fillMaxWidth()) { content() }
  }
}

@Composable
fun DeterministicQrCode(payload: String, modifier: Modifier = Modifier) {
  val matrixSize = 69
  val modules =
    remember(payload) {
      val grid = Array(matrixSize) { BooleanArray(matrixSize) }
      fun drawFinder(top: Int, left: Int) {
        for (r in 0..6) {
          for (c in 0..6) {
            val isBorder = r == 0 || r == 6 || c == 0 || c == 6
            val isInner = r in 2..4 && c in 2..4
            grid[top + r][left + c] = isBorder || isInner
          }
        }
      }
      drawFinder(0, 0)
      drawFinder(0, matrixSize - 7)
      drawFinder(matrixSize - 7, 0)

      for (i in 8 until matrixSize - 8) {
        grid[6][i] = i % 2 == 0
        grid[i][6] = i % 2 == 0
      }

      val ar = matrixSize - 9
      for (r in -2..2) {
        for (c in -2..2) {
          val isEdge = kotlin.math.abs(r) == 2 || kotlin.math.abs(c) == 2
          grid[ar + r][ar + c] = isEdge || (r == 0 && c == 0)
        }
      }

      var state = 0x9E3779B97F4A7C15UL
      for (ch in payload) {
        state = state xor ch.code.toULong()
        state *= 0xBF58476D1CE4E5B9UL
      }
      for (r in 0 until matrixSize) {
        for (c in 0 until matrixSize) {
          val inFinder =
            (r < 8 && c < 8) || (r < 8 && c >= matrixSize - 8) || (r >= matrixSize - 8 && c < 8)
          val inTiming = r == 6 || c == 6
          val inAlign = r in (ar - 2)..(ar + 2) && c in (ar - 2)..(ar + 2)
          if (!inFinder && !inTiming && !inAlign) {
            state = state xor (state shl 13)
            state = state xor (state shr 7)
            state = state xor (state shl 17)
            grid[r][c] = (state and 1UL) == 1UL
          }
        }
      }
      grid
    }

  Surface(
    color = Color.White,
    modifier = modifier,
  ) {
    Canvas(modifier = Modifier.fillMaxSize().padding(4.dp)) {
      val cell = size.width / matrixSize
      for (r in 0 until matrixSize) {
        for (c in 0 until matrixSize) {
          if (modules[r][c]) {
            drawRect(
              color = Color(0xFF111111),
              topLeft = Offset(c * cell, r * cell),
              size = Size(cell + 0.5f, cell + 0.5f),
            )
          }
        }
      }
    }
  }
}

fun categoryDisplayLabel(category: String): String =
  when (category) {
    TicketCategory.RESERVED.name -> "Reserved"
    TicketCategory.PLATFORM.name -> "Platform"
    else -> "Unreserved"
  }
