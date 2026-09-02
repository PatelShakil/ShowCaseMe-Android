package com.techsavvy.showcaseme.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * AapdiWebsite brand palette.
 *
 * The brand is built on three ideas: a warm paper ground, deep ink text and a
 * calm emerald accent, with gold reserved for highlights. Every Material 3
 * colour slot used anywhere in the app is derived from these below so screens
 * never fall through to Material's default purple greys.
 */

// ── Core brand ────────────────────────────────────────────────────
val BrandPaper = Color(0xFFF5F1E6)        // warm paper background
val BrandPaperElevated = Color(0xFFFFFDF7) // cards sitting on paper
val BrandInk = Color(0xFF0D2B3E)          // primary text / dark surfaces
val BrandInkMuted = Color(0xFF5B6A72)     // secondary text
val BrandAccent = Color(0xFF2D8B6F)       // emerald — primary action
val BrandAccentDeep = Color(0xFF1B6B56)   // pressed / tertiary
val BrandAccentSoft = Color(0xFFD6EBE3)   // accent container
val BrandGold = Color(0xFFC5A55A)         // secondary / highlight
val BrandGoldSoft = Color(0xFFF5E9CE)     // gold container
val BrandSand = Color(0xFFE7E1D2)         // surface variant
val BrandOutline = Color(0xFFA9A08C)
val BrandOutlineSoft = Color(0xFFD9D2C0)
val BrandDanger = Color(0xFFB3261E)
val BrandDangerSoft = Color(0xFFF9DEDC)
val WHITE = Color(0xFFFFFFFF)

// ── Dark counterparts ─────────────────────────────────────────────
val BrandNight = Color(0xFF071A26)
val BrandNightElevated = Color(0xFF0D2B3E)
val BrandNightVariant = Color(0xFF163A4E)
val BrandAccentLight = Color(0xFF5FC3A2)
val BrandAccentOnDark = Color(0xFF00382A)
val BrandGoldLight = Color(0xFFE2C67F)
val BrandInkOnNight = Color(0xFFE6EDF1)
val BrandInkMutedOnNight = Color(0xFFA9BCC6)
val BrandOutlineNight = Color(0xFF6B8592)
val BrandOutlineNightSoft = Color(0xFF33505F)
val BrandDangerLight = Color(0xFFF2B8B5)

// Retained for source compatibility with the QR / WebView screens.
val MainColor = Color(0xFFEDF7FF)
val YellowColor = MainColor
