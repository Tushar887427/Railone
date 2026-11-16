package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RailOneLightColorScheme =
  lightColorScheme(
    primary = RailBlue,
    onPrimary = Color.White,
    primaryContainer = FilterTrayBg,
    onPrimaryContainer = RailNavy,
    secondary = UpcomingPurpleStart,
    onSecondary = Color.White,
    secondaryContainer = UnreservedBadgeBg,
    onSecondaryContainer = UnreservedBadgeText,
    tertiary = CompletedGreen,
    onTertiary = Color.White,
    background = PageBackground,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = QrContainerBg,
    onSurfaceVariant = TextSecondary,
    outline = DividerLight,
    error = CancelledRed,
    onError = Color.White,
  )

@Composable
fun RailOneTheme(content: @Composable () -> Unit) {
  MaterialTheme(
    colorScheme = RailOneLightColorScheme,
    typography = RailOneTypography,
    content = content,
  )
}
