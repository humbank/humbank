package org.scrobotic.humbank.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.humbank.ktorclient.icons.imagevectors.Account
import org.scrobotic.humbank.NetworkClient.ApiRepository
import org.scrobotic.humbank.NetworkClient.NetworkResult
import org.scrobotic.humbank.data.Transaction
import org.scrobotic.humbank.data.Account
import org.scrobotic.humbank.data.AllAccount
import org.scrobotic.humbank.data.generateRandomId
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionInputPopup(
    balance: Double,
    senderAccount: AllAccount,
    onDismiss: () -> Unit,
    // Pass the logic as a lambda to keep the UI component "dumb" and reusable
    onSend: (Double, String, String) -> Unit,
    isLoading: Boolean
) {
    var amount by remember { mutableStateOf("") }
    var receiver by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Validation states matching your Admin Dialog style
    var amountError by remember { mutableStateOf("") }
    var receiverError by remember { mutableStateOf("") }

    // Logic synced via LaunchedEffect (like your Admin Dialog)
    LaunchedEffect(amount, receiver) {
        val amt = amount.toDoubleOrNull()
        amountError = when {
            amount.isEmpty() -> ""
            amt == null || amt <= 0 -> "Ungültiger Betrag"
            else -> ""
        }

        receiverError = when {
            receiver.isEmpty() -> ""
            receiver == senderAccount.username -> "Selbstüberweisung nicht möglich"
            else -> ""
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Neue Überweisung", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Balance Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Verfügbar", color = Color.Gray, fontSize = 12.sp)
                        Text("€$balance", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                // Amount
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Betrag") },
                    prefix = { Text("HMB") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    isError = amountError.isNotEmpty(),
                    supportingText = { if (amountError.isNotEmpty()) Text(amountError) }
                )

                // Receiver
                OutlinedTextField(
                    value = receiver,
                    onValueChange = { receiver = it },
                    label = { Text("Empfänger") },
                    leadingIcon = { Icon(Account, null) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    isError = receiverError.isNotEmpty(),
                    supportingText = { if (receiverError.isNotEmpty()) Text(receiverError) }
                )

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Verwendungszweck") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    amount.toDoubleOrNull()?.let { onSend(it, receiver, description) }
                },
                enabled = !isLoading &&
                        amountError.isEmpty() &&
                        receiverError.isEmpty() &&
                        amount.isNotBlank() &&
                        receiver.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text("Senden")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text("Abbrechen")
            }
        }
    )
}