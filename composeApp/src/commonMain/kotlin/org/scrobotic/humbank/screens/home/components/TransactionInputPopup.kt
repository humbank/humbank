package org.scrobotic.humbank.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.scrobotic.humbank.data.Transaction
import org.scrobotic.humbank.data.Account
import org.scrobotic.humbank.data.generateRandomId
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun TransactionInputPopup(
    account: Account,
    onDismiss: () -> Unit,
    onTransactionCreated: (Transaction) -> Unit
) {
    // 1. Input States
    var amount by remember { mutableStateOf("") }
    var sender by remember { mutableStateOf("") }
    var receiver by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1E1E),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Neue Überweisung", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)

            // Amount Input
            OutlinedTextField(
                value = amount,
                onValueChange = {
                    amount = it
                },
                label = { Text("Betrag (HMB)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            OutlinedTextField(
                value = sender,
                onValueChange = { sender = it },
                label = { Text("Sender ID") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            // Receiver Input
            OutlinedTextField(
                value = receiver,
                onValueChange = { receiver = it },
                label = { Text("Empfänger ID") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            // Description Input
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Verwendungszweck") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            // Submit Button
            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull() ?: 0.0
                    if (amt > 0 && receiver.isNotBlank() && sender.isNotBlank() && (sender == account.username || receiver == account.username)) {
                        if (sender == account.username) {
                            val newTx = Transaction(
                                id = "tx_${generateRandomId()}", // Generate temp ID
                                sender = sender, // Your ID
                                receiver = receiver,
                                amount = amt,
                                description = TODO(),
                                transaction_date = TODO(),
                            )
                            onTransactionCreated(newTx)
                            onDismiss()
                        }
                        else{
                            val newTx = Transaction(
                                id = "tx_${generateRandomId()}", // Generate temp ID
                                sender = sender, // Your ID
                                receiver = receiver,
                                amount = amt,
                                description = TODO(),
                                transaction_date = TODO(),
                            )
                            onTransactionCreated(newTx)
                            onDismiss()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)) // Pink40
            ) {
                Text("Überweisung bestätigen", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}