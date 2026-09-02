package com.techsavvy.showcaseme.ui.theme

import android.app.Activity
import android.os.Build
import android.view.WindowInsetsController
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = BrandAccent,
    onPrimary = WHITE,
    primaryContainer = BrandAccentSoft,
    onPrimaryContainer = BrandAccentDeep,

    secondary = BrandGold,
    onSecondary = BrandInk,
    secondaryContainer = BrandGoldSoft,
    onSecondaryContainer = BrandInk,

    tertiary = BrandAccentDeep,
    onTertiary = WHITE,
    tertiaryContainer = BrandAccentSoft,
    onTertiaryContainer = BrandAccentDeep,

    background = BrandPaper,
    onBackground = BrandInk,
    surface = BrandPaperElevated,
    onSurface = BrandInk,
    surfaceVariant = BrandSand,
    onSurfaceVariant = BrandInkMuted,
    surfaceTint = BrandAccent,

    inverseSurface = BrandInk,
    inverseOnSurface = BrandPaper,
    inversePrimary = BrandAccentLight,

    outline = BrandOutline,
    outlineVariant = BrandOutlineSoft,

    error = BrandDanger,
    onError = WHITE,
    errorContainer = BrandDangerSoft,
    onErrorContainer = BrandDanger,
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandAccentLight,
    onPrimary = BrandAccentOnDark,
    primaryContainer = BrandAccentDeep,
    onPrimaryContainer = BrandAccentSoft,

    secondary = BrandGoldLight,
    onSecondary = BrandInk,
    secondaryContainer = BrandNightVariant,
    onSecondaryContainer = BrandGoldSoft,

    tertiary = BrandAccentLight,
    onTertiary = BrandInk,
    tertiaryContainer = BrandAccentDeep,
    onTertiaryContainer = BrandAccentSoft,

    background = BrandNight,
    onBackground = BrandInkOnNight,
    surface = BrandNightElevated,
    onSurface = BrandInkOnNight,
    surfaceVariant = BrandNightVariant,
    onSurfaceVariant = BrandInkMutedOnNight,
    surfaceTint = BrandAccentLight,

    inverseSurface = BrandPaper,
    inverseOnSurface = BrandInk,
    inversePrimary = BrandAccent,

    outline = BrandOutlineNight,
    outlineVariant = BrandOutlineNightSoft,

    error = BrandDangerLight,
    onError = BrandInk,
    errorContainer = BrandDanger,
    onErrorContainer = BrandDangerSoft,
)

val BrandShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

/**
 * @param darkTheme the app currently ships light-locked because the QR and
 * WebView screens are drawn against the paper ground. The dark scheme above is
 * complete — pass `isSystemInDarkTheme()` here to switch the whole app over.
 */
@Composable
fun ShowCaseMeTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val context = LocalContext.current

    SideEffect {
        val window = (context as? Activity)?.window ?: return@SideEffect
        window.statusBarColor = colorScheme.background.toArgb()
        window.navigationBarColor = colorScheme.background.toArgb()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Light icons on a dark bar, dark icons on the paper ground.
            val appearance = if (darkTheme) 0 else WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            window.insetsController?.setSystemBarsAppearance(
                appearance,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = BrandShapes,
        content = content
    )
}
