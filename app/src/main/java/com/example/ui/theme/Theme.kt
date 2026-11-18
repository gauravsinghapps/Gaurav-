package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val ZivoraLightColorScheme = lightColorScheme(
    primary = ZivoraOrange,
    onPrimary = ZivoraPureWhite,
    primaryContainer = ZivoraPeachContainer,
    onPrimaryContainer = ZivoraOrangeDark,
    secondary = ZivoraOrangeLight,
    onSecondary = ZivoraPureWhite,
    secondaryContainer = ZivoraPeachSoft,
    onSecondaryContainer = ZivoraDeepInk,
    tertiary = ZivoraSuccess,
    onTertiary = ZivoraPureWhite,
    tertiaryContainer = ZivoraSuccessContainer,
    onTertiaryContainer = ZivoraSuccess,
    background = ZivoraWarmWhite,
    onBackground = ZivoraDeepInk,
    surface = ZivoraPureWhite,
    onSurface = ZivoraDeepInk,
    surfaceVariant = ZivoraPeachSoft,
    onSurfaceVariant = ZivoraMutedInk,
    outline = ZivoraOutline,
    outlineVariant = ZivoraPeachContainer,
    error = ZivoraError,
    onError = ZivoraPureWhite,
    errorContainer = ZivoraErrorContainer,
    onErrorContainer = ZivoraError
)

private val ZivoraDarkColorScheme = darkColorScheme(
    primary = ZivoraOrange,
    onPrimary = ZivoraPureWhite,
    primaryContainer = ZivoraOrangeDark,
    onPrimaryContainer = ZivoraPeachContainer,
    secondary = ZivoraOrangeLight,
    onSecondary = ZivoraDeepInk,
    secondaryContainer = ZivoraDarkSurfaceVariant,
    onSecondaryContainer = ZivoraPeachContainer,
    tertiary = ZivoraSuccess,
    onTertiary = ZivoraPureWhite,
    background = ZivoraDarkBg,
    onBackground = ZivoraDarkOnSurface,
    surface = ZivoraDarkSurface,
    onSurface = ZivoraDarkOnSurface,
    surfaceVariant = ZivoraDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFD6C7BE),
    outline = ZivoraDarkOutline,
    error = ZivoraError,
    onError = ZivoraPureWhite
)

val ZivoraShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun ZivoraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ZivoraDarkColorScheme else ZivoraLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ZivoraTypography,
        shapes = ZivoraShapes,
        content = content
    )
}
