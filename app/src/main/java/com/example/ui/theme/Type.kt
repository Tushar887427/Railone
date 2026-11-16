package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val MontserratFamily =
  FontFamily(
    Font(R.font.montserrat_400, FontWeight.Normal),
    Font(R.font.montserrat_500, FontWeight.Medium),
    Font(R.font.montserrat_600, FontWeight.SemiBold),
    Font(R.font.montserrat_700, FontWeight.Bold),
    Font(R.font.montserrat_800, FontWeight.ExtraBold),
  )

val PlusJakartaSansFamily =
  FontFamily(
    Font(R.font.plus_jakarta_sans_400, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_500, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_600, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_700, FontWeight.Bold),
    Font(R.font.plus_jakarta_sans_800, FontWeight.ExtraBold),
  )

val InterFamily =
  FontFamily(
    Font(R.font.inter_400, FontWeight.Normal),
    Font(R.font.inter_500, FontWeight.Medium),
    Font(R.font.inter_600, FontWeight.SemiBold),
    Font(R.font.inter_700, FontWeight.Bold),
  )

val DevanagariFontFamily =
  FontFamily(
    Font(R.font.noto_sans_devanagari_600, FontWeight.SemiBold),
    Font(R.font.noto_sans_devanagari_700, FontWeight.Bold),
  )

private fun railStyle(
  fontSize: Int,
  lineHeight: Int,
  fontWeight: FontWeight = FontWeight.Normal,
  letterSpacing: Float = 0f,
) =
  TextStyle(
    fontFamily = MontserratFamily,
    fontWeight = fontWeight,
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
