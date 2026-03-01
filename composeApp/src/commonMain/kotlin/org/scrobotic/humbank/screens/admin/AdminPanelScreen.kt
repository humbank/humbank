package org.scrobotic.humbank.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import org.humbank.ktorclient.icons.imagevectors.Account
import org.scrobotic.humbank.NetworkClient.ApiRepository
import org.scrobotic.humbank.data.AllAccount
import org.scrobotic.humbank.screens.admin.components.CreateAccountDialog
import org.scrobotic.humbank.ui.HumbankGradientScreen
import org.scrobotic.humbank.ui.HumbankPanelCard
import org.scrobotic.humbank.ui.elements.icons.processed.ArrowBack
import org.scrobotic.humbank.ui.humbankPalette

@Composable
fun AdminPanelScreen(
    apiRepository: ApiRepository,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val palette = humbankPalette()

    var accounts by remember { mutableStateOf<List<AllAccount>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }
    var isCreatingUser by remember { mutableStateOf(true) }
    var createLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            accounts = apiRepository.getAllAccounts()
        } finally {
            isLoading = false
        }
    }

    val filteredAccounts = accounts.filter { account ->
        account.full_name.contains(searchQuery, ignoreCase = true) ||
            account.username.contains(searchQuery, ignoreCase = true) ||
            account.role.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Panel") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = palette.primaryButton,
                contentColor = palette.primaryButtonText
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Account, null, modifier = Modifier.size(20.dp))
                    Text("New Account", fontWeight = FontWeight.Medium)
                }
            }
        }
    ) { padding ->
        HumbankGradientScreen(modifier = Modifier.padding(padding)) {
            HumbankPanelCard(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search users") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = palette.inputFillFocused,
                            unfocusedContainerColor = palette.inputFillUnfocused,
                            focusedBorderColor = palette.inputBorderFocused,
                            unfocusedBorderColor = palette.inputBorderUnfocused,
                            focusedTextColor = palette.title,
                            unfocusedTextColor = palette.title
                        )
                    )

                    when {
                        isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                        filteredAccounts.isEmpty() -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(if (searchQuery.isNotEmpty()) "No matching users" else "No users found", color = palette.muted)
                        }
                        else -> LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredAccounts) { account ->
                                UserAdminCard(account = account)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateAccountDialog(
            isCreatingUser = isCreatingUser,
            onDismiss = { showCreateDialog = false },
            onToggleType = { isCreatingUser = !isCreatingUser },
            onCreate = { inputUsername, fullName, pin ->
                scope.launch {
                    createLoading = true
                    try {
                        if (isCreatingUser) {
                            val names = fullName.trim().split(" ", limit = 2)
                            apiRepository.createUser(
                                firstName = names[0],
                                lastName = names.getOrElse(1) { "" },
                                username = inputUsername,
                                pin = pin,
                                role = "user"
                            )
                        } else {
                            apiRepository.createBusiness(
                                businessName = fullName,
                                ownerUsername = inputUsername,
                                pin = pin,
                                description = "Business account created by admin"
                            )
                        }

                        accounts = apiRepository.getAllAccounts()
                        showCreateDialog = false
                        snackbarHostState.showSnackbar("Account created successfully")
                    } catch (e: Exception) {
                        showCreateDialog = false
                        snackbarHostState.showSnackbar(e.message ?: "Creation failed")
                    } finally {
                        createLoading = false
                    }
                }
            },
            isLoading = createLoading
        )
    }
}

@Composable
private fun UserAdminCard(account: AllAccount) {
    val palette = humbankPalette()
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = palette.inputFillUnfocused)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(account.full_name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("@${account.username}", style = MaterialTheme.typography.bodyMedium, color = palette.subtitle)
                }
                Surface(shape = RoundedCornerShape(16.dp), color = palette.primaryButton.copy(alpha = 0.2f), contentColor = palette.primaryButtonText) {
                    Text(account.role.uppercase(), style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AdminActionButton("View")
                AdminActionButton("Ban")
                AdminActionButton("Disable")
                AdminActionButton("Delete", dangerous = true)
            }
        }
    }
}

@Composable
private fun AdminActionButton(label: String, dangerous: Boolean = false) {
    val palette = humbankPalette()
    TextButton(
        onClick = {},
        modifier = Modifier.height(36.dp),
        colors = ButtonDefaults.textButtonColors(
            containerColor = if (dangerous) palette.errorBackground else palette.primaryButton.copy(alpha = 0.2f),
            contentColor = if (dangerous) palette.errorText else palette.primaryButtonText
        )
    ) { Text(label) }
}
