package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.outlined.AltRoute
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Train
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.TicketCategory
import com.example.data.TicketEntity
import com.example.data.TicketStatus
import com.example.ui.components.BottomTab
import com.example.ui.components.HomeUpcomingTicketCard
import com.example.ui.components.OfferingModalSheet
import com.example.ui.components.OfferingType
import com.example.ui.components.RailBottomBar
import com.example.ui.theme.PageBackground
import com.example.ui.theme.RailBlue
import com.example.ui.theme.RailNavy
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TileBlueBg
import com.example.ui.theme.TileBlueIcon
import com.example.ui.theme.TileCoralBg
import com.example.ui.theme.TileCoralIcon
import com.example.ui.theme.TileCreamBg
import com.example.ui.theme.TileCreamIcon
import com.example.ui.theme.TileGrayBg
import com.example.ui.theme.TileGrayIcon
import com.example.ui.theme.TileGreenBg
import com.example.ui.theme.TileGreenIcon
import com.example.ui.theme.TileLavenderBg
import com.example.ui.theme.TileLavenderIcon
import com.example.ui.theme.TilePinkBg
import com.example.ui.theme.TilePinkIcon
import com.example.ui.theme.TileSlateBg
import com.example.ui.theme.UpcomingPurpleEnd
import com.example.ui.theme.UpcomingPurpleStart
import com.example.ui.viewmodel.UserProfile

@Composable
fun HomeScreen(
  profile: UserProfile,
  tickets: List<TicketEntity>,
  onToggleLanguage: () -> Unit,
  onSelectCategory: (TicketCategory) -> Unit,
  onBookAgain: (TicketEntity) -> Unit,
  onViewDetails: (Long) -> Unit,
  onBookRoute: (String, String) -> Unit,
  onCancelTicket: (TicketEntity) -> Unit,
  onNavigateTab: (BottomTab) -> Unit,
) {
  var activeOffering by remember { mutableStateOf<OfferingType?>(null) }
  var showNotificationsDialog by remember { mutableStateOf(false) }

  val upcomingTicket =
    remember(tickets) {
      tickets.firstOrNull { it.status == TicketStatus.UPCOMING.name } ?: tickets.firstOrNull()
    }

  Scaffold(
    containerColor = PageBackground,
    bottomBar = { RailBottomBar(selectedTab = BottomTab.HOME, onSelectTab = onNavigateTab) },
  ) { innerPadding ->
    Column(
      modifier =
        Modifier.fillMaxSize()
          .padding(bottom = innerPadding.calculateBottomPadding())
          .windowInsetsPadding(WindowInsets.statusBars)
          .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Column(
        modifier =
          Modifier.fillMaxWidth().widthIn(max = 600.dp).padding(horizontal = 18.dp, vertical = 10.dp)
      ) {
        // Top Header: Language button | RailOne wordmark | Notification bell
        Row(
          modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Surface(
            onClick = onToggleLanguage,
            shape = CircleShape,
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE3E7EE)),
            modifier = Modifier.size(48.dp).testTag("home_language_button"),
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Outlined.Translate,
                contentDescription = "Switch language",
                tint = RailBlue,
                modifier = Modifier.size(22.dp),
              )
            }
          }

          Text(
            text =
              buildAnnotatedString {
                withStyle(SpanStyle(color = Color(0xFF2B303A), fontWeight = FontWeight.SemiBold)) {
                  append("Rail")
                }
                withStyle(SpanStyle(color = Color(0xFF646C7A), fontWeight = FontWeight.Normal)) {
                  append("One")
                }
              },
            fontSize = 26.sp,
            letterSpacing = (-0.4).sp,
          )

          Surface(
            onClick = { showNotificationsDialog = true },
            shape = CircleShape,
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE3E7EE)),
            modifier = Modifier.size(48.dp).testTag("home_notifications_button"),
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifications",
                tint = Color(0xFF424752),
                modifier = Modifier.size(22.dp),
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Personalized Greeting
        Text(
          text =
            if (profile.isHindi) "नमस्ते, ${profile.name}!" else "Hi, ${profile.name}!",
          color = RailNavy,
          fontSize = 16.sp,
          fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Journey Planner
        Text(
          text = if (profile.isHindi) "यात्रा योजनाकार" else "Journey Planner",
          color = RailNavy,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          PlannerCard(
            title = "Reserved",
            drawableRes = R.drawable.img_planner_reserved,
            onClick = { onSelectCategory(TicketCategory.RESERVED) },
            testTag = "planner_reserved_card",
            modifier = Modifier.weight(1f),
          )
          PlannerCard(
            title = "Unreserved",
            drawableRes = R.drawable.img_planner_unreserved,
            onClick = { onSelectCategory(TicketCategory.UNRESERVED) },
            testTag = "planner_unreserved_card",
            modifier = Modifier.weight(1f),
          )
          PlannerCard(
            title = "Platform",
            drawableRes = R.drawable.img_planner_platform,
            onClick = { onSelectCategory(TicketCategory.PLATFORM) },
            testTag = "planner_platform_card",
            modifier = Modifier.weight(1f),
          )
        }

        Spacer(modifier = Modifier.height(22.dp))

        // More Offerings
        Text(
          text = if (profile.isHindi) "अधिक सेवाएं" else "More Offerings",
          color = RailNavy,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          OfferingTile(
            label = "Search\nTrains",
            bgColor = TilePinkBg,
            iconColor = TilePinkIcon,
            icon = Icons.Outlined.AltRoute,
            onClick = { activeOffering = OfferingType.SEARCH_TRAINS },
            testTag = "offering_search_trains",
            modifier = Modifier.weight(1f),
          )
          OfferingTile(
            label = "PNR\nStatus",
            bgColor = TileGreenBg,
            iconColor = TileGreenIcon,
            icon = Icons.Filled.ConfirmationNumber,
            onClick = { activeOffering = OfferingType.PNR_STATUS },
            testTag = "offering_pnr_status",
            modifier = Modifier.weight(1f),
          )
          OfferingTile(
            label = "Coach\nPosition",
            bgColor = TileBlueBg,
            iconColor = TileBlueIcon,
            icon = Icons.Filled.DirectionsTransit,
            onClick = { activeOffering = OfferingType.COACH_POSITION },
            testTag = "offering_coach_position",
            modifier = Modifier.weight(1f),
          )
          OfferingTile(
            label = "Track Your\nTrain",
            bgColor = TileCreamBg,
            iconColor = TileCreamIcon,
            icon = Icons.Outlined.Train,
            onClick = { activeOffering = OfferingType.TRACK_TRAIN },
            testTag = "offering_track_train",
            modifier = Modifier.weight(1f),
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          OfferingTile(
            label = "Order\nFood",
            bgColor = TileLavenderBg,
            iconColor = TileLavenderIcon,
            icon = Icons.Filled.Fastfood,
            onClick = { activeOffering = OfferingType.ORDER_FOOD },
            testTag = "offering_order_food",
            modifier = Modifier.weight(1f),
          )
          OfferingTile(
            label = "File\nRefund",
            bgColor = TileGrayBg,
            iconColor = TileGrayIcon,
            icon = Icons.Outlined.ConfirmationNumber,
            onClick = { activeOffering = OfferingType.FILE_REFUND },
            testTag = "offering_file_refund",
            modifier = Modifier.weight(1f),
          )
          OfferingTile(
            label = "Rail\nMadad",
            bgColor = TileCoralBg,
            iconColor = TileCoralIcon,
            icon = Icons.Filled.Handshake,
            onClick = { activeOffering = OfferingType.RAIL_MADAD },
            testTag = "offering_rail_madad",
            modifier = Modifier.weight(1f),
          )
          WavesOfferingTile(
            onClick = { activeOffering = OfferingType.WAVES },
            modifier = Modifier.weight(1f),
          )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Upcoming Journey
        Text(
          text = if (profile.isHindi) "आगामी यात्रा" else "Upcoming Journey",
          color = RailNavy,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (upcomingTicket != null) {
          HomeUpcomingTicketCard(
            ticket = upcomingTicket,
            onBookAgain = { onBookAgain(upcomingTicket) },
            onViewDetails = { onViewDetails(upcomingTicket.id) },
          )
        } else {
          Box(
            modifier =
              Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(
                  Brush.horizontalGradient(listOf(UpcomingPurpleStart, UpcomingPurpleEnd))
                )
                .padding(20.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Text(
                text = "No upcoming journeys yet",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
              )
              Text(
                text = "Book an unreserved, reserved, or platform ticket in seconds.",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
              )
              Button(
                onClick = { onSelectCategory(TicketCategory.UNRESERVED) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(50),
              ) {
                Text("Book Unreserved Ticket", color = UpcomingPurpleStart)
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))
      }
    }
  }

  activeOffering?.let { offering ->
    OfferingModalSheet(
      offering = offering,
      tickets = tickets,
      onDismiss = { activeOffering = null },
      onBookRoute = onBookRoute,
      onCancelTicket = onCancelTicket,
      onOpenTicketDetails = onViewDetails,
    )
  }

  if (showNotificationsDialog) {
    AlertDialog(
      onDismissRequest = { showNotificationsDialog = false },
      title = { Text("RailOne Alerts", fontWeight = FontWeight.Bold) },
      text = {
        val activeCount = tickets.count { it.status == TicketStatus.UPCOMING.name }
        Text(
          "You have $activeCount upcoming journey ticket(s) stored safely on this device. Always carry valid ID proof for reserved travel."
        )
      },
      confirmButton = {
        TextButton(onClick = { showNotificationsDialog = false }) { Text("OK") }
      },
    )
  }
}

@Composable
private fun PlannerCard(
  title: String,
  drawableRes: Int,
  onClick: () -> Unit,
  testTag: String,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.clickable(onClick = onClick).testTag(testTag),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Image(
      painter = painterResource(id = drawableRes),
      contentDescription = "$title ticket booking",
      contentScale = ContentScale.Crop,
      modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(14.dp)),
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = title,
      color = RailNavy,
      fontSize = 14.sp,
      fontWeight = FontWeight.Medium,
      textAlign = TextAlign.Center,
    )
  }
}

@Composable
private fun OfferingTile(
  label: String,
  bgColor: Color,
  iconColor: Color,
  icon: ImageVector,
  onClick: () -> Unit,
  testTag: String,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.clickable(onClick = onClick).testTag(testTag),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Box(
      modifier =
        Modifier.size(66.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(bgColor)
          .border(0.5.dp, Color.Black.copy(alpha = 0.04f), RoundedCornerShape(16.dp)),
      contentAlignment = Alignment.Center,
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label.replace("\n", " "),
        tint = iconColor,
        modifier = Modifier.size(30.dp),
      )
    }
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = label,
      color = TextPrimary,
      fontSize = 12.sp,
      lineHeight = 15.sp,
      fontWeight = FontWeight.Normal,
      textAlign = TextAlign.Center,
    )
  }
}

@Composable
private fun WavesOfferingTile(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.clickable(onClick = onClick).testTag("offering_waves"),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Box(
      modifier = Modifier.size(66.dp).clip(RoundedCornerShape(16.dp)).background(TileSlateBg),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = "W",
        color = Color.White,
        fontSize = 28.sp,
        fontWeight = FontWeight.ExtraBold,
      )
    }
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = "Go To\nWAVES",
      color = TextPrimary,
      fontSize = 12.sp,
      lineHeight = 15.sp,
      fontWeight = FontWeight.Normal,
      textAlign = TextAlign.Center,
    )
  }
}
