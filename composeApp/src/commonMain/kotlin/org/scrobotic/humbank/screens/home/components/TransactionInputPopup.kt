package org.scrobotic.humbank.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.humbank.ktorclient.icons.imagevectors.Account
import org.scrobotic.humbank.data.AllAccount
import org.scrobotic.humbank.ui.humbankPalette

@Composable
fun TransactionInputPopup(
    balance: Double,
    senderAccount: AllAccount,
    onDismiss: () -> Unit,
    onSend: (Double, String, String) -> Unit,
    isLoading: Boolean
) {
    var amount by remember { mutableStateOf("") }
    var receiver by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf("") }
    var receiverError by remember { mutableStateOf("") }
    val palette = humbankPalette()

    LaunchedEffect(amount, receiver) {
        val amt = amount.toDoubleOrNull()
        amountError = when {
            amount.isEmpty() -> ""
            amt == null || amt <= 0 -> "Invalid amount"
            else -> ""
        }

        receiverError = when {
            receiver.isEmpty() -> ""
            receiver == senderAccount.username -> "You cannot transfer to yourself"
            else -> ""
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("New Transfer", fontWeight = FontWeight.Bold, color = palette.title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = palette.inputFillUnfocused),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Available", color = palette.subtitle)
                        Text("HMB $balance", color = palette.title, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    prefix = { Text("HMB") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    isError = amountError.isNotEmpty(),
                    supportingText = { if (amountError.isNotEmpty()) Text(amountError) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = palette.inputFillFocused,
                        unfocusedContainerColor = palette.inputFillUnfocused,
                        focusedBorderColor = palette.inputBorderFocused,
                        unfocusedBorderColor = palette.inputBorderUnfocused,
                        focusedTextColor = palette.title,
                        unfocusedTextColor = palette.title
                    )
                )

                OutlinedTextField(
                    value = receiver,
                    onValueChange = { receiver = it },
                    label = { Text("Receiver") },
                    leadingIcon = { Icon(Account, null) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    isError = receiverError.isNotEmpty(),
                    supportingText = { if (receiverError.isNotEmpty()) Text(receiverError) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = palette.inputFillFocused,
                        unfocusedContainerColor = palette.inputFillUnfocused,
                        focusedBorderColor = palette.inputBorderFocused,
                        unfocusedBorderColor = palette.inputBorderUnfocused,
                        focusedTextColor = palette.title,
                        unfocusedTextColor = palette.title
                    )
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Reference") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = palette.inputFillFocused,
                        unfocusedContainerColor = palette.inputFillUnfocused,
                        focusedBorderColor = palette.inputBorderFocused,
                        unfocusedBorderColor = palette.inputBorderUnfocused,
                        focusedTextColor = palette.title,
                        unfocusedTextColor = palette.title
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { amount.toDoubleOrNull()?.let { onSend(it, receiver, description) } },
                enabled = !isLoading && amountError.isEmpty() && receiverError.isEmpty() && amount.isNotBlank() && receiver.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = palette.primaryButton, contentColor = palette.primaryButtonText)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = palette.primaryButtonText)
                } else {
                    Text("Send")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) { Text("Cancel") }
        }
    )
}
