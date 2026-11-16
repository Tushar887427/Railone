package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.RailBlue

enum class BottomTab(
  val label: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val tag: String,
) {
  HOME("Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
  MY_BOOKINGS(
    "My Bookings",
    Icons.Filled.ConfirmationNumber,
    Icons.Outlined.ConfirmationNumber,
    "nav_my_bookings",
  ),
  YOU("You", Icons.Filled.Person, Icons.Outlined.Person, "nav_you"),
  MENU("Menu", Icons.Filled.Menu, Icons.Outlined.Menu, "nav_menu"),
}

@Composable
fun RailBottomBar(
  selectedTab: BottomTab,
  onSelectTab: (BottomTab) -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier =
      modifier
        .fillMaxWidth()
        .background(RailBlue)
        .windowInsetsPadding(WindowInsets.navigationBars)
        .height(64.dp),
    horizontalArrangement = Arrangement.SpaceEvenly,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    BottomTab.entries.forEach { tab ->
      val selected = tab == selectedTab
      val contentColor = if (selected) Color.White else Color.White.copy(alpha = 0.78f)
      Column(
        modifier =
          Modifier.weight(1f)
            .clickable { onSelectTab(tab) }
            .padding(vertical = 8.dp)
            .testTag(tab.tag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
      ) {
        Icon(
          imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
          contentDescription = tab.label,
          tint = contentColor,
          modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
          text = tab.label,
          color = contentColor,
          fontSize = 11.sp,
          fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
          maxLines = 1,
        )
      }
    }
  }
}
