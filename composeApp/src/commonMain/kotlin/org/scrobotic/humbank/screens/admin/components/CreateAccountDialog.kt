package org.scrobotic.humbank.screens.admin.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

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

    // Validate PIN length and match
    LaunchedEffect(pin, confirmPin) {
        val errors = mutableListOf<String>()

        if (pin.length < 6) {
            pinLengthError = true
            errors.add("PIN must be 6+ characters")
        } else {
            pinLengthError = false
        }

        if (pin.isNotEmpty() && confirmPin.isNotEmpty() && pin != confirmPin) {
            errors.add("PINs do not match")
        }

        pinError = if (errors.isNotEmpty()) errors.joinToString(", ") else ""
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isCreatingUser) "New User" else "New Business") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Toggle switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = isCreatingUser, onClick = onToggleType)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column { Text("User", style = MaterialTheme.typography.titleMedium) }
                    Spacer(modifier = Modifier.weight(1f))
                    RadioButton(selected = !isCreatingUser, onClick = onToggleType)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column { Text("Business", style = MaterialTheme.typography.titleMedium) }
                }

                // Form fields
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(if (isCreatingUser) "Username" else "Owner Username") },
                    enabled = !isLoading
                )
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text(if (isCreatingUser) "Full Name" else "Business Name") },
                    enabled = !isLoading
                )

                // Account PIN field with length counter
                OutlinedTextField(
                    value = pin,
                    onValueChange = {
                        pin = it
                        if (confirmPin.isNotEmpty()) pinError = ""
                    },
                    label = { Text("Account PIN (6+ characters)") },
                    visualTransformation = PasswordVisualTransformation(),
                    enabled = !isLoading,
                    isError = pinError.isNotEmpty() || pinLengthError,
                    supportingText = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (pinError.isNotEmpty()) {
                                Text(pinError, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                )

                // Confirm Account PIN field
                OutlinedTextField(
                    value = confirmPin,
                    onValueChange = { confirmPin = it },
                    label = { Text("Confirm Account PIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    enabled = !isLoading,
                    isError = pinError.isNotEmpty(),
                    supportingText = {
                        if (pinError.isNotEmpty() && pin.length < 6) {
                            Text(
                                "PIN must be 6+ characters and match above",
                                color = MaterialTheme.colorScheme.error
                            )
                        } else if (pinError.isNotEmpty()) {
                            Text(pinError, color = MaterialTheme.colorScheme.error)
                        }
                    }
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
                        pin == confirmPin
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(16.dp))
                else Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
