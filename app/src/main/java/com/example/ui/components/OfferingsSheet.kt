package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StationCatalog
import com.example.data.TicketEntity
import com.example.data.TicketStatus
import com.example.ui.theme.CompletedGreen
import com.example.ui.theme.DividerLight
import com.example.ui.theme.PageBackground
import com.example.ui.theme.RailBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class OfferingType(val title: String) {
  SEARCH_TRAINS("Search Trains"),
  PNR_STATUS("PNR / UTS Status"),
  COACH_POSITION("Coach Position"),
  TRACK_TRAIN("Track Your Train"),
  ORDER_FOOD("Order Food (e-Catering)"),
  FILE_REFUND("File Refund / Cancel"),
  RAIL_MADAD("Rail Madad Helpline"),
  WAVES("RailOne WAVES Info"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfferingModalSheet(
  offering: OfferingType,
  tickets: List<TicketEntity>,
  onDismiss: () -> Unit,
  onBookRoute: (String, String) -> Unit,
  onCancelTicket: (TicketEntity) -> Unit,
  onOpenTicketDetails: (Long) -> Unit,
) {
  ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor = Color.White,
  ) {
    Column(
      modifier =
        Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 28.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Text(
        text = offering.title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
      )

      when (offering) {
        OfferingType.SEARCH_TRAINS -> {
          var query by remember { mutableStateOf("") }
          val matches =
            remember(query) {
              val q = query.trim().uppercase()
              if (q.isEmpty()) StationCatalog.stations.take(6)
              else
                StationCatalog.stations.filter {
                  it.displayUpper.contains(q) || it.code.contains(q)
                }
            }
          OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search station (e.g. Wardha, Nagpur)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
          )
          matches.forEach { st ->
            Surface(
              color = PageBackground,
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Column {
                  Text(st.displayUpper, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                  Text("Station Code: ${st.code}", fontSize = 12.sp, color = TextSecondary)
                }
                OutlinedButton(
                  onClick = {
                    val dest =
                      if (st.displayUpper == "WARDHA JN.") "PULGAON JN." else "WARDHA JN."
                    onDismiss()
                    onBookRoute(st.displayUpper, dest)
                  }
                ) {
                  Text("Book From Here", fontSize = 12.sp)
                }
              }
            }
          }
        }
        OfferingType.PNR_STATUS -> {
          var codeQuery by remember { mutableStateOf("") }
          OutlinedTextField(
            value = codeQuery,
            onValueChange = { codeQuery = it.uppercase() },
            label = { Text("Enter UTS / Ticket Number") },
            placeholder = { Text("e.g. XYGDEHJ01C") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
          )
          val found =
            tickets.find {
              it.utsNumber.equals(codeQuery.trim(), ignoreCase = true)
            } ?: tickets.firstOrNull()
          if (found != null) {
            Surface(
              color = PageBackground,
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Text(
                  text = "UTS ${found.utsNumber} • ${found.status}",
                  fontWeight = FontWeight.Bold,
                  color = CompletedGreen,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("${found.sourceStation} → ${found.destinationStation}", color = TextPrimary)
                Text(
                  "${found.bookingDateTimeFull} • ${StationCatalog.formatRupees(found.fareAmount)}",
                  fontSize = 12.sp,
                  color = TextSecondary,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                  onClick = {
                    onDismiss()
                    onOpenTicketDetails(found.id)
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = RailBlue),
                ) {
                  Text("Open Ticket")
                }
              }
            }
          }
        }
        OfferingType.FILE_REFUND -> {
          val upcoming = tickets.filter { it.status == TicketStatus.UPCOMING.name }
          Text(
            text = "Unreserved UTS tickets are ordinarily non-refundable, or you can mark an upcoming journey as cancelled locally.",
            fontSize = 13.sp,
            color = TextSecondary,
          )
          if (upcoming.isEmpty()) {
            Text("No upcoming tickets available to cancel.", color = TextSecondary)
          } else {
            upcoming.forEach { t ->
              Surface(
                color = PageBackground,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth().padding(12.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text("${t.sourceStation} → ${t.destinationStation}", fontWeight = FontWeight.SemiBold)
                    Text("UTS: ${t.utsNumber} • ${t.bookingDateLabel}", fontSize = 12.sp, color = TextSecondary)
                  }
                  OutlinedButton(
                    onClick = {
                      onCancelTicket(t)
                      onDismiss()
                    },
                    modifier = Modifier.testTag("cancel_ticket_button_${t.utsNumber}"),
                  ) {
                    Text("Cancel", color = Color(0xFFD32F2F))
                  }
                }
              }
            }
          }
        }
        OfferingType.COACH_POSITION -> {
          Text(
            text = "Typical Central Railway Express Formation",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
          )
          Surface(color = PageBackground, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Text(
              text = "ENG → SLR → GEN → GEN → S1 → S2 → S3 → B1 → B2 → A1 → GEN → SLR",
              modifier = Modifier.padding(14.dp),
              fontWeight = FontWeight.Medium,
              color = RailBlue,
            )
          }
          Text(
            text = "Unreserved General (GEN) coaches are positioned right behind the engine and at the rear.",
            fontSize = 13.sp,
            color = TextSecondary,
          )
        }
        OfferingType.TRACK_TRAIN -> {
          Text(
            text = "Central Railway Corridor Status (Wardha – Pulgaon – Nagpur)",
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
          )
          listOf(
            "11121 Bhusaval–Wardha MEMU • On Time (PF 2)",
            "12119 Amravati–Ajni Intercity • Running 8m late",
            "01371 Wardha–Ballarshah Passenger • Departing in 22m",
          ).forEach { line ->
            Surface(color = PageBackground, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
              Text(line, modifier = Modifier.padding(12.dp), fontSize = 13.sp, color = TextPrimary)
            }
          }
        }
        OfferingType.ORDER_FOOD -> {
          Text(
            text = "IRCTC e-Catering is available at Wardha Jn. (WR), Nagpur (NGP), and Badnera Jn. (BD) for reserved PNR journeys.",
            fontSize = 14.sp,
            color = TextSecondary,
          )
          HorizontalDivider(color = DividerLight)
          Text(
            text = "Station Refreshment Stalls: PF 1 & PF 2 open 24×7.",
            fontSize = 13.sp,
            color = TextPrimary,
          )
        }
        OfferingType.RAIL_MADAD -> {
          Text(
            text = "Integrated Indian Railways Helpline: 139",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = RailBlue,
          )
          Text(
            text = "Available 24×7 for medical assistance, security, cleanliness, and ticket enquiries across all stations and trains.",
            fontSize = 13.sp,
            color = TextSecondary,
          )
        }
        OfferingType.WAVES -> {
          Text(
            text = "RailOne WAVES Entertainment & Wi-Fi Portal",
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
          )
          Text(
            text = "Connect to RailWire station Wi-Fi at major junctions (Wardha Jn., Nagpur, Akola Jn.) for regional news, audiobooks, and journey updates.",
            fontSize = 13.sp,
            color = TextSecondary,
          )
        }
      }

      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(containerColor = RailBlue),
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(50),
      ) {
        Text("Close")
      }
    }
  }
}
