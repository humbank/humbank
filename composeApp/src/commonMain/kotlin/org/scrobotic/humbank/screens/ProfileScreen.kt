package org.scrobotic.humbank.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import humbank.composeapp.generated.resources.Res
import humbank.composeapp.generated.resources.admin_back
import humbank.composeapp.generated.resources.create_account_type
import humbank.composeapp.generated.resources.create_full_name
import humbank.composeapp.generated.resources.login_username
import humbank.composeapp.generated.resources.user_transaction
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.scrobotic.humbank.NetworkClient.ApiRepository
import org.scrobotic.humbank.data.AllAccount
import org.scrobotic.humbank.data.generateRandomId
import org.scrobotic.humbank.screens.home.components.TransactionInputPopup
import org.scrobotic.humbank.ui.HumbankGradientScreen
import org.scrobotic.humbank.ui.humbankPalette
import org.scrobotic.humbank.ui.elements.icons.processed.ArrowBack

@Composable
fun ProfileScreen(
    receiverAccount: AllAccount,
    senderAccount: AllAccount?,
    currentBalance: Double,
    apiRepository: ApiRepository,
    onTransactionSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val palette = humbankPalette()
    val scope = rememberCoroutineScope()

    var showTransferDialog by remember { mutableStateOf(false) }
    var isTransferLoading by remember { mutableStateOf(false) }

    HumbankGradientScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .statusBarsPadding()
        ) {
            // Back button
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(palette.cardSurface)
            ) {
                Icon(ArrowBack, contentDescription = stringResource(Res.string.admin_back), tint = palette.title, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Avatar + name
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(palette.primaryButton.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = receiverAccount.full_name.firstOrNull()?.toString()?.uppercase() ?: "?",
                        color = palette.primaryButton,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = receiverAccount.full_name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.title,
                    letterSpacing = (-0.4).sp
                )
                Text(
                    text = "@${receiverAccount.username}",
                    fontSize = 14.sp,
                    color = palette.muted
                )

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = palette.primaryButton.copy(alpha = 0.12f)
                ) {
                    Text(
                        receiverAccount.role.uppercase(),
                        color = palette.primaryButton,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Info card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = palette.cardSurface,
                border = BorderStroke(
                    1.dp,
                    Brush.verticalGradient(
                        colors = listOf(palette.cardStroke.copy(alpha = 0.6f), palette.cardStroke.copy(alpha = 0.1f))
                    )
                ),
                shadowElevation = 4.dp,
                tonalElevation = 0.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    ProfileDetailRow(stringResource(Res.string.create_full_name), receiverAccount.full_name, palette)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = palette.cardStroke.copy(alpha = 0.4f), thickness = 0.5.dp)
                    ProfileDetailRow(stringResource(Res.string.login_username), "@${receiverAccount.username}", palette)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = palette.cardStroke.copy(alpha = 0.4f), thickness = 0.5.dp)
                    ProfileDetailRow(stringResource(Res.string.create_account_type), receiverAccount.role.replaceFirstChar { it.uppercase() }, palette)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Send money button
            Button(
                onClick = { showTransferDialog = true },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.primaryButton,
                    contentColor = palette.primaryButtonText
                )
            ) {
                Text(
                    stringResource(Res.string.user_transaction),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
        }
    }

    // Inline transfer popup — no navigation needed
    if (showTransferDialog && senderAccount != null) {
        TransactionInputPopup(
            balance = currentBalance,
            senderAccount = senderAccount,
            prefilledReceiver = receiverAccount.username,
            isLoading = isTransferLoading,
            onDismiss = { showTransferDialog = false },
            onSend = { amt, receiver, desc ->
                isTransferLoading = true
                scope.launch {
                    try {
                        val result = apiRepository.executeTransfer(
                            issuerUsername = receiver,
                            amount = amt,
                            transactionId = "tx_${generateRandomId()}",
                            description = desc
                        )
                        showTransferDialog = false
                        if (result) onTransactionSuccess()
                    } finally {
                        isTransferLoading = false
                    }
                }
            }
        )
    }
}

@Composable
private fun ProfileDetailRow(label: String, value: String, palette: org.scrobotic.humbank.ui.HumbankPalette) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 13.sp, color = palette.muted, fontWeight = FontWeight.Medium)
        Text(value, fontSize = 13.sp, color = palette.title, fontWeight = FontWeight.SemiBold)
    }
}
