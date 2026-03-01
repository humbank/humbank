package org.scrobotic.humbank.screens.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.scrobotic.humbank.ui.humbankPalette

@Composable
fun InfoCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val palette = humbankPalette()
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = palette.cardSurface,
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.verticalGradient(
                colors = listOf(
                    palette.cardStroke.copy(alpha = 0.8f),
                    palette.cardStroke.copy(alpha = 0.2f)
                )
            )
        ),
        shadowElevation = 4.dp,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label.uppercase(),
                fontSize = 10.sp,
                letterSpacing = 0.8.sp,
                color = palette.muted,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                letterSpacing = (-0.3).sp
            )
        }
    }
}
