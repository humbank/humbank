package org.scrobotic.humbank.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.scrobotic.humbank.data.AllAccount
import org.scrobotic.humbank.domain.Language
import org.scrobotic.humbank.ui.elements.icons.processed.ArrowDownward

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    account: AllAccount,      // Nullable now
    language: Language,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = ArrowDownward,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (account != null) {
                // Display user profile
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar placeholder
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = account.full_name.firstOrNull()?.toString() ?: "?",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text("Name: ${account.full_name}", style = MaterialTheme.typography.titleMedium)
                    Text("Username: ${account.username}", style = MaterialTheme.typography.bodyMedium)
                    Text("Role: ${account.role}", style = MaterialTheme.typography.bodyMedium)
                    //Text("Balance: $${account.balance}", style = MaterialTheme.typography.bodyMedium)
                    Text("Language: ${language.name}", style = MaterialTheme.typography.bodyMedium)

                    Button(
                        onClick = { /* TODO: Edit profile */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Edit Profile")
                    }
                }
            } else {
                // User not found
                Text(
                    "User not found",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
