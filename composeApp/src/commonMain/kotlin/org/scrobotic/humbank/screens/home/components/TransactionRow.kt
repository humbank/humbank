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
import org.scrobotic.humbank.ui.Gray
import org.scrobotic.humbank.ui.GreenStart
import org.scrobotic.humbank.ui.Pink40
import org.scrobotic.humbank.ui.elements.icons.processed.ArrowDownward
import org.scrobotic.humbank.ui.elements.icons.processed.ArrowUpward

@Composable
fun TransactionRow(
    tx: Transaction,
    accountId: String?,
    onClick: (Transaction) -> Unit // New parameter for click handling
) {
    val isIncoming = tx.receiver == accountId

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(tx) }
            .padding(vertical = 12.dp, horizontal = 16.dp), // Increased padding for better touch targets
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    // Using GreenStart and Pink40 from your palette
                    color = if (isIncoming) GreenStart.copy(alpha = 0.1f) else Pink40.copy(alpha = 0.1f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                // Assuming you have these icons available
                imageVector = if(isIncoming) ArrowUpward else ArrowDownward,
                contentDescription = null,
                tint = if (isIncoming) GreenStart else Pink40,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = tx.pureDescription,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = tx.created.toString(),
                color = Gray, // Using Gray from your UI palette
                fontSize = 12.sp
            )
        }

        Text(
            // Using your formatCurrency extension
            text = (if (isIncoming) "+" else "-") + tx.amount.formatCurrency(),
            color = if (isIncoming) GreenStart else Pink40,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
    }
}