package org.scrobotic.humbank.screens.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HumbankCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp), // They use very rounded corners
        color = Color(0xFF1A1C1E), // base-200 in their dark theme
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)), // border-base-300
        shadowElevation = 8.dp
    ) {
        Column(Modifier.padding(24.dp)) {
            content()
        }
    }
}