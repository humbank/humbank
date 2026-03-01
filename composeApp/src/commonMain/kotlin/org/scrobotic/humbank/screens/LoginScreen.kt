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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.burnoo.compose.remembersetting.rememberStringSetting
import humbank.composeapp.generated.resources.Res
import humbank.composeapp.generated.resources.login_failed
import humbank.composeapp.generated.resources.login_password
import humbank.composeapp.generated.resources.login_sign_in
import humbank.composeapp.generated.resources.login_sign_in_account
import humbank.composeapp.generated.resources.login_username
import humbank.composeapp.generated.resources.login_welcome_back
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.scrobotic.humbank.data.UserSession
import org.scrobotic.humbank.ui.HumbankGradientScreen
import org.scrobotic.humbank.ui.HumbankPanelCard
import org.scrobotic.humbank.ui.humbankPalette

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
    val palette = humbankPalette()



    val failed: String = stringResource(Res.string.login_failed)

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = palette.inputFillFocused,
        unfocusedContainerColor = palette.inputFillUnfocused,
        disabledContainerColor = palette.inputFillUnfocused.copy(alpha = 0.5f),
        focusedTextColor = palette.title,
        unfocusedTextColor = palette.title,
        disabledTextColor = palette.muted,
        focusedBorderColor = palette.inputBorderFocused,
        unfocusedBorderColor = palette.inputBorderUnfocused,
        focusedLabelColor = palette.inputBorderFocused,
        unfocusedLabelColor = palette.muted
    )

    HumbankGradientScreen(
        modifier = Modifier.fillMaxSize()
    ) {
        HumbankPanelCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.Center)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 30.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Wordmark / logo area
                Text(
                    text = "humbank",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primaryButton,
                    letterSpacing = 1.5.sp
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = stringResource(Res.string.login_welcome_back),
                    style = MaterialTheme.typography.labelLarge,
                    color = palette.muted,
                    fontSize = 13.sp
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = stringResource(Res.string.login_sign_in_account),
                    style = MaterialTheme.typography.headlineSmall,
                    color = palette.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    letterSpacing = (-0.5).sp
                )

                Spacer(Modifier.height(26.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(stringResource(Res.string.login_username)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading,
                    shape = RoundedCornerShape(14.dp),
                    colors = fieldColors
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(stringResource(Res.string.login_password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading,
                    shape = RoundedCornerShape(14.dp),
                    colors = fieldColors
                )

                Spacer(Modifier.height(16.dp))

                if (error != null) {
                    Text(
                        text = error!!,
                        color = palette.errorText,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(palette.errorBackground)
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                }

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = !isLoading && username.isNotBlank() && password.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.primaryButton,
                        contentColor = palette.primaryButtonText,
                        disabledContainerColor = palette.inputBorderUnfocused,
                        disabledContentColor = palette.muted
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
                                error = e.message ?: failed
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
                            color = palette.primaryButtonText
                        )
                    } else {
                        Text(
                            stringResource(Res.string.login_sign_in),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text = "©Humbank, 2026",
                    color = palette.muted,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp
                )
            }
        }
    }
}
