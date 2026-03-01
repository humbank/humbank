package org.scrobotic.humbank.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val HumbankTopGradient = Color(0xFF120F26)
val HumbankMiddleGradient = Color(0xFF2E1E53)
val HumbankBottomGradient = Color(0xFF0C0A1A)
val HumbankPanelColor = Color(0xFF17152A).copy(alpha = 0.92f)
val HumbankPanelStroke = Color.White.copy(alpha = 0.07f)

@Composable
fun HumbankGradientScreen(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(HumbankTopGradient, HumbankMiddleGradient, HumbankBottomGradient)
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
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = HumbankPanelStroke,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = modifier
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = HumbankPanelColor),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(28.dp))
                .padding(1.dp)
        ) {
            content()
        }
    }
}
