package org.scrobotic.humbank.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.humbank.ktorclient.icons.imagevectors.Account
import org.humbank.ktorclient.icons.imagevectors.CarCrash
import org.humbank.ktorclient.icons.imagevectors.CreditCard
import org.scrobotic.humbank.NetworkClient.ApiRepository
import org.scrobotic.humbank.data.AllAccount
import org.scrobotic.humbank.ui.Gray
import org.scrobotic.humbank.ui.elements.icons.processed.AccountCircleOff
import org.scrobotic.humbank.ui.elements.icons.processed.ArrowBack
import org.scrobotic.humbank.ui.elements.icons.processed.ArrowDownward
import org.scrobotic.humbank.ui.elements.icons.processed.Close
import org.scrobotic.humbank.ui.elements.icons.processed.Delete
import org.scrobotic.humbank.ui.elements.icons.processed.Gavel
import org.scrobotic.humbank.ui.elements.icons.processed.Visibility

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    apiRepository: ApiRepository,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var accounts by remember { mutableStateOf<List<AllAccount>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }

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
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search users") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true
            )

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                filteredAccounts.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No matching users" else "No users found",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserAdminCard(account: AllAccount) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = account.full_name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "@${account.username}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }


                Surface(
                    modifier = Modifier,
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = account.role.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminActionButton(
                    icon = Visibility,
                    label = "View",
                    onClick = { println("TODO: View profile ${account.username}") }
                )
                AdminActionButton(
                    icon = Gavel,
                    label = "Ban",
                    onClick = { println("TODO: Ban ${account.username}") }
                )
                AdminActionButton(
                    icon = AccountCircleOff,
                    label = "Disable",
                    onClick = { println("TODO: Disable ${account.username}") }
                )
                AdminActionButton(
                    icon = Delete,
                    label = "Delete",
                    isDangerous = true,
                    onClick = { println("TODO: Delete ${account.username}") }
                )
            }
        }
    }
}

@Composable
private fun AdminActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    isDangerous: Boolean = false
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.height(40.dp),
        colors = ButtonDefaults.textButtonColors(
            containerColor = if (isDangerous) MaterialTheme.colorScheme.errorContainer
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            contentColor = if (isDangerous) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.primary
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
            Text(label, fontSize = MaterialTheme.typography.labelSmall.fontSize)
        }
    }
}
