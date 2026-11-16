package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalActivity
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Train
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StationCatalog
import com.example.data.TicketCategory
import com.example.data.TicketEntity
import com.example.data.TicketStatus
import com.example.ui.components.BottomTab
import com.example.ui.components.RailBottomBar
import com.example.ui.theme.CompletedGreen
import com.example.ui.theme.PageBackground
import com.example.ui.theme.RailBlue
import com.example.ui.theme.RailNavy
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UpcomingOrange
import com.example.ui.viewmodel.UserProfile

@Composable
fun YouScreen(
  profile: UserProfile,
  tickets: List<TicketEntity>,
  onSaveProfile: (String, String) -> Unit,
  onOpenBookings: () -> Unit,
  onNavigateTab: (BottomTab) -> Unit,
) {
  var nameInput by remember(profile.name) { mutableStateOf(profile.name) }
  var mobileInput by remember(profile.mobileNumber) { mutableStateOf(profile.mobileNumber) }

  val upcomingCount = tickets.count { it.status == TicketStatus.UPCOMING.name }
  val completedCount = tickets.count { it.status == TicketStatus.COMPLETED.name }
  val totalSpent = tickets.sumOf { it.fareAmount }

  Scaffold(
    containerColor = PageBackground,
    topBar = {
      Surface(color = RailBlue, modifier = Modifier.fillMaxWidth()) {
        Box(
          modifier =
            Modifier.fillMaxWidth()
              .windowInsetsPadding(WindowInsets.statusBars)
              .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
          Text(
            text = "Passenger Profile",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
          )
        }
      }
    },
    bottomBar = { RailBottomBar(selectedTab = BottomTab.YOU, onSelectTab = onNavigateTab) },
  ) { innerPadding ->
    Column(
      modifier =
        Modifier.fillMaxSize()
          .padding(innerPadding)
          .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().widthIn(max = 600.dp).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        Surface(color = Color.White, shape = RoundedCornerShape(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Box(
              modifier = Modifier.size(56.dp).clip(CircleShape).background(Color(0xFFE8F1FC)),
              contentAlignment = Alignment.Center,
            ) {
              Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Passenger avatar",
                tint = RailBlue,
                modifier = Modifier.size(30.dp),
              )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
              Text(profile.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
              Text("Mobile: ${profile.mobileNumber}", fontSize = 13.sp, color = TextSecondary)
            }
          }
        }

        // Travel summary stats
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          StatPill("Upcoming", upcomingCount.toString(), UpcomingOrange, Modifier.weight(1f))
          StatPill("Completed", completedCount.toString(), CompletedGreen, Modifier.weight(1f))
          StatPill("Total Fare", StationCatalog.formatRupees(totalSpent), RailBlue, Modifier.weight(1f))
        }

        Surface(color = Color.White, shape = RoundedCornerShape(16.dp)) {
          Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Text(
              text = "Edit Default Passenger Details",
              color = RailNavy,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
            )
            OutlinedTextField(
              value = nameInput,
              onValueChange = { nameInput = it },
              label = { Text("Passenger Name") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("profile_name_input"),
            )
            OutlinedTextField(
              value = mobileInput,
              onValueChange = { mobileInput = it.filter(Char::isDigit).take(10) },
              label = { Text("Mobile Number") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("profile_mobile_input"),
            )
            Button(
              onClick = { onSaveProfile(nameInput, mobileInput) },
              colors = ButtonDefaults.buttonColors(containerColor = RailBlue),
              shape = RoundedCornerShape(50),
              modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_profile_button"),
            ) {
              Text("Save Profile")
            }
          }
        }

        Button(
          onClick = onOpenBookings,
          colors = ButtonDefaults.buttonColors(containerColor = RailNavy),
          shape = RoundedCornerShape(50),
          modifier = Modifier.fillMaxWidth().height(48.dp),
        ) {
          Text("View All My Bookings")
        }
      }
    }
  }
}

@Composable
private fun StatPill(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
  Surface(color = Color.White, shape = RoundedCornerShape(14.dp), modifier = modifier) {
    Column(
      modifier = Modifier.padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(text = value, color = color, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = label, color = TextSecondary, fontSize = 11.sp)
    }
  }
}

@Composable
fun MenuScreen(
  onBookCategory: (TicketCategory) -> Unit,
  onOpenBookings: () -> Unit,
  onNavigateTab: (BottomTab) -> Unit,
) {
  Scaffold(
    containerColor = PageBackground,
    topBar = {
      Surface(color = RailBlue, modifier = Modifier.fillMaxWidth()) {
        Box(
          modifier =
            Modifier.fillMaxWidth()
              .windowInsetsPadding(WindowInsets.statusBars)
              .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
          Text(
            text = "RailOne Menu",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
          )
        }
      }
    },
    bottomBar = { RailBottomBar(selectedTab = BottomTab.MENU, onSelectTab = onNavigateTab) },
  ) { innerPadding ->
    Column(
      modifier =
        Modifier.fillMaxSize()
          .padding(innerPadding)
          .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().widthIn(max = 600.dp).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        MenuActionCard(
          icon = Icons.Outlined.LocalActivity,
          title = "Book Unreserved Ticket",
          subtitle = "Instant UTS journey & return tickets",
          onClick = { onBookCategory(TicketCategory.UNRESERVED) },
        )
        MenuActionCard(
          icon = Icons.Outlined.Train,
          title = "Book Reserved Ticket",
          subtitle = "Sleeper, First Class & AC Chair bookings",
          onClick = { onBookCategory(TicketCategory.RESERVED) },
        )
        MenuActionCard(
          icon = Icons.Filled.ConfirmationNumber,
          title = "Book Platform Ticket",
          subtitle = "Quick station platform entry pass",
          onClick = { onBookCategory(TicketCategory.PLATFORM) },
        )
        MenuActionCard(
          icon = Icons.Outlined.Info,
          title = "My Bookings & Edit Tickets",
          subtitle = "Manage upcoming, completed & cancelled journeys",
          onClick = onOpenBookings,
        )
        Surface(color = Color.White, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(Icons.Outlined.Phone, contentDescription = null, tint = RailBlue)
            Spacer(modifier = Modifier.width(14.dp))
            Column {
              Text("Railway Helpline 139", fontWeight = FontWeight.Bold, color = TextPrimary)
              Text(
                "Tickets are stored locally on your device for instant offline inspection.",
                fontSize = 12.sp,
                color = TextSecondary,
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun MenuActionCard(
  icon: ImageVector,
  title: String,
  subtitle: String,
  onClick: () -> Unit,
) {
  Surface(
    color = Color.White,
    shape = RoundedCornerShape(14.dp),
    modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
        Icon(icon, contentDescription = title, tint = RailBlue)
        Spacer(modifier = Modifier.width(14.dp))
        Column {
          Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary)
          Text(subtitle, fontSize = 12.sp, color = TextSecondary)
        }
      }
      Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = null,
        tint = TextSecondary,
      )
    }
  }
}
