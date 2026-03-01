package org.scrobotic.humbank.ui.elements.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.humbank.ktorclient.icons.imagevectors.Account
import org.humbank.ktorclient.icons.imagevectors.Home
import org.scrobotic.humbank.ui.elements.icons.processed.Search
import org.scrobotic.humbank.ui.elements.icons.processed.Settings
import org.scrobotic.humbank.ui.humbankPalette

// Import your actual icon vectors here — adjust to match your icon set
// These are placeholder references; swap with your real icon imports

private data class NavItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun BottomNavigationBar(
    onHomeClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onNotificationsClicked: () -> Unit,
    onAccountClicked: () -> Unit,
    selectedIndex: Int = 0
) {
    val palette = humbankPalette()

    val items = listOf(
        NavItem("Home", Home, onHomeClicked),
        NavItem("Search", Search, onNotificationsClicked),
        NavItem("Settings", Settings, onSettingsClicked),
        NavItem("Account", Account, onAccountClicked)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(palette.cardSurface)
    ) {
        // Top border line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            palette.cardStroke.copy(alpha = 0f),
                            palette.cardStroke.copy(alpha = 0.8f),
                            palette.cardStroke.copy(alpha = 0.8f),
                            palette.cardStroke.copy(alpha = 0f)
                        )
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                NavBarItem(
                    item = item,
                    selected = index == selectedIndex
                )
            }
        }
    }
}

@Composable
private fun NavBarItem(
    item: NavItem,
    selected: Boolean
) {
    val palette = humbankPalette()

    val iconColor by animateColorAsState(
        targetValue = if (selected) palette.primaryButton else palette.muted,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "iconColor"
    )

    val pillSize by animateDpAsState(
        targetValue = if (selected) 44.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "pillSize"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { item.onClick() }
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Selected pill background
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(width = pillSize, height = 34.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.primaryButton.copy(alpha = 0.12f))
                )
            }

            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = iconColor,
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .size(22.dp)
            )
        }

        Text(
            text = item.label,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = iconColor,
            letterSpacing = 0.3.sp
        )
    }
}
