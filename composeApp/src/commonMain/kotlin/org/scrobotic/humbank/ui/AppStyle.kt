package org.scrobotic.humbank.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class HumbankPalette(
    val gradientTop: Color,
    val gradientMiddle: Color,
    val gradientBottom: Color,
    val panel: Color,
    val panelStroke: Color,
    val title: Color,
    val subtitle: Color,
    val muted: Color,
    val inputFillFocused: Color,
    val inputFillUnfocused: Color,
    val inputBorderFocused: Color,
    val inputBorderUnfocused: Color,
    val primaryButton: Color,
    val primaryButtonText: Color,
    val dangerButton: Color,
    val dangerButtonText: Color,
    val errorText: Color,
    val errorBackground: Color,
    // Extended palette for richer UI
    val accentGlow: Color,
    val cardSurface: Color,
    val cardStroke: Color,
    val incomeBg: Color,
    val expenseBg: Color,
    val balanceText: Color
)

@Composable
fun humbankPalette(): HumbankPalette {
    val dark = isSystemInDarkTheme()
    return if (dark) {
        HumbankPalette(
            gradientTop = Color(0xFF0D0B1A),
            gradientMiddle = Color(0xFF170F30),
            gradientBottom = Color(0xFF0A0916),
            panel = Color(0xFF1A1630).copy(alpha = 0.95f),
            panelStroke = Color(0xFF6B5ACD).copy(alpha = 0.25f),
            title = Color(0xFFF0ECFF),
            subtitle = Color(0xFFAA9EDA),
            muted = Color(0xFF7A6EAA),
            inputFillFocused = Color(0xFF241E42),
            inputFillUnfocused = Color(0xFF1C1836),
            inputBorderFocused = Color(0xFF8B7FD4),
            inputBorderUnfocused = Color(0xFF3D3566),
            primaryButton = Color(0xFF7C6AE8),
            primaryButtonText = Color(0xFFF5F2FF),
            dangerButton = Color(0xFFCF375D),
            dangerButtonText = Color.White,
            errorText = Color(0xFFFF9FAE),
            errorBackground = Color(0xFF5A1C35).copy(alpha = 0.4f),
            accentGlow = Color(0xFF7C6AE8).copy(alpha = 0.15f),
            cardSurface = Color(0xFF1E1A38),
            cardStroke = Color(0xFF4A3F7A).copy(alpha = 0.5f),
            incomeBg = Color(0xFF0D2B1F),
            expenseBg = Color(0xFF2B0D18),
            balanceText = Color(0xFFE8DEFF)
        )
    } else {
        HumbankPalette(
            gradientTop = Color(0xFFF5F0FF),
            gradientMiddle = Color(0xFFEDE4FF),
            gradientBottom = Color(0xFFFAF8FF),
            panel = Color(0xFFFFFFFF).copy(alpha = 0.96f),
            panelStroke = Color(0xFF9B8AE0).copy(alpha = 0.3f),
            title = Color(0xFF1A1035),
            subtitle = Color(0xFF5A4A8F),
            muted = Color(0xFF8877BB),
            inputFillFocused = Color(0xFFEDE6FF),
            inputFillUnfocused = Color(0xFFF4F0FF),
            inputBorderFocused = Color(0xFF6B56CC),
            inputBorderUnfocused = Color(0xFFCCC0F0),
            primaryButton = Color(0xFF5B45C8),
            primaryButtonText = Color.White,
            dangerButton = Color(0xFFC93658),
            dangerButtonText = Color.White,
            errorText = Color(0xFF9B001A),
            errorBackground = Color(0xFFFFE6EC),
            accentGlow = Color(0xFF5B45C8).copy(alpha = 0.08f),
            cardSurface = Color(0xFFFFFFFF),
            cardStroke = Color(0xFFD4C8F5),
            incomeBg = Color(0xFFE8F8F1),
            expenseBg = Color(0xFFFFEDF2),
            balanceText = Color(0xFF1A1035)
        )
    }
}

@Composable
fun HumbankGradientScreen(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val palette = humbankPalette()
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to palette.gradientTop,
                        0.4f to palette.gradientMiddle,
                        1.0f to palette.gradientBottom
                    )
                )
            ),
        content = content
    )
}

@Composable
fun HumbankPanelCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val palette = humbankPalette()
    val dark = isSystemInDarkTheme()
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = palette.panel,
        tonalElevation = 0.dp,
        shadowElevation = if (dark) 0.dp else 16.dp,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            brush = Brush.verticalGradient(
                colors = listOf(
                    palette.panelStroke.copy(alpha = 0.6f),
                    palette.panelStroke.copy(alpha = 0.15f)
                )
            )
        ),
        modifier = modifier
    ) {
        content()
    }
}

@Composable
fun appSurfaceTextColor(): Color = if (isSystemInDarkTheme()) Color(0xFFF0ECFF) else MaterialTheme.colorScheme.onSurface
