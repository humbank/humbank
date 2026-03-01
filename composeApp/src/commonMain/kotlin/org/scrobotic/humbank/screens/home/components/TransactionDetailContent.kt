package org.scrobotic.humbank.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import humbank.composeapp.generated.resources.Res
import humbank.composeapp.generated.resources.close_btn
import humbank.composeapp.generated.resources.tx_description
import humbank.composeapp.generated.resources.tx_receiverID
import humbank.composeapp.generated.resources.tx_senderID
import org.jetbrains.compose.resources.stringResource
import org.scrobotic.humbank.data.Transaction
import org.scrobotic.humbank.data.formatCurrency
import org.scrobotic.humbank.ui.elements.icons.processed.ReceiptLong
import org.scrobotic.humbank.ui.humbankPalette
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun TransactionDetailContent(accountId: String?, transaction: Transaction, onClose: () -> Unit) {
    val isIncoming = transaction.receiver == accountId
    val palette = humbankPalette()

    val accentColor = if (isIncoming) Color(0xFF22C55E) else Color(0xFFF43F5E)
    val iconBg = if (isIncoming) Color(0xFF22C55E).copy(alpha = 0.12f) else Color(0xFFF43F5E).copy(alpha = 0.12f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pull handle indicator
        Box(
            modifier = Modifier
                .size(width = 40.dp, height = 4.dp)
                .clip(CircleShape)
                .background(palette.muted.copy(alpha = 0.4f))
        )

        Spacer(modifier = Modifier.size(24.dp))

        // Icon
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(iconBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                ReceiptLong,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(34.dp)
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        // Status label
        Text(
            text = if (isIncoming) "Received" else "Sent",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = accentColor,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.size(6.dp))

        // Amount
        Text(
            text = if (isIncoming) "+${transaction.amount.formatCurrency()}" else "−${transaction.amount.formatCurrency()}",
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = palette.title,
            letterSpacing = (-1).sp
        )

        Text(
            text = "HMB",
            fontSize = 14.sp,
            color = palette.muted,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.size(24.dp))

        // Detail rows in a card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(palette.cardSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                DetailInfoRow(stringResource(Res.string.tx_senderID), transaction.sender, palette)
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = palette.cardStroke.copy(alpha = 0.4f),
                    thickness = 0.5.dp
                )
                DetailInfoRow(stringResource(Res.string.tx_receiverID), transaction.receiver, palette)
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = palette.cardStroke.copy(alpha = 0.4f),
                    thickness = 0.5.dp
                )
                DetailInfoRow(stringResource(Res.string.tx_description), transaction.description, palette)
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = palette.cardStroke.copy(alpha = 0.4f),
                    thickness = 0.5.dp
                )
                DetailInfoRow("Transaction ID", transaction.id, palette, monospace = true)
            }
        }

        Spacer(modifier = Modifier.size(24.dp))

        // Close button
        Button(
            onClick = onClose,
            modifier = Modifier
                .fillMaxWidth()
                .size(height = 52.dp, width = 0.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.primaryButton,
                contentColor = palette.primaryButtonText
            )
        ) {
            Text(
                stringResource(Res.string.close_btn),
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
private fun DetailInfoRow(
    label: String,
    value: String,
    palette: org.scrobotic.humbank.ui.HumbankPalette,
    monospace: Boolean = false
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            label,
            fontSize = 11.sp,
            color = palette.muted,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.4.sp
        )
        Text(
            value,
            fontSize = if (monospace) 12.sp else 13.sp,
            color = palette.title,
            fontWeight = if (monospace) FontWeight.Normal else FontWeight.SemiBold,
            fontFamily = if (monospace) androidx.compose.ui.text.font.FontFamily.Monospace else androidx.compose.ui.text.font.FontFamily.Default,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}
