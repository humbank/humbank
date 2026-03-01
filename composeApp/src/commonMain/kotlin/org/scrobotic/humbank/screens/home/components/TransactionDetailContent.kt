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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import org.scrobotic.humbank.ui.GreenStart
import org.scrobotic.humbank.ui.Pink40
import org.scrobotic.humbank.ui.elements.icons.processed.ReceiptLong
import org.scrobotic.humbank.ui.humbankPalette
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun TransactionDetailContent(accountId: String?, transaction: Transaction, onClose: () -> Unit) {
    val isIncoming = transaction.receiver == accountId
    val palette = humbankPalette()

    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp).navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(64.dp).background(palette.inputFillFocused, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(ReceiptLong, contentDescription = null, tint = palette.primaryButtonText, modifier = Modifier.size(32.dp))
        }

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            text = if (isIncoming) "+${transaction.amount.formatCurrency()} HMB" else "-${transaction.amount.formatCurrency()} HMB",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isIncoming) GreenStart else Pink40
        )

        Spacer(modifier = Modifier.size(8.dp))

        InfoRow(stringResource(Res.string.tx_senderID), transaction.sender, palette)
        InfoRow(stringResource(Res.string.tx_receiverID), transaction.receiver, palette)
        InfoRow(stringResource(Res.string.tx_description), transaction.description, palette)

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = palette.panelStroke)

        Button(
            onClick = onClose,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = palette.primaryButton, contentColor = palette.primaryButtonText)
        ) {
            Text(stringResource(Res.string.close_btn), fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, palette: org.scrobotic.humbank.ui.HumbankPalette) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = palette.subtitle)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = palette.title)
    }
}
