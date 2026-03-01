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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.scrobotic.humbank.data.AllAccount
import org.scrobotic.humbank.domain.Language
import org.scrobotic.humbank.ui.HumbankGradientScreen
import org.scrobotic.humbank.ui.HumbankPanelCard
import org.scrobotic.humbank.ui.elements.icons.processed.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    account: AllAccount?,
    language: Language,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onAdminPanelClick: () -> Unit
) {
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
                if (account != null) {
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
                                .background(Color(0xFF4B3D72)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = account.full_name.firstOrNull()?.toString() ?: "?",
                                color = Color.White,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(text = account.full_name, style = MaterialTheme.typography.titleLarge, color = Color.White)
                        Text(text = "@${account.username}", style = MaterialTheme.typography.bodyMedium, color = Color(0xFFAFA6D4))

                        Button(
                            onClick = onLogout,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCF375D), contentColor = Color.White)
                        ) {
                            Text("Logout", fontWeight = FontWeight.SemiBold)
                        }

                        if (account.role == "admin") {
                            Button(
                                onClick = onAdminPanelClick,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFECE4FF),
                                    contentColor = Color(0xFF28194A)
                                )
                            ) {
                                Text("Admin Panel", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                } else {
                    Text(
                        text = "User not found",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFFF9FAE),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    )
                }
            }
        }
    }
}
