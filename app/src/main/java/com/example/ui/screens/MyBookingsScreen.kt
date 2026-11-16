package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TicketEntity
import com.example.ui.components.BookingTicketCard
import com.example.ui.theme.CancelledRed
import com.example.ui.theme.CompletedGreen
import com.example.ui.theme.FilterItemUnselectedBg
import com.example.ui.theme.FilterTrayBg
import com.example.ui.theme.RailBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UpcomingOrange
import com.example.ui.viewmodel.BookingFilter
import com.example.ui.viewmodel.BookingsUiState

@Composable
fun MyBookingsScreen(
  uiState: BookingsUiState,
  onBack: () -> Unit,
  onSelectFilter: (BookingFilter) -> Unit,
  onToggleSort: () -> Unit,
  onRefresh: () -> Unit,
  onBookAgain: (TicketEntity) -> Unit,
  onViewDetails: (Long) -> Unit,
  onEditTicket: (Long) -> Unit,
  onBookNewTicket: () -> Unit,
) {
  Scaffold(
    containerColor = Color.White,
    topBar = {
      Surface(color = RailBlue, modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier =
            Modifier.fillMaxWidth()
              .windowInsetsPadding(WindowInsets.statusBars)
              .padding(horizontal = 16.dp, vertical = 14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              onClick = onBack,
              shape = CircleShape,
              color = Color.Transparent,
              border = BorderStroke(1.5.dp, Color.White),
              modifier = Modifier.size(40.dp).testTag("bookings_back_button"),
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
            Spacer(modifier = Modifier.width(16.dp))
            Text(
              text = "My Bookings",
              color = Color.White,
              fontSize = 20.sp,
              fontWeight = FontWeight.SemiBold,
            )
          }

          IconButton(
            onClick = onToggleSort,
            modifier = Modifier.size(48.dp).testTag("bookings_sort_button"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Sort,
              contentDescription = "Sort bookings",
              tint = Color.White,
              modifier = Modifier.size(24.dp),
            )
          }
        }
      }
    },
    bottomBar = {
      BookingsFilterTray(
        selectedFilter = uiState.selectedFilter,
        onSelectFilter = onSelectFilter,
      )
    },
  ) { innerPadding ->
    Box(
      modifier = Modifier.fillMaxSize().padding(innerPadding),
      contentAlignment = Alignment.TopCenter,
    ) {
      LazyColumn(
        modifier = Modifier.fillMaxSize().widthIn(max = 600.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        when (uiState.selectedFilter) {
          BookingFilter.ALL -> {
            item(key = "header_upcoming") {
              SectionHeaderWithRefresh(
                title = "Upcoming (${uiState.upcomingTickets.size})",
                color = UpcomingOrange,
                showRefresh = true,
                onRefresh = onRefresh,
              )
            }
            if (uiState.upcomingTickets.isEmpty()) {
              item(key = "empty_upcoming") {
                EmptyFilterHint(
                  text = "No upcoming tickets.",
                  onBookClick = onBookNewTicket,
                )
              }
            } else {
              items(uiState.upcomingTickets, key = { "u_${it.id}" }) { ticket ->
                BookingTicketCard(
                  ticket = ticket,
                  onBookAgain = { onBookAgain(ticket) },
                  onViewDetails = { onViewDetails(ticket.id) },
                  onEditClick = { onEditTicket(ticket.id) },
                )
              }
            }

            item(key = "header_completed") {
              Spacer(modifier = Modifier.height(6.dp))
              SectionHeaderWithRefresh(
                title = "Completed (${uiState.completedTickets.size})",
                color = CompletedGreen,
                showRefresh = false,
                onRefresh = onRefresh,
              )
            }
            if (uiState.completedTickets.isEmpty()) {
              item(key = "empty_completed") {
                Text(
                  text = "No completed journeys yet.",
                  color = TextSecondary,
                  fontSize = 13.sp,
                  modifier = Modifier.padding(vertical = 8.dp),
                )
              }
            } else {
              items(uiState.completedTickets, key = { "c_${it.id}" }) { ticket ->
                BookingTicketCard(
                  ticket = ticket,
                  onBookAgain = { onBookAgain(ticket) },
                  onViewDetails = { onViewDetails(ticket.id) },
                  onEditClick = { onEditTicket(ticket.id) },
                )
              }
            }

            if (uiState.cancelledTickets.isNotEmpty()) {
              item(key = "header_cancelled") {
                Spacer(modifier = Modifier.height(6.dp))
                SectionHeaderWithRefresh(
                  title = "Cancelled (${uiState.cancelledTickets.size})",
                  color = CancelledRed,
                  showRefresh = false,
                  onRefresh = onRefresh,
                )
              }
              items(uiState.cancelledTickets, key = { "x_${it.id}" }) { ticket ->
                BookingTicketCard(
                  ticket = ticket,
                  onBookAgain = { onBookAgain(ticket) },
                  onViewDetails = { onViewDetails(ticket.id) },
                  onEditClick = { onEditTicket(ticket.id) },
                )
              }
            }
          }
          BookingFilter.UPCOMING -> {
            item(key = "only_upcoming_header") {
              SectionHeaderWithRefresh(
                title = "Upcoming (${uiState.upcomingTickets.size})",
                color = UpcomingOrange,
                showRefresh = true,
                onRefresh = onRefresh,
              )
            }
            if (uiState.upcomingTickets.isEmpty()) {
              item(key = "only_upcoming_empty") {
                EmptyFilterHint(
                  text = "No upcoming tickets scheduled.",
                  onBookClick = onBookNewTicket,
                )
              }
            } else {
              items(uiState.upcomingTickets, key = { "ou_${it.id}" }) { ticket ->
                BookingTicketCard(
                  ticket = ticket,
                  onBookAgain = { onBookAgain(ticket) },
                  onViewDetails = { onViewDetails(ticket.id) },
                  onEditClick = { onEditTicket(ticket.id) },
                )
              }
            }
          }
          BookingFilter.COMPLETED -> {
            item(key = "only_completed_header") {
              SectionHeaderWithRefresh(
                title = "Completed (${uiState.completedTickets.size})",
                color = CompletedGreen,
                showRefresh = true,
                onRefresh = onRefresh,
              )
            }
            if (uiState.completedTickets.isEmpty()) {
              item(key = "only_completed_empty") {
                EmptyFilterHint(
                  text = "No completed journeys found.",
                  onBookClick = onBookNewTicket,
                )
              }
            } else {
              items(uiState.completedTickets, key = { "oc_${it.id}" }) { ticket ->
                BookingTicketCard(
                  ticket = ticket,
                  onBookAgain = { onBookAgain(ticket) },
                  onViewDetails = { onViewDetails(ticket.id) },
                  onEditClick = { onEditTicket(ticket.id) },
                )
              }
            }
          }
          BookingFilter.CANCELLED -> {
            item(key = "only_cancelled_header") {
              SectionHeaderWithRefresh(
                title = "Cancelled (${uiState.cancelledTickets.size})",
                color = CancelledRed,
                showRefresh = true,
                onRefresh = onRefresh,
              )
            }
            if (uiState.cancelledTickets.isEmpty()) {
              item(key = "only_cancelled_empty") {
                EmptyFilterHint(
                  text = "No cancelled tickets.",
                  onBookClick = onBookNewTicket,
                )
              }
            } else {
              items(uiState.cancelledTickets, key = { "ox_${it.id}" }) { ticket ->
                BookingTicketCard(
                  ticket = ticket,
                  onBookAgain = { onBookAgain(ticket) },
                  onViewDetails = { onViewDetails(ticket.id) },
                  onEditClick = { onEditTicket(ticket.id) },
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SectionHeaderWithRefresh(
  title: String,
  color: Color,
  showRefresh: Boolean,
  onRefresh: () -> Unit,
) {
  Box(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
    Text(
      text = title,
      color = color,
      fontSize = 16.sp,
      fontWeight = FontWeight.SemiBold,
      modifier = Modifier.align(Alignment.Center),
    )
    if (showRefresh) {
      IconButton(
        onClick = onRefresh,
        modifier = Modifier.align(Alignment.CenterEnd).size(36.dp).testTag("bookings_refresh_button"),
      ) {
        Icon(
          imageVector = Icons.Outlined.Refresh,
          contentDescription = "Refresh bookings",
          tint = Color(0xFF707580),
          modifier = Modifier.size(20.dp),
        )
      }
    }
  }
}

@Composable
private fun EmptyFilterHint(text: String, onBookClick: () -> Unit) {
  Surface(
    color = Color(0xFFF6F8FC),
    shape = RoundedCornerShape(14.dp),
    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
  ) {
    Column(
      modifier = Modifier.padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Text(text = text, color = TextSecondary, fontSize = 14.sp)
      Button(
        onClick = onBookClick,
        colors = ButtonDefaults.buttonColors(containerColor = RailBlue),
        shape = RoundedCornerShape(50),
      ) {
        Text("Book New Ticket")
      }
    }
  }
}

@Composable
private fun BookingsFilterTray(
  selectedFilter: BookingFilter,
  onSelectFilter: (BookingFilter) -> Unit,
) {
  Surface(
    color = FilterTrayBg,
    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
    shadowElevation = 6.dp,
    modifier = Modifier.fillMaxWidth(),
  ) {
    Row(
      modifier =
        Modifier.fillMaxWidth()
          .windowInsetsPadding(WindowInsets.navigationBars)
          .padding(horizontal = 12.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      BookingFilter.entries.forEach { filter ->
        val isSelected = filter == selectedFilter
        val activeColor =
          when (filter) {
            BookingFilter.UPCOMING -> UpcomingOrange
            BookingFilter.COMPLETED -> CompletedGreen
            BookingFilter.CANCELLED -> CancelledRed
            BookingFilter.ALL -> TextPrimary
          }
        val iconTint =
          when {
            !isSelected -> Color(0xFF7D8594)
            filter == BookingFilter.ALL -> RailBlue
            else -> activeColor
          }
        val textColor = if (isSelected) activeColor else Color(0xFF7D8594)

        Surface(
          color = if (isSelected) Color.White else FilterItemUnselectedBg,
          shape = RoundedCornerShape(12.dp),
          shadowElevation = if (isSelected) 2.dp else 0.dp,
          modifier =
            Modifier.weight(1f)
              .height(64.dp)
              .clip(RoundedCornerShape(12.dp))
              .clickable { onSelectFilter(filter) }
              .testTag("filter_tab_${filter.name.lowercase()}"),
        ) {
          Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
          ) {
            Icon(
              imageVector =
                if (isSelected) Icons.Filled.ConfirmationNumber
                else Icons.Outlined.ConfirmationNumber,
              contentDescription = filter.label,
              tint = iconTint,
              modifier = Modifier.size(22.dp),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = filter.label,
              color = textColor,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              maxLines = 1,
            )
          }
        }
      }
    }
  }
}
