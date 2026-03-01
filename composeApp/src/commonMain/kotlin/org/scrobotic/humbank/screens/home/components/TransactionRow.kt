package org.scrobotic.humbank.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.scrobotic.humbank.data.Transaction
import org.scrobotic.humbank.data.formatCurrency
import org.scrobotic.humbank.ui.GreenStart
import org.scrobotic.humbank.ui.Pink40
import org.scrobotic.humbank.ui.elements.icons.processed.ArrowDownward
import org.scrobotic.humbank.ui.elements.icons.processed.ArrowUpward
import org.scrobotic.humbank.ui.humbankPalette
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun TransactionRow(
    tx: Transaction,
    accountId: String?,
    onClick: (Transaction) -> Unit
) {
    val isIncoming = tx.receiver == accountId
    val palette = humbankPalette()

    val iconColor = if (isIncoming) Color(0xFF22C55E) else Color(0xFFF43F5E)
    val amountColor = if (isIncoming) Color(0xFF22C55E) else Color(0xFFF43F5E)
    val iconBg = if (isIncoming) Color(0xFF22C55E).copy(alpha = 0.12f) else Color(0xFFF43F5E).copy(alpha = 0.12f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick(tx) }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Icon bubble
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(color = iconBg, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isIncoming) ArrowUpward else ArrowDownward,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        // Description + date
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = tx.description,
                color = palette.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                letterSpacing = (-0.2).sp
            )
            Text(
                text = tx.transaction_date.toString().take(10),
                color = palette.muted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            )
        }

        // Amount
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = (if (isIncoming) "+" else "−") + tx.amount.formatCurrency(),
                color = amountColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = (-0.3).sp
            )
            Text(
                text = "HMB",
                color = palette.muted,
                fontSize = 10.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}
