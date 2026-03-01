package org.scrobotic.humbank.screens.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import org.scrobotic.humbank.ui.humbankPalette

@Composable
fun HumbankCard(content: @Composable () -> Unit) {
    val palette = humbankPalette()
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = palette.cardSurface,
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.verticalGradient(
                colors = listOf(
                    palette.cardStroke.copy(alpha = 0.7f),
                    palette.cardStroke.copy(alpha = 0.15f)
                )
            )
        ),
        shadowElevation = 8.dp,
        tonalElevation = 0.dp
    ) {
        Column(Modifier.padding(20.dp)) { content() }
    }
}
