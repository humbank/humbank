package org.scrobotic.humbank.screens.admin.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import humbank.composeapp.generated.resources.Res
import humbank.composeapp.generated.resources.create_account_pin
import humbank.composeapp.generated.resources.create_business_label
import humbank.composeapp.generated.resources.create_business_name
import humbank.composeapp.generated.resources.create_business_title
import humbank.composeapp.generated.resources.create_cancel
import humbank.composeapp.generated.resources.create_confirm_pin
import humbank.composeapp.generated.resources.create_create
import humbank.composeapp.generated.resources.create_fill_details
import humbank.composeapp.generated.resources.create_full_name
import humbank.composeapp.generated.resources.create_owner_username
import humbank.composeapp.generated.resources.create_pin_min_length
import humbank.composeapp.generated.resources.create_pin_mismatch
import humbank.composeapp.generated.resources.create_user_label
import humbank.composeapp.generated.resources.create_user_title
import humbank.composeapp.generated.resources.login_username
import org.jetbrains.compose.resources.stringResource
import org.scrobotic.humbank.ui.humbankPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountDialog(
    isCreatingUser: Boolean,
    onDismiss: () -> Unit,
    onToggleType: () -> Unit,
    onCreate: (String, String, String) -> Unit,
    isLoading: Boolean
) {
    var username by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf("") }
    var pinLengthError by remember { mutableStateOf(false) }
    val palette = humbankPalette()

    val pin_min_length = stringResource(Res.string.create_pin_min_length)
    val pin_mismatch = stringResource(Res.string.create_pin_mismatch)

    LaunchedEffect(pin, confirmPin) {
        val errors = mutableListOf<String>()
        if (pin.length < 6) {
            pinLengthError = true
            errors.add(pin_min_length)
        } else {
            pinLengthError = false
        }
        if (pin.isNotEmpty() && confirmPin.isNotEmpty() && pin != confirmPin) {
            errors.add(pin_mismatch)
        }
        pinError = errors.joinToString(", ")
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = palette.inputFillFocused,
        unfocusedContainerColor = palette.inputFillUnfocused,
        focusedBorderColor = palette.inputBorderFocused,
        unfocusedBorderColor = palette.inputBorderUnfocused,
        focusedTextColor = palette.title,
        unfocusedTextColor = palette.title,
        focusedLabelColor = palette.inputBorderFocused,
        unfocusedLabelColor = palette.muted,
        errorBorderColor = palette.dangerButton,
        errorLabelColor = palette.dangerButton
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = palette.panel,
        shape = RoundedCornerShape(28.dp),
        title = {
            Column {
                Text(
                    if (isCreatingUser) stringResource(Res.string.create_user_title) else stringResource(Res.string.create_business_title),
                    fontWeight = FontWeight.Bold,
                    color = palette.title,
                    fontSize = 20.sp,
                    letterSpacing = (-0.3).sp
                )
                Text(
                    stringResource(Res.string.create_fill_details),
                    color = palette.muted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Account type toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(palette.inputFillUnfocused)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AccountTypeChip(
                        label = stringResource(Res.string.create_user_label),
                        selected = isCreatingUser,
                        onClick = { if (!isCreatingUser) onToggleType() },
                        modifier = Modifier.weight(1f),
                        palette = palette
                    )
                    AccountTypeChip(
                        label = stringResource(Res.string.create_business_label),
                        selected = !isCreatingUser,
                        onClick = { if (isCreatingUser) onToggleType() },
                        modifier = Modifier.weight(1f),
                        palette = palette
                    )
                }

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(if (isCreatingUser) stringResource(Res.string.login_username) else stringResource(Res.string.create_owner_username)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(14.dp),
                    colors = fieldColors
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text(if (isCreatingUser) stringResource(Res.string.create_full_name) else stringResource(Res.string.create_business_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(14.dp),
                    colors = fieldColors
                )

                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it },
                    label = { Text(stringResource(Res.string.create_account_pin)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    isError = pinError.isNotEmpty() || pinLengthError,
                    supportingText = {
                        if (pinError.isNotEmpty()) {
                            Text(pinError, color = palette.dangerButton, fontSize = 11.sp)
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = fieldColors
                )

                OutlinedTextField(
                    value = confirmPin,
                    onValueChange = { confirmPin = it },
                    label = { Text(stringResource(Res.string.create_confirm_pin)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    isError = pinError.isNotEmpty() && confirmPin.isNotEmpty(),
                    supportingText = {
                        if (pinError.isNotEmpty() && confirmPin.isNotEmpty() && pin != confirmPin) {
                            Text(stringResource(Res.string.create_pin_mismatch), color = palette.dangerButton, fontSize = 11.sp)
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = fieldColors
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(username, fullName, pin) },
                enabled = !isLoading &&
                        username.isNotBlank() &&
                        fullName.isNotBlank() &&
                        pin.length >= 6 &&
                        pin == confirmPin,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.primaryButton,
                    contentColor = palette.primaryButtonText,
                    disabledContainerColor = palette.inputBorderUnfocused,
                    disabledContentColor = palette.muted
                ),
                modifier = Modifier.size(height = 46.dp, width = 100.dp)
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = palette.primaryButtonText)
                else Text(stringResource(Res.string.create_create), fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(stringResource(Res.string.create_cancel), color = palette.muted)
            }
        }
    )
}

@Composable
private fun AccountTypeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    palette: org.scrobotic.humbank.ui.HumbankPalette
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) palette.primaryButton else palette.inputFillUnfocused,
            contentColor = if (selected) palette.primaryButtonText else palette.muted
        ),
        elevation = androidx.compose.material3.ButtonDefaults.buttonElevation(
            defaultElevation = if (selected) 4.dp else 0.dp
        )
    ) {
        Text(label, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium, fontSize = 13.sp)
    }
}
