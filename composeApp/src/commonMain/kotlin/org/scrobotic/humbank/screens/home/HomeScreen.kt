package org.scrobotic.humbank.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import humbank.composeapp.generated.resources.home_try_again
import humbank.composeapp.generated.resources.home_transfer_success
import humbank.composeapp.generated.resources.home_transfer_failed_check_username
import humbank.composeapp.generated.resources.home_send
import humbank.composeapp.generated.resources.home_no_transactions
import humbank.composeapp.generated.resources.home_loading
import humbank.composeapp.generated.resources.home_error_title
import humbank.composeapp.generated.resources.account_not_found
import kotlinx.coroutines.isActive
import org.jetbrains.compose.resources.stringResource
import org.scrobotic.humbank.data.Transaction
import org.scrobotic.humbank.screens.home.components.BalanceLineChart
import org.scrobotic.humbank.screens.home.components.InfoCard
import org.scrobotic.humbank.screens.home.components.TransactionDetailContent
import org.scrobotic.humbank.screens.home.components.TransactionRow
import org.scrobotic.humbank.ui.elements.icons.processed.Send
import org.scrobotic.humbank.AccountRepository
import org.scrobotic.humbank.NetworkClient.ApiRepository
import org.scrobotic.humbank.NetworkClient.NetworkResult
import org.scrobotic.humbank.data.AllAccount
import org.scrobotic.humbank.data.UserSession
import org.scrobotic.humbank.data.generateRandomId
import org.scrobotic.humbank.screens.home.components.TransactionInputPopup
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun HomeScreen(
    userSession: UserSession,
    contentPadding: PaddingValues,
    onNavigateToProfile: (String) -> Unit,
    onTokenInvalid: () -> Unit,
    repo: AccountRepository,
    apiRepository: ApiRepository
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val palette = humbankPalette()

    var account by remember { mutableStateOf<AllAccount?>(null) }
    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currentBalance by remember { mutableStateOf(0.0) }

    var showTransactionDialog by remember { mutableStateOf(false) }
    var showAllTransactions by remember { mutableStateOf(false) }
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }
    val sheetState = rememberModalBottomSheetState()
    var isTransactionLoading by remember { mutableStateOf(false) }
    var refreshTrigger by remember { mutableIntStateOf(0) }
    var isRefreshing by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()


    val transfer_success = stringResource(Res.string.home_transfer_success)
    val transfer_fail = stringResource(Res.string.home_transfer_failed_check_username)


    LaunchedEffect(refreshTrigger) {
        try {
            if (refreshTrigger > 0) isTransactionLoading = true
            if (refreshTrigger > 0) isRefreshing = true
            val balanceResult = apiRepository.getBalance()
            if (!coroutineContext.isActive) return@LaunchedEffect
            val transactionsResult = apiRepository.getTodaysTransactions()
            if (!coroutineContext.isActive) return@LaunchedEffect
            currentBalance = balanceResult
            transactions = transactionsResult.sortedByDescending { it.transaction_date }
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
        } finally {
            if (coroutineContext.isActive) isTransactionLoading = false
            if (coroutineContext.isActive) isRefreshing = false
        }
    }

    LaunchedEffect(userSession.username) {
        try {
            isLoading = true
            errorMessage = null
            val isTokenValid = apiRepository.validateToken()
            if (!isTokenValid) { onTokenInvalid() }
            val latestTime = repo.getLatestTime()
            val updatedAccounts = if (latestTime != null) {
                apiRepository.updateAccounts(latestTime)
            } else {
                apiRepository.getAllAccounts()
            }
            repo.syncAccounts(updatedAccounts)
            account = repo.getAccount(userSession.username)
            transactions = apiRepository.getTodaysTransactions().sortedByDescending { it.transaction_date }
            isLoading = false
        } catch (e: Exception) {
            errorMessage = "Failed to load data: ${e.message}"
            isLoading = false
            e.printStackTrace()
        }
    }

    LaunchedEffect(account) {
        if (account != null) {
            try { currentBalance = apiRepository.getBalance() } catch (e: Exception) {}
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = palette.primaryButton, strokeWidth = 2.5.dp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(Res.string.home_loading), color = palette.muted, fontSize = 14.sp)
            }
        }
        return
    }

    if (errorMessage != null) {
        Box(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Text(stringResource(Res.string.home_error_title), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = palette.title)
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMessage ?: "Unknown error", color = palette.muted, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            errorMessage = null
                            try {
                                account = repo.getAccount(userSession.username)
                                transactions = apiRepository.getTodaysTransactions().sortedByDescending { it.transaction_date }
                                isLoading = false
                            } catch (e: Exception) {
                                errorMessage = "Failed to load data: ${e.message}"
                                isLoading = false
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = palette.primaryButton, contentColor = palette.primaryButtonText)
                ) {
                    Text(stringResource(Res.string.home_try_again))
                }
            }
        }
        return
    }

    if (account == null) {
        Box(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(Res.string.account_not_found), color = palette.errorText, fontSize = 16.sp)
        }
        return
    }

    val incomingSum = transactions.filter { it.receiver == account!!.username }.sumOf { it.amount }
    val outgoingSum = transactions.filter { it.sender == account!!.username }.sumOf { it.amount }
    val networthSum = incomingSum - outgoingSum
    val transactionsToShow = if (showAllTransactions) transactions else transactions.take(5)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(palette.gradientTop, palette.gradientMiddle, palette.gradientBottom)
                )
            )
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                refreshTrigger++
            },
            modifier = Modifier.fillMaxSize(),
            state = pullToRefreshState,
            indicator = {
                PullToRefreshDefaults.Indicator(
                    state = pullToRefreshState,
                    isRefreshing = isRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter),
                    containerColor = palette.cardSurface,
                    color = palette.primaryButton
                )
            }
        ) {
            // Your content

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = contentPadding.calculateTopPadding() + 8.dp,
            bottom = contentPadding.calculateBottomPadding() + 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.dashboard_title),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = palette.title,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "${stringResource(Res.string.greeting)} ${account!!.full_name}",
                        color = palette.muted,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                // Send button — elevated circular FAB style
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(palette.primaryButton, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { showTransactionDialog = true },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            Send,
                            contentDescription = stringResource(Res.string.home_send),
                            tint = palette.primaryButtonText,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Premium balance card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = Color.Transparent,
                border = BorderStroke(
                    1.dp,
                    Brush.verticalGradient(
                        colors = listOf(
                            palette.cardStroke.copy(alpha = 0.9f),
                            palette.cardStroke.copy(alpha = 0.1f)
                        )
                    )
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(palette.cardSurface, palette.cardSurface.copy(alpha = 0.9f))
                            ),
                            RoundedCornerShape(28.dp)
                        )
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = stringResource(Res.string.balance_title).uppercase(),
                            color = palette.muted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = currentBalance.formatCurrency(),
                                fontSize = 40.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = palette.title,
                                letterSpacing = (-1).sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "HMB",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = palette.muted,
                                modifier = Modifier.padding(bottom = 6.dp),
                                letterSpacing = 1.sp
                            )
                        }

                        Text(
                            text = "@${account!!.username}",
                            color = palette.muted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Net worth pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (networthSum >= 0) Color(0xFF22C55E).copy(alpha = 0.12f) else Color(0xFFF43F5E).copy(alpha = 0.12f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = stringResource(Res.string.networth).uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = palette.muted,
                                    letterSpacing = 0.8.sp
                                )
                                Text(
                                    text = "${if (networthSum > 0) "+" else ""}${networthSum.formatCurrency()} HMB",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (networthSum >= 0) Color(0xFF22C55E) else Color(0xFFF43F5E)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Income / Expense summary
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoCard(
                    label = stringResource(Res.string.incomes),
                    value = "+${incomingSum.formatCurrency()} HMB",
                    color = Color(0xFF22C55E),
                    modifier = Modifier.weight(1f)
                )
                InfoCard(
                    label = stringResource(Res.string.expenses),
                    value = "−${outgoingSum.formatCurrency()} HMB",
                    color = Color(0xFFF43F5E),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Balance chart section
        item {
            Column {
                Text(
                    text = stringResource(Res.string.running_chart_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = palette.title,
                    letterSpacing = (-0.2).sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                BalanceLineChart(
                    transactions.take(5),
                    accountId = account!!.username,
                    currentBalance = currentBalance
                )
            }
        }

        // Transactions header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(Res.string.last_transactions_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = palette.title,
                        letterSpacing = (-0.2).sp
                    )
                    if (transactions.size > 5) {
                        Text(
                            text = "${transactionsToShow.size} of ${transactions.size} shown",
                            color = palette.muted,
                            fontSize = 12.sp
                        )
                    }
                }
                if (transactions.size > 5) {
                    TextButton(
                        onClick = { showAllTransactions = !showAllTransactions },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.textButtonColors(contentColor = palette.primaryButton)
                    ) {
                        Text(
                            text = if (showAllTransactions) "Show less" else "See all",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Transaction list inside a single card
        item {
            if (transactionsToShow.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(palette.cardSurface)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(Res.string.home_no_transactions), color = palette.muted, fontSize = 14.sp)
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = palette.cardSurface,
                    border = BorderStroke(
                        1.dp,
                        Brush.verticalGradient(
                            colors = listOf(
                                palette.cardStroke.copy(alpha = 0.6f),
                                palette.cardStroke.copy(alpha = 0.1f)
                            )
                        )
                    ),
                    shadowElevation = 4.dp,
                    tonalElevation = 0.dp
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        transactionsToShow.forEachIndexed { index, tx ->
                            TransactionRow(
                                tx = tx,
                                accountId = account!!.username,
                                onClick = { selectedTransaction = tx }
                            )
                            if (index < transactionsToShow.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = palette.cardStroke.copy(alpha = 0.35f),
                                    thickness = 0.5.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Snackbar overlaid on the gradient Box
    Box(modifier = Modifier.fillMaxSize().padding(contentPadding)) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
    } // end PullToRefreshBox
    } // end gradient Box

    // Transaction detail bottom sheet
    selectedTransaction?.let { tx ->
        ModalBottomSheet(
            onDismissRequest = { selectedTransaction = null },
            sheetState = sheetState,
            containerColor = palette.panel,
            dragHandle = null,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            TransactionDetailContent(
                accountId = account!!.username,
                transaction = tx,
                onClose = { selectedTransaction = null }
            )
        }
    }

    // Transfer dialog
    if (showTransactionDialog && account != null) {
        isTransactionLoading = false
        TransactionInputPopup(
            balance = currentBalance,
            senderAccount = account!!,
            isLoading = isTransactionLoading,
            onDismiss = { showTransactionDialog = false },
            onSend = { amt, receiver, desc ->
                isTransactionLoading = true
                scope.launch {
                    val result = apiRepository.executeTransfer(
                        issuerUsername = receiver,
                        amount = amt,
                        transactionId = "tx_${generateRandomId()}",
                        description = desc
                    )
                    showTransactionDialog = false
                    if (result) {
                        snackbarHostState.showSnackbar(transfer_success)
                        refreshTrigger++
                    } else {
                        snackbarHostState.showSnackbar(transfer_fail)
                    }
                }
            }
        )
    }
}
