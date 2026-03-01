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
    val errorBackground: Color
)

@Composable
fun humbankPalette(): HumbankPalette {
    val dark = isSystemInDarkTheme()
    return if (dark) {
        HumbankPalette(
            gradientTop = Color(0xFF120F26),
            gradientMiddle = Color(0xFF2E1E53),
            gradientBottom = Color(0xFF0C0A1A),
            panel = Color(0xFF17152A).copy(alpha = 0.92f),
            panelStroke = Color.White.copy(alpha = 0.07f),
            title = Color.White,
            subtitle = Color(0xFFAFA6D4),
            muted = Color(0xFF9A92BE),
            inputFillFocused = Color.White.copy(alpha = 0.06f),
            inputFillUnfocused = Color.White.copy(alpha = 0.03f),
            inputBorderFocused = Color(0xFFD6C7FF),
            inputBorderUnfocused = Color(0xFF4D4473),
            primaryButton = Color(0xFFECE4FF),
            primaryButtonText = Color(0xFF28194A),
            dangerButton = Color(0xFFCF375D),
            dangerButtonText = Color.White,
            errorText = Color(0xFFFF9FAE),
            errorBackground = Color(0xFF5A1C35).copy(alpha = 0.4f)
        )
    } else {
        HumbankPalette(
            gradientTop = Color(0xFFF3EEFF),
            gradientMiddle = Color(0xFFE8E0FF),
            gradientBottom = Color(0xFFFDFBFF),
            panel = Color(0xFFFFFFFF).copy(alpha = 0.93f),
            panelStroke = Color(0xFFB6A7E5).copy(alpha = 0.5f),
            title = Color(0xFF21163F),
            subtitle = Color(0xFF5D4D8B),
            muted = Color(0xFF73639F),
            inputFillFocused = Color(0xFFF1EBFF),
            inputFillUnfocused = Color(0xFFF7F3FF),
            inputBorderFocused = Color(0xFF7459BD),
            inputBorderUnfocused = Color(0xFFC7B9ED),
            primaryButton = Color(0xFF4F3698),
            primaryButtonText = Color.White,
            dangerButton = Color(0xFFC93658),
            dangerButtonText = Color.White,
            errorText = Color(0xFFB00020),
            errorBackground = Color(0xFFFFE8EE)
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
                    colors = listOf(palette.gradientTop, palette.gradientMiddle, palette.gradientBottom)
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
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = palette.panelStroke,
        tonalElevation = 0.dp,
        shadowElevation = if (isSystemInDarkTheme()) 0.dp else 3.dp,
        modifier = modifier
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = palette.panel),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(28.dp))
                .padding(1.dp)
        ) {
            content()
        }
    }
}

@Composable
fun appSurfaceTextColor(): Color = if (isSystemInDarkTheme()) Color.White else MaterialTheme.colorScheme.onSurface
