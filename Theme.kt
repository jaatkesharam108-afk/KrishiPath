package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val KrishiLightColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = PureWhite,
    primaryContainer = SoftMint,
    onPrimaryContainer = ForestGreen,
    secondary = VibrantEmerald,
    onSecondary = PureWhite,
    secondaryContainer = MintSurface,
    onSecondaryContainer = DeepEmerald,
    tertiary = HarvestAmber,
    onTertiary = PureWhite,
    tertiaryContainer = HarvestAmberSoft,
    onTertiaryContainer = HarvestAmberText,
    background = SoftBotanicalWhite,
    onBackground = DarkInk,
    surface = PureWhite,
    onSurface = DarkInk,
    surfaceVariant = MintSurface,
    onSurfaceVariant = SageMuted,
    outline = SageOutline,
    outlineVariant = ColorTokens.LightBorder,
    error = ErrorRed,
    onError = PureWhite,
    errorContainer = ErrorRedSoft,
    onErrorContainer = ErrorRed
)

private val KrishiDarkColorScheme = darkColorScheme(
    primary = CanopyLightGreen,
    onPrimary = CanopyDarkBg,
    primaryContainer = DeepEmerald,
    onPrimaryContainer = CanopyMintText,
    secondary = VibrantEmerald,
    onSecondary = PureWhite,
    secondaryContainer = CanopyDarkCard,
    onSecondaryContainer = CanopyMintText,
    tertiary = HarvestAmber,
    onTertiary = CanopyDarkBg,
    tertiaryContainer = HarvestAmberText,
    onTertiaryContainer = HarvestAmberSoft,
    background = CanopyDarkBg,
    onBackground = CanopyMintText,
    surface = CanopyDarkSurface,
    onSurface = CanopyMintText,
    surfaceVariant = CanopyDarkCard,
    onSurfaceVariant = CanopyMutedText,
    outline = DeepEmerald,
    outlineVariant = CanopyDarkCard,
    error = ErrorRed,
    onError = PureWhite,
    errorContainer = ErrorRedSoft,
    onErrorContainer = ErrorRed
)

private object ColorTokens {
    val LightBorder = androidx.compose.ui.graphics.Color(0xFFE1EFE5)
}

val KrishiShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun KrishiPathTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Intentionally preserve our signature green-and-white agriculture identity
    val colorScheme = if (darkTheme) KrishiDarkColorScheme else KrishiLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = KrishiTypography,
        shapes = KrishiShapes,
        content = content
    )
}
