package org.scrobotic.humbank.screens.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.scrobotic.humbank.data.formatCurrency
import org.scrobotic.humbank.ui.*
import humbank.composeapp.generated.resources.Res
import humbank.composeapp.generated.resources.balance_title
import humbank.composeapp.generated.resources.dashboard_title
import humbank.composeapp.generated.resources.expenses
import humbank.composeapp.generated.resources.greeting
import humbank.composeapp.generated.resources.incomes
import humbank.composeapp.generated.resources.last_transactions_title
import humbank.composeapp.generated.resources.networth
import humbank.composeapp.generated.resources.running_chart_title
import org.jetbrains.compose.resources.stringResource
import org.scrobotic.humbank.data.Transaction
import org.scrobotic.humbank.screens.home.components.BalanceLineChart
import org.scrobotic.humbank.screens.home.components.InfoCard
import org.scrobotic.humbank.screens.home.components.TransactionDetailContent
import org.scrobotic.humbank.screens.home.components.TransactionRow
import org.scrobotic.humbank.ui.elements.icons.processed.Send
import org.scrobotic.humbank.AccountRepository
import org.scrobotic.humbank.NetworkClient.ApiRepository
import org.scrobotic.humbank.data.AllAccount
import org.scrobotic.humbank.data.UserSession
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun HomeScreen(
    userSession: UserSession,
    contentPadding: PaddingValues,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToTransfer: () -> Unit,
    onTokenInvalid: () -> Unit,
    repo: AccountRepository,
    apiRepository: ApiRepository
) {
    var account by remember { mutableStateOf<AllAccount?>(null) }
    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currentBalance by remember { mutableStateOf(0.0) }


    var showAllTransactions by remember { mutableStateOf(false) }

    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // Load data on first composition
    LaunchedEffect(userSession.username) {
        try {
            isLoading = true
            errorMessage = null

            val isTokenValid = apiRepository.validateToken()
            if (!isTokenValid) {
                onTokenInvalid()
            }

            // Sync accounts from API
            val allAccounts = apiRepository.getAllAccounts()
            repo.syncAccounts(allAccounts)


            account = repo.getAccount(userSession.username)
            println("DEBUG: Found account: ${account?.username}")


            transactions = apiRepository.getTodaysTransactions()
                .sortedByDescending { it.transaction_date }
            println("DEBUG: Loaded ${transactions.size} transactions")

            isLoading = false
        } catch (e: Exception) {
            errorMessage = "Failed to load data: ${e.message}"
            isLoading = false
            e.printStackTrace()
        }
    }

    LaunchedEffect(account) {
        if (account != null) {
            try {
                currentBalance = apiRepository.getBalance()
                println("DEBUG: Current balance loaded: $currentBalance")
            } catch (e: Exception) {
                println("DEBUG: Failed to load balance")
            }
        }
    }

    // Show loading state
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading...", color = MaterialTheme.colorScheme.onBackground)
            }
        }
        return
    }

    // Show error state
    if (errorMessage != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    text = "Error",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage ?: "Unknown error",
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    scope.launch {
                        isLoading = true
                        errorMessage = null
                        try {
                            val allAccounts = apiRepository.getAllAccounts()
                            repo.syncAccounts(allAccounts)
                            account = repo.getAccount(userSession.username)
                            transactions = apiRepository.getTodaysTransactions()
                                .sortedByDescending { it.transaction_date }
                            isLoading = false
                        } catch (e: Exception) {
                            errorMessage = "Failed to load data: ${e.message}"
                            isLoading = false
                        }
                    }
                }) {
                    Text("Retry")
                }
            }
        }
        return
    }

    // Show account not found state
    if (account == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Account not found",
                color = MaterialTheme.colorScheme.error,
                fontSize = 18.sp
            )
        }
        return
    }

    // Calculate financial metrics
    val incomingSum = transactions
        .filter { it.receiver == account?.username }
        .sumOf { it.amount }

    val outgoingSum = transactions
        .filter { it.sender == account?.username }
        .sumOf { it.amount }

    val networthSum = incomingSum - outgoingSum

    val transactionsToShow = if (showAllTransactions) {
        transactions
    } else {
        transactions.take(5)
    }
    // Main content
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = contentPadding.calculateTopPadding() + 16.dp,
                    bottom = contentPadding.calculateBottomPadding() + 16.dp
                )
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.dashboard_title),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${stringResource(Res.string.greeting)}${account?.full_name}",
                        color = Gray,
                        fontSize = 14.sp
                    )
                }
                // UPDATED: Navigate to transfer screen
                IconButton(onClick = onNavigateToTransfer) {
                    Icon(Send, contentDescription = "Transfer", tint = Hannes_Gray)
                }
            }
        }

        // Balance Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(Res.string.balance_title),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${currentBalance.formatCurrency()} HMB",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Account ID: ${account?.username}",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoCard(
                        label = stringResource(Res.string.networth),
                        value = "${if (networthSum > 0) "+" else ""}${networthSum.formatCurrency()} HMB",
                        color = if (networthSum > 0) GreenStart else Pink40,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Income/Expense Cards
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoCard(
                    label = stringResource(Res.string.incomes),
                    value = "+${incomingSum.formatCurrency()} HMB",
                    color = GreenStart,
                    modifier = Modifier.weight(1f)
                )
                InfoCard(
                    label = stringResource(Res.string.expenses),
                    value = "-${outgoingSum.formatCurrency()} HMB",
                    color = Pink40,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Balance Chart
        item {
            Text(
                text = stringResource(Res.string.running_chart_title),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            BalanceLineChart(
                transactions.take(5),
                accountId = account?.username ?: "",
                currentBalance = currentBalance
            )
        }

        // Transactions List
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(Res.string.last_transactions_title),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (transactions.size > 5) {
                        Text(
                            text = "Showing ${transactionsToShow.size} of ${transactions.size}",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }

                if (transactions.size > 5) {
                    TextButton(
                        onClick = { showAllTransactions = !showAllTransactions }
                    ) {
                        Text(
                            text = if (showAllTransactions) "Show Less" else "Show More",
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        items(transactionsToShow) { tx ->
            TransactionRow(
                tx = tx,
                accountId = account?.username,
                onClick = { selectedTransaction = tx }
            )
        }
    }

    // Transaction Detail Bottom Sheet
    selectedTransaction?.let { tx ->
        ModalBottomSheet(
            onDismissRequest = { selectedTransaction = null },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.background
        ) {
            TransactionDetailContent(
                accountId = account?.username,
                transaction = tx,
                onClose = { selectedTransaction = null }
            )
        }
    }
}