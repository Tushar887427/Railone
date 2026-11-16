package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AltRoute
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StationCatalog
import com.example.data.TicketEntity
import com.example.data.TicketStatus
import com.example.ui.components.DeterministicQrCode
import com.example.ui.components.DynamicPreviewHeader
import com.example.ui.components.NotchedDetailsCard
import com.example.ui.theme.ActivePillBg
import com.example.ui.theme.ActivePillText
import com.example.ui.theme.CancelledRed
import com.example.ui.theme.CyanTicketEdge
import com.example.ui.theme.DividerLight
import com.example.ui.theme.MontserratFamily
import com.example.ui.theme.PageBackground
import com.example.ui.theme.QrContainerBg
import com.example.ui.theme.RailBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningNoteBg
import com.example.ui.theme.WarningNoteText
import kotlinx.coroutines.delay

@Composable
fun BookingDetailsScreen(
  ticket: TicketEntity?,
  onBack: () -> Unit,
  onBookConnectingJourney: (TicketEntity) -> Unit,
) {
  var remainingSeconds by remember(ticket?.id) { mutableIntStateOf(296) } // 04:56 initial

  LaunchedEffect(ticket?.id) {
    while (remainingSeconds > 0) {
      delay(1000L)
      remainingSeconds -= 1
    }
  }

  Scaffold(
    containerColor = Color.White,
    topBar = {
      Surface(color = RailBlue, modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier =
            Modifier.fillMaxWidth()
              .windowInsetsPadding(WindowInsets.statusBars)
              .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              onClick = onBack,
              shape = CircleShape,
              color = Color.Transparent,
              border = BorderStroke(1.5.dp, Color.White),
              modifier = Modifier.size(40.dp).testTag("details_back_button"),
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                  contentDescription = "Navigate back",
                  tint = Color.White,
                  modifier = Modifier.size(20.dp),
                )
              }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
              Text(
                text = "Booking Details",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
              )
              Spacer(modifier = Modifier.height(1.dp))
              Text(
                text = "Mobile: ${ticket?.mobileNumber ?: "9405937428"}",
                color = Color.White.copy(alpha = 0.92f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
              )
            }
          }
        }
      }
    },
  ) { innerPadding ->
    if (ticket == null) {
      Box(
        modifier = Modifier.fillMaxSize().padding(innerPadding),
        contentAlignment = Alignment.Center,
      ) {
        Text("Loading ticket details...", color = TextSecondary)
      }
      return@Scaffold
    }

    Column(
      modifier =
        Modifier.fillMaxSize()
          .padding(innerPadding)
          .windowInsetsPadding(WindowInsets.navigationBars)
          .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      // Greeting strip
      Surface(color = Color.White, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().widthIn(max = 600.dp)) {
          Text(
            text = "Thank You ${ticket.passengerName}, Happy Journey !",
            color = Color(0xFF4A505A),
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
          )
        }
      }
      HorizontalDivider(color = Color(0xFFEDF0F5))

      Column(
        modifier =
          Modifier.fillMaxWidth()
            .background(PageBackground)
            .widthIn(max = 600.dp)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        // Cyan-capped Notched Ticket Card matching screenshot
        NotchedDetailsCard(
          notchOffsetFromBottom = 68.dp,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Box(modifier = Modifier.fillMaxWidth().height(13.5.dp).background(CyanTicketEdge))

          DynamicPreviewHeader(
            remainingSeconds = remainingSeconds,
            bookingDateTimeFull = ticket.bookingDateTimeFull,
            securityCode = ticket.securityCode,
          )

          Column(
            modifier =
              Modifier.fillMaxWidth()
                .padding(start = 14.dp, end = 14.dp, top = 16.dp, bottom = 18.dp)
          ) {
            // Journey Ticket + ACTIVE pill
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Column {
                Text(
                  text = "Journey Ticket",
                  fontFamily = FontFamily.SansSerif,
                  color = Color(0xFF7B7B7B),
                  fontSize = 13.5.sp,
                  fontWeight = FontWeight.Medium,
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                  text = ticket.utsNumber,
                  fontFamily = FontFamily.SansSerif,
                  color = Color(0xFF222222),
                  fontSize = 14.5.sp,
                  fontWeight = FontWeight.Bold,
                )
              }

              val (pillBg, pillText, statusLabel) =
                when (ticket.status) {
                  TicketStatus.UPCOMING.name -> Triple(ActivePillBg, ActivePillText, "ACTIVE")
                  TicketStatus.COMPLETED.name -> Triple(Color(0xFFE8F1FC), RailBlue, "COMPLETED")
                  else -> Triple(WarningNoteBg, CancelledRed, "CANCELLED")
                }
              Box(
                modifier =
                  Modifier.clip(RoundedCornerShape(50))
                    .background(pillBg)
                    .padding(horizontal = 12.dp, vertical = 5.dp)
              ) {
                Text(
                  text = "• $statusLabel",
                  fontFamily = FontFamily.SansSerif,
                  color = pillText,
                  fontSize = 12.5.sp,
                  fontWeight = FontWeight.Medium,
                )
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            DetailPairRow(
              leftLabel = "Source",
              leftValue = ticket.sourceStation,
              rightLabel = "Destination",
              rightValue = ticket.destinationStation,
            )

            Spacer(modifier = Modifier.height(16.dp))

            DetailPairRow(
              leftLabel = "Distance",
              leftValue = if (ticket.distanceKm > 0) "${ticket.distanceKm} km" else "Platform",
              rightLabel = "Passenger",
              rightValue = "${ticket.adults} Adult, ${ticket.children} Child",
            )

            Spacer(modifier = Modifier.height(16.dp))

            DetailPairRow(
              leftLabel = "Ticket Type",
              leftValue = ticket.ticketType,
              rightLabel = "Train Types",
              rightValue = ticket.trainType,
            )

            Spacer(modifier = Modifier.height(16.dp))

            DetailPairRow(
              leftLabel = "Class",
              leftValue = ticket.travelClass,
              rightLabel = "Fare",
              rightValue = StationCatalog.formatRupees(ticket.fareAmount),
            )

            Spacer(modifier = Modifier.height(13.dp))

            // Via pill box
            Row(
              modifier =
                Modifier.fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .border(1.dp, DividerLight, RoundedCornerShape(10.dp))
                  .padding(horizontal = 12.dp, vertical = 9.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                imageVector = Icons.Outlined.AltRoute,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(18.dp),
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text =
                  buildAnnotatedString {
                    withStyle(
                      SpanStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191C20),
                      )
                    ) {
                      append("Via: ")
                    }
                    withStyle(
                      SpanStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF191C20),
                      )
                    ) {
                      append(ticket.viaRoute)
                    }
                  },
                fontSize = 14.sp,
              )
            }

            Spacer(modifier = Modifier.height(11.dp))

            Text(
              text = ticket.irCode,
              fontFamily = MontserratFamily,
              color = Color(0xFF191C20),
              fontSize = 14.sp,
              fontWeight = FontWeight.Medium,
            )
          }

          // Validity footnote + bottom cyan cap below dashed tear line (68.dp total: 54.5.dp + 13.5.dp)
          Box(
            modifier =
              Modifier.fillMaxWidth()
                .height(54.5.dp)
                .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 8.dp),
            contentAlignment = Alignment.CenterStart,
          ) {
            Text(
              text = "*Valid for start of journey within 3 hour or until departure of the first train.",
              fontFamily = MontserratFamily,
              color = Color(0xFF23272E),
              fontSize = 12.sp,
              fontWeight = FontWeight.Normal,
              lineHeight = 16.sp,
            )
          }
          Box(modifier = Modifier.fillMaxWidth().height(13.5.dp).background(CyanTicketEdge))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pink warning box
        Box(
          modifier =
            Modifier.fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(WarningNoteBg)
              .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
          Text(
            text =
              "Note: This ticket is non refundable. Ticket is stored locally on the device. Please do not change your handset or perform factory reset.",
            color = WarningNoteText,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Outlined Book Connecting Journey button
        OutlinedButton(
          onClick = { onBookConnectingJourney(ticket) },
          shape = RoundedCornerShape(50),
          border = BorderStroke(1.2.dp, RailBlue),
          colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
          modifier = Modifier.fillMaxWidth().height(50.dp).testTag("book_connecting_button"),
        ) {
          Text(
            text = "Book Connecting Journey",
            color = RailBlue,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
          )
        }
      }

      // Light-gray QR Code band
      Box(
        modifier =
          Modifier.fillMaxWidth()
            .background(QrContainerBg)
            .padding(vertical = 26.dp),
        contentAlignment = Alignment.Center,
      ) {
        val qrPayload =
          "${ticket.utsNumber}|${ticket.sourceStation}|${ticket.destinationStation}|" +
            "${ticket.distanceKm}km|${ticket.fareAmount}|${ticket.bookingDateTimeFull}|${ticket.irCode}"
        DeterministicQrCode(
          payload = qrPayload,
          modifier = Modifier.size(204.dp).testTag("ticket_qr_code"),
        )
      }

      // Bottom "Do you know?" card
      Surface(color = Color.White, modifier = Modifier.fillMaxWidth()) {
        Column(
          modifier =
            Modifier.fillMaxWidth()
              .widthIn(max = 600.dp)
              .padding(horizontal = 18.dp, vertical = 22.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          Text(
            text = "Do you know?",
            color = Color(0xFF111111),
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
          )
          Text(
            text = "IR recovers only 57% of cost of travel on an average.",
            color = Color(0xFF2B2F36),
            fontSize = 15.sp,
            lineHeight = 22.sp,
          )
          Text(
            text =
              "This ticket is booked on a personal user ID. It’s sale/purchase is an offence u/s 143 of the Railways Act, 1989",
            color = Color(0xFF2B2F36),
            fontSize = 15.sp,
            lineHeight = 22.sp,
          )
          Text(
            text = "For enquiry and integrated railway helpline, please dial 139.",
            color = Color(0xFF2B2F36),
            fontSize = 15.sp,
            lineHeight = 22.sp,
          )
        }
      }
    }
  }
}

@Composable
private fun DetailPairRow(
  leftLabel: String,
  leftValue: String,
  rightLabel: String,
  rightValue: String,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.Top,
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = leftLabel,
        fontFamily = FontFamily.SansSerif,
        color = Color(0xFF7B7B7B),
        fontSize = 13.5.sp,
        fontWeight = FontWeight.Medium,
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = leftValue,
        fontFamily = FontFamily.SansSerif,
        color = Color(0xFF222222),
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
      )
    }
    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
      Text(
        text = rightLabel,
        fontFamily = FontFamily.SansSerif,
        color = Color(0xFF7B7B7B),
        fontSize = 13.5.sp,
        fontWeight = FontWeight.Medium,
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = rightValue,
        fontFamily = FontFamily.SansSerif,
        color = Color(0xFF222222),
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.End,
      )
    }
  }
}
