package com.kanzun.perbendaharaan.core.designsystem

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = com.kanzun.perbendaharaan.R.array.com_google_android_gms_fonts_certs
)

val PlusJakartaSans = GoogleFont("Plus Jakarta Sans")

val PlusJakartaSansFamily = FontFamily(
    Font(googleFont = PlusJakartaSans, fontProvider = provider, weight = FontWeight.Light),
    Font(googleFont = PlusJakartaSans, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = PlusJakartaSans, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = PlusJakartaSans, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = PlusJakartaSans, fontProvider = provider, weight = FontWeight.Bold),
)

object TypographyTokens {
    val Regular = FontWeight.Normal // 400 = Body / Supporting / Metadata
    val Medium = FontWeight.Medium   // 500 = Subtle emphasis / Form labels / Active nav
    val SemiBold = FontWeight.SemiBold // 600 = Heading / Section / Column / Button / Action / Dialog Title
    val Bold = FontWeight.Bold       // 700 = Nominal / Main Financial Data / High Emphasis
}

// 5-Level Typographic Hierarchy System
val KanzunTypography = Typography(
    // Level 1: Primary / High Emphasis (Bold 700) for large financial metrics
    displayLarge = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = TypographyTokens.Bold,
        fontSize = 44.sp,
        lineHeight = (44 * 1.15).sp,
        letterSpacing = (-0.96).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = TypographyTokens.Bold,
        fontSize = 32.sp,
        lineHeight = (32 * 1.10).sp,
        letterSpacing = (-0.64).sp,
    ),
    displaySmall = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = TypographyTokens.Bold,
        fontSize = 28.sp,
        lineHeight = (28 * 1.15).sp,
        letterSpacing = (-0.5).sp,
    ),
    // Level 2: Heading / Structure (SemiBold 600) for screen and section titles
    headlineLarge = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = TypographyTokens.SemiBold,
        fontSize = 24.sp,
        lineHeight = (24 * 1.20).sp,
        letterSpacing = (-0.4).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = TypographyTokens.SemiBold,
        fontSize = 20.sp,
        lineHeight = (20 * 1.25).sp,
        letterSpacing = (-0.3).sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = TypographyTokens.SemiBold,
        fontSize = 18.sp,
        lineHeight = (18 * 1.30).sp,
        letterSpacing = (-0.2).sp,
    ),
    titleLarge = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = TypographyTokens.SemiBold,
        fontSize = 17.sp,
        lineHeight = (17 * 1.30).sp,
        letterSpacing = (-0.1).sp,
    ),
    titleMedium = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = TypographyTokens.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = TypographyTokens.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    // Level 4: Body / Supporting Information (Normal 400)
    bodyLarge = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = TypographyTokens.Regular,
        fontSize = 18.sp,
        lineHeight = (18 * 1.40).sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = TypographyTokens.Regular,
        fontSize = 16.sp,
        lineHeight = (16 * 1.40).sp,
    ),
    bodySmall = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = TypographyTokens.Regular,
        fontSize = 14.sp,
        lineHeight = (14 * 1.40).sp,
    ),
    // Level 3: Action / Button (SemiBold 600)
    labelLarge = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = TypographyTokens.SemiBold,
        fontSize = 16.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = TypographyTokens.SemiBold,
        fontSize = 14.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.sp,
    ),
    // Level 5: Secondary / Metadata / Caption (Normal 400 - subtler via size & color)
    labelSmall = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = TypographyTokens.Regular,
        fontSize = 12.sp,
        lineHeight = (12 * 1.45).sp,
        letterSpacing = 0.sp,
    ),
)
