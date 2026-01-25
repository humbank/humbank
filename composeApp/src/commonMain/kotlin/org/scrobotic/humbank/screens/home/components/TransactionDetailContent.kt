package org.scrobotic.humbank.screens.home.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import humbank.composeapp.generated.resources.Res
import humbank.composeapp.generated.resources.close_btn
import humbank.composeapp.generated.resources.tx_completed
import humbank.composeapp.generated.resources.tx_description
import humbank.composeapp.generated.resources.tx_receiverID
import humbank.composeapp.generated.resources.tx_senderID
import org.jetbrains.compose.resources.stringResource
import org.scrobotic.humbank.data.Transaction
import org.scrobotic.humbank.data.formatCurrency
import org.scrobotic.humbank.ui.Blue
import org.scrobotic.humbank.ui.Gray
import org.scrobotic.humbank.ui.GreenStart
import org.scrobotic.humbank.ui.Pink40
import org.scrobotic.humbank.ui.Pink80
import org.scrobotic.humbank.ui.elements.icons.processed.ReceiptLong

@Composable
fun TransactionDetailContent(accountId: String, transaction: Transaction, onClose: () -> Unit) {
    val isIncoming = transaction.receiver == accountId
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon / Status
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(Color.Blue.copy(alpha = 0.05f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(ReceiptLong, contentDescription = null, tint = Blue, modifier = Modifier.size(32.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Large Amount
        Text(
            text = if (isIncoming) "+${transaction.amount.formatCurrency()} HMB" else "-${transaction.amount.formatCurrency()} HMB",
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isIncoming) GreenStart else Pink40
        )
        Text(transaction.created.toString(), color = Color.Gray, fontSize = 14.sp)
        Text(transaction.id, color = Color.Gray, fontSize = 14.sp)

        HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp), color = Color.Gray.copy(alpha = 0.2f))

        // Data Grid Blueprint
        DetailItem(label = stringResource(Res.string.tx_description), value = transaction.pureDescription)
        DetailItem(label = stringResource(Res.string.tx_senderID), value = transaction.sender)
        DetailItem(label = stringResource(Res.string.tx_receiverID), value = transaction.receiver)
        DetailItem(label = "Status", value = stringResource(Res.string.tx_completed))

        Spacer(modifier = Modifier.height(32.dp))

        // Action Button
        Button(
            onClick = onClose,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(stringResource(Res.string.close_btn), color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}