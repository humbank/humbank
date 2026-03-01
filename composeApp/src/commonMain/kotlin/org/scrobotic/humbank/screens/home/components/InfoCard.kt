package org.scrobotic.humbank.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = palette.inputFillUnfocused),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = label, fontSize = 12.sp, color = palette.subtitle, fontWeight = FontWeight.Medium)
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
