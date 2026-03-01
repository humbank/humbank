package org.scrobotic.humbank.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.humbank.ktorclient.icons.imagevectors.Account
import org.scrobotic.humbank.NetworkClient.ApiRepository
import org.scrobotic.humbank.data.AllAccount
import org.scrobotic.humbank.screens.admin.components.CreateAccountDialog
import org.scrobotic.humbank.ui.HumbankGradientScreen
import org.scrobotic.humbank.ui.elements.icons.processed.ArrowBack
import org.scrobotic.humbank.ui.elements.icons.processed.Close
import org.scrobotic.humbank.ui.elements.icons.processed.Delete
import org.scrobotic.humbank.ui.elements.icons.processed.Gavel
import org.scrobotic.humbank.ui.elements.icons.processed.Visibility
import org.scrobotic.humbank.ui.humbankPalette

@OptIn(ExperimentalMaterial3Api::class)
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

    Box(modifier = Modifier.fillMaxSize()) {
        HumbankGradientScreen {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 0.dp,
                    bottom = 100.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header
                item {
                    Spacer(modifier = Modifier.statusBarsPadding())
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        ) {
                            Icon(
                                ArrowBack,
                                contentDescription = "Back",
                                tint = palette.title,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                "Admin Panel",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = palette.title,
                                letterSpacing = (-0.4).sp
                            )
                            Text(
                                "${accounts.size} accounts",
                                color = palette.muted,
                                fontSize = 13.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Search field
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search accounts") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = palette.inputFillFocused,
                            unfocusedContainerColor = palette.cardSurface,
                            focusedBorderColor = palette.inputBorderFocused,
                            unfocusedBorderColor = palette.cardStroke,
                            focusedTextColor = palette.title,
                            unfocusedTextColor = palette.title,
                            focusedLabelColor = palette.inputBorderFocused,
                            unfocusedLabelColor = palette.muted
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                when {
                    isLoading -> item {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = palette.primaryButton,
                                strokeWidth = 2.5.dp
                            )
                        }
                    }

                    filteredAccounts.isEmpty() -> item {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (searchQuery.isNotEmpty()) "No matching accounts" else "No accounts found",
                                color = palette.muted
                            )
                        }
                    }

                    else -> items(filteredAccounts) { account ->
                        UserAdminCard(account = account)
                    }
                }
            }
        }

        // FAB
        ExtendedFloatingActionButton(
            onClick = { showCreateDialog = true },
            containerColor = palette.primaryButton,
            contentColor = palette.primaryButtonText,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Account, null, modifier = Modifier.size(18.dp))
                Text("New Account", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
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
    val isAdmin = account.role.equals("admin", ignoreCase = true)

    val badgeBg = if (isAdmin) palette.errorBackground else palette.primaryButton.copy(alpha = 0.12f)
    val badgeTextColor = if (isAdmin) palette.errorText else palette.primaryButton

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = palette.cardSurface,
        border = BorderStroke(
            1.dp,
            brush = Brush.verticalGradient(
                colors = listOf(
                    palette.cardStroke.copy(alpha = 0.6f),
                    palette.cardStroke.copy(alpha = 0.1f)
                )
            )
        ),
        shadowElevation = 2.dp,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        account.full_name,
                        fontWeight = FontWeight.Bold,
                        color = palette.title,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        letterSpacing = (-0.2).sp
                    )
                    Text(
                        "@${account.username}",
                        color = palette.muted,
                        fontSize = 13.sp
                    )
                }
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = badgeBg
                ) {
                    Text(
                        account.role.uppercase(),
                        color = badgeTextColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminActionButton("View",    Visibility,    Modifier.weight(1f))
                AdminActionButton("Ban",     Gavel,         Modifier.weight(1f))
                AdminActionButton("Disable", Close, Modifier.weight(1f))
                AdminActionButton("Delete",  Delete,        Modifier.weight(1f), dangerous = true)
            }
        }
    }
}

@Composable
private fun AdminActionButton(
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    dangerous: Boolean = false
) {
    val palette = humbankPalette()
    TextButton(
        onClick = {},
        modifier = modifier.height(38.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.textButtonColors(
            containerColor = if (dangerous) palette.errorBackground else palette.inputFillUnfocused,
            contentColor = if (dangerous) palette.errorText else palette.subtitle
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(13.dp)
            )
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
