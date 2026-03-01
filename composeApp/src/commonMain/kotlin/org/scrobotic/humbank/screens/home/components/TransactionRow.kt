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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(tx) }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color = if (isIncoming) GreenStart.copy(alpha = 0.14f) else Pink40.copy(alpha = 0.14f), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isIncoming) ArrowUpward else ArrowDownward,
                contentDescription = null,
                tint = if (isIncoming) GreenStart else Pink40,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(text = tx.description, color = palette.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = tx.transaction_date.toString(), color = palette.subtitle, fontSize = 12.sp)
        }

        Text(
            text = (if (isIncoming) "+" else "-") + tx.amount.formatCurrency(),
            color = if (isIncoming) GreenStart else Pink40,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}
