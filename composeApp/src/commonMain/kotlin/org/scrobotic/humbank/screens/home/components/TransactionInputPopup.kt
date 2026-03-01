package org.scrobotic.humbank.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.humbank.ktorclient.icons.imagevectors.Account
import org.scrobotic.humbank.data.AllAccount
import org.scrobotic.humbank.data.formatCurrency
import org.scrobotic.humbank.ui.humbankPalette

@Composable
fun TransactionInputPopup(
    balance: Double,
    senderAccount: AllAccount,
    onDismiss: () -> Unit,
    onSend: (Double, String, String) -> Unit,
    isLoading: Boolean,
    prefilledReceiver: String = ""
) {
    var amount by remember { mutableStateOf("") }
    var receiver by remember { mutableStateOf(prefilledReceiver) }
    val receiverLocked = prefilledReceiver.isNotEmpty()
    var description by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf("") }
    var receiverError by remember { mutableStateOf("") }
    val palette = humbankPalette()

    LaunchedEffect(amount, receiver) {
        val amt = amount.toDoubleOrNull()
        amountError = when {
            amount.isEmpty() -> ""
            amt == null || amt <= 0 -> "Enter a valid amount"
            amt > balance -> "Insufficient balance"
            else -> ""
        }
        receiverError = when {
            receiver.isEmpty() -> ""
            receiver == senderAccount.username -> "Can't transfer to yourself"
            else -> ""
        }
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = palette.inputFillFocused,
        unfocusedContainerColor = palette.inputFillUnfocused,
        focusedBorderColor = palette.inputBorderFocused,
        unfocusedBorderColor = palette.inputBorderUnfocused,
        focusedTextColor = palette.title,
        unfocusedTextColor = palette.title,
        focusedLabelColor = palette.inputBorderFocused,
        unfocusedLabelColor = palette.muted,
        errorContainerColor = palette.inputFillFocused,
        errorBorderColor = palette.dangerButton,
        errorLabelColor = palette.dangerButton,
        errorTextColor = palette.title
    )

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        containerColor = palette.panel,
        shape = RoundedCornerShape(28.dp),
        title = {
            Column {
                Text(
                    "New Transfer",
                    fontWeight = FontWeight.Bold,
                    color = palette.title,
                    fontSize = 20.sp,
                    letterSpacing = (-0.3).sp
                )
                Text(
                    "Send HMB to another account",
                    color = palette.muted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Balance pill
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(palette.accentGlow.copy(alpha = 0.5f))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Available balance",
                        color = palette.muted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "HMB ${balance.formatCurrency()}",
                        color = palette.primaryButton,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    prefix = { Text("HMB ", color = palette.muted, fontSize = 14.sp) },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    isError = amountError.isNotEmpty(),
                    supportingText = { if (amountError.isNotEmpty()) Text(amountError, color = palette.dangerButton, fontSize = 11.sp) },
                    shape = RoundedCornerShape(14.dp),
                    colors = textFieldColors
                )

                OutlinedTextField(
                    value = receiver,
                    onValueChange = { if (!receiverLocked) receiver = it },
                    label = { Text(if (receiverLocked) "Sending to" else "Recipient username") },
                    leadingIcon = { Icon(Account, null, tint = if (receiverLocked) palette.primaryButton else palette.muted) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading && !receiverLocked,
                    readOnly = receiverLocked,
                    isError = receiverError.isNotEmpty(),
                    supportingText = { if (receiverError.isNotEmpty()) Text(receiverError, color = palette.dangerButton, fontSize = 11.sp) },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = if (receiverLocked) palette.primaryButton.copy(alpha = 0.08f) else palette.inputFillFocused,
                        unfocusedContainerColor = if (receiverLocked) palette.primaryButton.copy(alpha = 0.08f) else palette.inputFillUnfocused,
                        disabledContainerColor = palette.primaryButton.copy(alpha = 0.08f),
                        focusedBorderColor = palette.inputBorderFocused,
                        unfocusedBorderColor = if (receiverLocked) palette.primaryButton.copy(alpha = 0.4f) else palette.inputBorderUnfocused,
                        disabledBorderColor = palette.primaryButton.copy(alpha = 0.4f),
                        focusedTextColor = palette.title,
                        unfocusedTextColor = palette.title,
                        disabledTextColor = palette.title,
                        focusedLabelColor = palette.inputBorderFocused,
                        unfocusedLabelColor = if (receiverLocked) palette.primaryButton else palette.muted,
                        disabledLabelColor = palette.primaryButton,
                        disabledLeadingIconColor = palette.primaryButton
                    )
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Reference (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(14.dp),
                    colors = textFieldColors
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { amount.toDoubleOrNull()?.let { onSend(it, receiver, description) } },
                enabled = !isLoading && amountError.isEmpty() && receiverError.isEmpty() && amount.isNotBlank() && receiver.isNotBlank(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.primaryButton,
                    contentColor = palette.primaryButtonText,
                    disabledContainerColor = palette.inputBorderUnfocused,
                    disabledContentColor = palette.muted
                ),
                modifier = Modifier.size(height = 46.dp, width = 110.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = palette.primaryButtonText
                    )
                } else {
                    Text("Send", fontWeight = FontWeight.SemiBold)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading,
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Cancel", color = palette.muted)
            }
        }
    )
}
