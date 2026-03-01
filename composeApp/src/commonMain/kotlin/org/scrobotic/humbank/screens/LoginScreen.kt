package org.scrobotic.humbank.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import dev.burnoo.compose.remembersetting.rememberStringSetting
import kotlinx.coroutines.launch
import org.scrobotic.humbank.data.UserSession
import org.scrobotic.humbank.ui.HumbankGradientScreen
import org.scrobotic.humbank.ui.HumbankPanelCard

@Composable
fun LoginScreen(
    onLogin: suspend (username: String, password: String) -> UserSession,
    onLoginSuccess: (UserSession) -> Unit
) {
    var token by rememberStringSetting("token", "")
    var savedUsername by rememberStringSetting("username", "")
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    HumbankGradientScreen(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        HumbankPanelCard(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Welcome back",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFFAFA6D4)
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Sign in to Humbank",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(22.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.06f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.03f),
                        disabledContainerColor = Color.White.copy(alpha = 0.02f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        disabledTextColor = Color(0xFFBBB4DD),
                        focusedBorderColor = Color(0xFFD6C7FF),
                        unfocusedBorderColor = Color(0xFF4D4473),
                        focusedLabelColor = Color(0xFFDCCFFF),
                        unfocusedLabelColor = Color(0xFFA89ECF)
                    )
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.06f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.03f),
                        disabledContainerColor = Color.White.copy(alpha = 0.02f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        disabledTextColor = Color(0xFFBBB4DD),
                        focusedBorderColor = Color(0xFFD6C7FF),
                        unfocusedBorderColor = Color(0xFF4D4473),
                        focusedLabelColor = Color(0xFFDCCFFF),
                        unfocusedLabelColor = Color(0xFFA89ECF)
                    )
                )

                Spacer(Modifier.height(14.dp))

                if (error != null) {
                    Text(
                        text = error!!,
                        color = Color(0xFFFF9FAE),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF5A1C35).copy(alpha = 0.4f))
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                }

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFECE4FF),
                        contentColor = Color(0xFF28194A),
                        disabledContainerColor = Color(0xFF948AAE),
                        disabledContentColor = Color(0xFF433A59)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    onClick = {
                        isLoading = true
                        error = null

                        scope.launch {
                            try {
                                val session = onLogin(username, password)
                                token = session.token
                                savedUsername = session.username
                                onLoginSuccess(session)
                            } catch (e: Exception) {
                                error = e.message ?: "Login failed"
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color(0xFF28194A)
                        )
                    } else {
                        Text(
                            "Login",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    text = "Secure access to your account and transactions",
                    color = Color(0xFF9A92BE),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
