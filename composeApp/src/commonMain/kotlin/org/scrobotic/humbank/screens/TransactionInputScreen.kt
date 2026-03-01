package org.scrobotic.humbank.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.humbank.ktorclient.icons.imagevectors.Account
import org.scrobotic.humbank.NetworkClient.ApiRepository
import org.scrobotic.humbank.NetworkClient.NetworkResult
import org.scrobotic.humbank.data.AllAccount
import org.scrobotic.humbank.data.generateRandomId
import org.scrobotic.humbank.ui.HumbankGradientScreen
import org.scrobotic.humbank.ui.HumbankPanelCard
import org.scrobotic.humbank.ui.elements.icons.processed.Close
import org.scrobotic.humbank.ui.humbankPalette

@OptIn(ExperimentalMaterial3Api::class)
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
    val palette = humbankPalette()

    var amount by remember { mutableStateOf("") }
    var receiver by remember { mutableStateOf(receiverAccount?.username ?: "") }
    var description by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New transfer") },
                navigationIcon = { IconButton(onClick = onNavigateBack, enabled = !isLoading) { Icon(Close, "Back") } }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        HumbankGradientScreen(modifier = Modifier.padding(padding)) {
            HumbankPanelCard(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Card(colors = CardDefaults.cardColors(containerColor = palette.inputFillUnfocused), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("From", color = palette.subtitle)
                            Text(senderAccount.full_name, color = palette.title, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    StyledField(amount, { amount = it }, "Amount (HMB)", isLoading, palette, KeyboardType.Decimal)

                    OutlinedTextField(
                        value = receiver,
                        onValueChange = { receiver = it },
                        label = { Text(receiverAccount?.full_name ?: "Receiver") },
                        leadingIcon = { Icon(Account, null) },
                        enabled = receiverAccount == null && !isLoading,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = palette.inputFillFocused,
                            unfocusedContainerColor = palette.inputFillUnfocused,
                            focusedBorderColor = palette.inputBorderFocused,
                            unfocusedBorderColor = palette.inputBorderUnfocused,
                            focusedTextColor = palette.title,
                            unfocusedTextColor = palette.title
                        )
                    )

                    StyledField(description, { description = it }, "Reference", isLoading, palette, null)

                    Button(
                        onClick = {
                            val amt = amount.toDoubleOrNull() ?: return@Button
                            if (amt <= 0 || receiver.isBlank() || description.isBlank() || receiver == senderAccount.username) return@Button
                            isLoading = true
                            scope.launch {
                                try {
                                    val result = apiRepository.executeTransfer(
                                        issuerUsername = receiver,
                                        amount = amt,
                                        transactionId = "tx_${generateRandomId()}",
                                        description = description
                                    )
                                    if (result is NetworkResult.Success<*>) {
                                        onTransactionSuccess()
                                        onNavigateBack()
                                    } else {
                                        snackbarHostState.showSnackbar("Transfer failed")
                                    }
                                } catch (_: Exception) {
                                    snackbarHostState.showSnackbar("Network error")
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = palette.primaryButton, contentColor = palette.primaryButtonText)
                    ) {
                        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = palette.primaryButtonText)
                        else Text("Send transfer", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun StyledField(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    enabled: Boolean,
    palette: org.scrobotic.humbank.ui.HumbankPalette,
    keyboardType: KeyboardType?
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType ?: KeyboardType.Text),
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