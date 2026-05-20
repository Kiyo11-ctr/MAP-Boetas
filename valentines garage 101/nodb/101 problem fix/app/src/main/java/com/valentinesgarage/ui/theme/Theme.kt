package com.valentinesgarage.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val GarageRed          = Color(0xFFE8421A)
val GarageRedContainer = Color(0xFFFEF3EF)
val GarageNavy         = Color(0xFF1E293B)

private val LightColors = lightColorScheme(
    primary            = GarageRed,
    onPrimary          = Color.White,
    primaryContainer   = GarageRedContainer,
    onPrimaryContainer = Color(0xFF7A1A00),
    secondary          = Color(0xFF1A56DB),
    onSecondary        = Color.White,
    background         = Color(0xFFF0F2F5),
    surface            = Color.White,
    onSurface          = Color(0xFF1A202C),
    onBackground       = Color(0xFF1A202C),
    surfaceVariant     = Color(0xFFF8FAFC),
    onSurfaceVariant   = Color(0xFF64748B),
    error              = Color(0xFFDC2626),
    outline            = Color(0xFFE2E8F0)
)

private val DarkColors = darkColorScheme(
    primary          = GarageRed,
    onPrimary        = Color.White,
    primaryContainer = Color(0xFF7A2010),
    secondary        = Color(0xFF60A5FA),
    background       = Color(0xFF0F172A),
    surface          = Color(0xFF1E293B),
    onSurface        = Color(0xFFE2E8F0),
    onBackground     = Color(0xFFE2E8F0)
)

@Composable
fun ValentinesGarageTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkTheme -> DarkColors
        else      -> LightColors
    }
    MaterialTheme(colorScheme = colorScheme, typography = Typography(), content = content)
}
