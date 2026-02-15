package org.scrobotic.humbank.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.scrobotic.humbank.NetworkClient.ApiRepository
import org.scrobotic.humbank.data.AllAccount
import org.scrobotic.humbank.data.generateRandomId
import org.scrobotic.humbank.ui.elements.icons.processed.Close
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun TransactionInputScreen(
    receiverAccount: AllAccount?,
    senderAccount: AllAccount,
    userToken: String,
    apiRepository: ApiRepository,
    onNavigateBack: () -> Unit,
    onTransactionSuccess: () -> Unit
) {
    // Input states
    var amount by remember { mutableStateOf("") }
    var receiver by remember { mutableStateOf(receiverAccount?.username ?: "") }
    var description by remember { mutableStateOf("") }

    // UI states
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color(0xFF1E1E1E),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Neue Überweisung",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, enabled = !isLoading) {
                        Icon(
                            imageVector = Close,
                            contentDescription = "Zurück",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E1E1E)
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Betrag (HMB)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.Gray,
                    disabledTextColor = Color.Gray
                ),
                isError = showError && amount.isBlank()
            )

            // Sender field - always read-only, showing current user's account
            OutlinedTextField(
                value = senderAccount.username,
                onValueChange = { },
                label = { Text("Sender ID") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.White,
                    disabledLabelColor = Color.Gray
                )
            )

            // Receiver field - editable if receiverAccount is null, otherwise read-only
            if (receiverAccount == null) {
                OutlinedTextField(
                    value = receiver,
                    onValueChange = { receiver = it },
                    label = { Text("Empfänger ID") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray,
                        disabledTextColor = Color.Gray
                    ),
                    isError = showError && receiver.isBlank()
                )
            } else {
                OutlinedTextField(
                    value = receiver,
                    onValueChange = { },
                    label = { Text("Empfänger ID") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = Color.White,
                        disabledLabelColor = Color.Gray
                    )
                )
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Verwendungszweck") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.Gray,
                    disabledTextColor = Color.Gray
                ),
                isError = showError && description.isBlank()
            )

            if (showError && errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // Validate all fields
                    val amt = amount.toDoubleOrNull()

                    if (amt == null || amt <= 0) {
                        showError = true
                        errorMessage = "Bitte geben Sie einen gültigen Betrag ein"
                        return@Button
                    }

                    if (receiver.isBlank()) {
                        showError = true
                        errorMessage = "Bitte geben Sie einen Empfänger ein"
                        return@Button
                    }

                    if (description.isBlank()) {
                        showError = true
                        errorMessage = "Bitte geben Sie einen Verwendungszweck ein"
                        return@Button
                    }

                    if (receiver == senderAccount.username) {
                        showError = true
                        errorMessage = "Sie können nicht an sich selbst überweisen"
                        return@Button
                    }

                    // Reset error state
                    showError = false
                    errorMessage = ""
                    isLoading = true

                    // Execute transfer via API
                    scope.launch {
                        try {
                            val transactionId = "tx_${generateRandomId()}"

                            val success = apiRepository.executeTransfer(
                                token = userToken,
                                issuerUsername = receiver,
                                amount = amt,
                                transactionId = transactionId,
                                description = description
                            )

                            if (success) {
                                // Transfer successful
                                isLoading = false
                                onTransactionSuccess()
                                onNavigateBack()
                            } else {
                                isLoading = false
                                showError = true
                                errorMessage = "Überweisung fehlgeschlagen"
                            }
                        } catch (e: Exception) {
                            isLoading = false
                            showError = true
                            errorMessage = e.message ?: "Überweisung fehlgeschlagen"
                            println("Transfer error: ${e.message}")
                            e.printStackTrace()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE91E63),
                    disabledContainerColor = Color(0xFFE91E63).copy(alpha = 0.5f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Überweisung bestätigen", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}