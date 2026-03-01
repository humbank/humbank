package org.scrobotic.humbank.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import org.humbank.ktorclient.icons.imagevectors.Account
import org.scrobotic.humbank.NetworkClient.ApiRepository
import org.scrobotic.humbank.NetworkClient.NetworkResult
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
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Form states
    var amount by remember { mutableStateOf("") }
    var receiver by remember { mutableStateOf(receiverAccount?.username ?: "") }
    var description by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Validation states
    var amountError by remember { mutableStateOf(false) }
    var receiverError by remember { mutableStateOf(false) }
    var descriptionError by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFF0F0F0F), // True dark
        topBar = {
            TopAppBar(
                title = { Text("Neue Überweisung", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, enabled = !isLoading) {
                        Icon(Close, "Zurück", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E1E1E))
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Balance preview card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Ihr Kontostand",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "€${senderAccount.full_name}", // Replace with real balance
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Amount field with live validation
            OutlinedTextField(
                value = amount,
                onValueChange = { newAmount ->
                    amount = newAmount
                    amountError = newAmount.toDoubleOrNull()?.let { it <= 0 } == true || newAmount.isBlank()
                },
                label = { Text("Betrag (HMB)") },
                prefix = { Text("€", color = Color.Gray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                singleLine = true,
                isError = amountError,
                supportingText = {
                    if (amountError) {
                        Text("Mindestens €0.01", color = MaterialTheme.colorScheme.error)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF2A2A2A),
                    unfocusedContainerColor = Color(0xFF2A2A2A),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    errorTextColor = MaterialTheme.colorScheme.error
                )
            )

            // Sender (read-only)
            OutlinedTextField(
                value = senderAccount.username,
                onValueChange = { },
                label = { Text("Von (Sie)") },
                leadingIcon = { Icon(Account, null, tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.White.copy(alpha = 0.7f)
                )
            )

            // Receiver field
            OutlinedTextField(
                value = receiver,
                onValueChange = { newReceiver ->
                    receiver = newReceiver
                    receiverError = newReceiver.isBlank() || newReceiver == senderAccount.username
                },
                label = { Text(receiverAccount?.full_name ?: "An Empfänger ID") },
                leadingIcon = { Icon(Account, null, tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                enabled = receiverAccount == null && !isLoading,
                singleLine = true,
                isError = receiverError,
                supportingText = {
                    if (receiverError && receiver == senderAccount.username) {
                        Text("Kann nicht an sich selbst senden", color = MaterialTheme.colorScheme.error)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF2A2A2A),
                    unfocusedContainerColor = Color(0xFF2A2A2A),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { newDesc ->
                    description = newDesc
                    descriptionError = newDesc.isBlank()
                },
                label = { Text("Verwendungszweck") },
                maxLines = 2,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                isError = descriptionError,
                supportingText = {
                    if (descriptionError) {
                        Text("Verwendungszweck erforderlich", color = MaterialTheme.colorScheme.error)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF2A2A2A),
                    unfocusedContainerColor = Color(0xFF2A2A2A),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            // Confirm button
            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull()
                    if (amt == null || amt <= 0 || receiver.isBlank() ||
                        description.isBlank() || receiver == senderAccount.username) {
                        return@Button
                    }

                    isLoading = true

                    scope.launch {
                        try {
                            val transactionId = "tx_${generateRandomId()}"
                            val result = apiRepository.executeTransfer(
                                issuerUsername = receiver,
                                amount = amt,
                                transactionId = transactionId,
                                description = description
                            )

                            when (result) {
                                is NetworkResult.Success<*> -> {
                                    // ✅ NAVIGATE FIRST
                                    onTransactionSuccess()
                                    onNavigateBack()

                                    // ✅ THEN snackbar shows on previous screen
                                    snackbarHostState.showSnackbar("✅ Überweisung erfolgreich!")
                                }
                                is NetworkResult.Failure -> {
                                    // ✅ NAVIGATE FIRST (for error too)
                                    onNavigateBack()

                                    val errorMsg = when {
                                        result.errorMessage?.contains("User not found") == true -> "Empfänger nicht gefunden"
                                        result.errorMessage?.contains("Insufficient funds") == true -> "Nicht genug Guthaben"
                                        else -> result.errorMessage ?: "Überweisung fehlgeschlagen"
                                    }
                                    snackbarHostState.showSnackbar("❌ $errorMsg")
                                }
                                else -> {
                                    onNavigateBack()
                                    snackbarHostState.showSnackbar("❌ Unbekannter Fehler")
                                }
                            }

                        } catch (e: Exception) {
                            onNavigateBack()
                            snackbarHostState.showSnackbar("❌ Netzwerkfehler")
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading && !amountError && !receiverError && !descriptionError,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE91E63)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text("Überweisung senden", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
