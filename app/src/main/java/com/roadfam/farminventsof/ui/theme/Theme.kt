package com.roadfam.farminventsof.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FarmLightColorScheme = lightColorScheme(
    primary = PrimaryGold,
    onPrimary = White,
    primaryContainer = LightGold,
    onPrimaryContainer = DarkGold,
    
    secondary = SecondaryOrange,
    onSecondary = White,
    secondaryContainer = LightOrange,
    onSecondaryContainer = Color(0xFFBF5C00),
    
    tertiary = InfoBlue,
    onTertiary = White,
    tertiaryContainer = Color(0xFFD4E7F7),
    onTertiaryContainer = Color(0xFF1A4D7A),
    
    background = BackgroundBeige,
    onBackground = TextGray,
    
    surface = White,
    onSurface = TextGray,
    surfaceVariant = Color(0xFFFFF4D6),
    onSurfaceVariant = TextGray,
    
    error = StatusRed,
    onError = White,
    errorContainer = StatusRedLight,
    onErrorContainer = StatusRed,
    
    outline = Color(0xFFD4C5A8),
    outlineVariant = Color(0xFFE8DCBF)
)

private val FarmDarkColorScheme = darkColorScheme(
    primary = DarkPrimaryGold,
    onPrimary = Color(0xFF3D2F00),
    primaryContainer = Color(0xFF574400),
    onPrimaryContainer = LightGold,
    
    secondary = SecondaryOrange,
    onSecondary = Color(0xFF4A2800),
    secondaryContainer = Color(0xFF6A3900),
    onSecondaryContainer = LightOrange,
    
    tertiary = Color(0xFF6CB4F0),
    onTertiary = Color(0xFF003352),
    tertiaryContainer = Color(0xFF004B73),
    onTertiaryContainer = Color(0xFFD4E7F7),
    
    background = DarkBackground,
    onBackground = Color(0xFFE8E1D6),
    
    surface = DarkSurface,
    onSurface = Color(0xFFE8E1D6),
    surfaceVariant = Color(0xFF4A4739),
    onSurfaceVariant = Color(0xFFCDC5B4),
    
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    
    outline = Color(0xFF968F7F),
    outlineVariant = Color(0xFF4A4739)
)

@Composable
fun RoadFarmInventoryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) FarmDarkColorScheme else FarmLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
