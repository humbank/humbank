package org.scrobotic.humbank.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import humbank.composeapp.generated.resources.Res
import humbank.composeapp.generated.resources.user_transaction
import org.jetbrains.compose.resources.stringResource
import org.scrobotic.humbank.data.AllAccount
import org.scrobotic.humbank.ui.HumbankGradientScreen
import org.scrobotic.humbank.ui.HumbankPanelCard
import org.scrobotic.humbank.ui.humbankPalette
import org.scrobotic.humbank.ui.elements.icons.processed.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(receiverAccount: AllAccount, onTransaction: (AllAccount) -> Unit, onBack: () -> Unit) {
    val palette = humbankPalette()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        HumbankGradientScreen(modifier = Modifier.padding(padding)) {
            HumbankPanelCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .align(Alignment.TopCenter)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(palette.inputBorderUnfocused),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = receiverAccount.full_name.firstOrNull()?.toString() ?: "?",
                            color = palette.title,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(receiverAccount.full_name, style = MaterialTheme.typography.titleLarge, color = palette.title)
                    Text("@${receiverAccount.username}", style = MaterialTheme.typography.bodyMedium, color = palette.subtitle)

                    Button(
                        onClick = { onTransaction(receiverAccount) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = palette.primaryButton,
                            contentColor = palette.primaryButtonText
                        )
                    ) {
                        Text(stringResource(Res.string.user_transaction), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
