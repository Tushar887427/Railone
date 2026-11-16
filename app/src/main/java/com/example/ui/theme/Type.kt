package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

// Register only Regular (400) so Compose's FontSynthesis.All synthesizes Medium/SemiBold/Bold weights crisply
val MontserratFamily = FontFamily(Font(R.font.montserrat, FontWeight.Normal))

val InterFamily = FontFamily(Font(R.font.inter, FontWeight.Normal))

private fun railStyle(
  fontSize: Int,
  lineHeight: Int,
  fontWeight: FontWeight = FontWeight.Normal,
  letterSpacing: Float = 0f,
) =
  TextStyle(
    fontFamily = MontserratFamily,
    fontWeight = fontWeight,
    fontSynthesis = FontSynthesis.All,
    fontSize = fontSize.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = letterSpacing.sp,
  )

val RailOneTypography =
  Typography(
    headlineMedium = railStyle(24, 30, FontWeight.Bold),
    titleLarge = railStyle(20, 26, FontWeight.Bold),
    titleMedium = railStyle(17, 22, FontWeight.SemiBold),
    titleSmall = railStyle(14, 20, FontWeight.SemiBold),
    bodyLarge = railStyle(15, 21, FontWeight.Normal),
    bodyMedium = railStyle(14, 20, FontWeight.Normal),
    bodySmall = railStyle(12, 16, FontWeight.Normal),
    labelLarge = railStyle(14, 18, FontWeight.SemiBold),
    labelMedium = railStyle(12, 16, FontWeight.Medium),
    labelSmall = railStyle(11, 14, FontWeight.Medium),
  )
