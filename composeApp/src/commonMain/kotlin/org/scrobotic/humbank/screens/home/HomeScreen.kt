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
import dev.burnoo.compose.remembersetting.rememberStringSetting
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
import org.scrobotic.humbank.data.Account
import org.scrobotic.humbank.data.Transaction
import org.scrobotic.humbank.screens.home.components.BalanceLineChart
import org.scrobotic.humbank.screens.home.components.InfoCard
import org.scrobotic.humbank.screens.home.components.TransactionDetailContent
import org.scrobotic.humbank.screens.home.components.TransactionRow
import org.scrobotic.humbank.ui.elements.icons.processed.Send
import org.scrobotic.humbank.AccountRepository
import org.scrobotic.humbank.NetworkClient.ApiRepository
import org.scrobotic.humbank.NetworkClient.ApiService
import org.scrobotic.humbank.data.AllAccount
import org.scrobotic.humbank.data.UserSession
import org.scrobotic.humbank.data.generateRandomId
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun HomeScreen(
    userSession: UserSession,
    contentPadding: PaddingValues,
    onNavigateToProfile: (String) -> Unit,
    repo: AccountRepository,
    apiRepository: ApiRepository
) {
    var account by remember { mutableStateOf<AllAccount?>(null) }

    LaunchedEffect(Unit) {
        println(apiRepository.getTodaysTransactions())
        repo.syncAccounts(apiRepository.getAllAccounts())
        account = repo.getAccount(userSession.username)
    }

// Show loading until account is ready
    if (account == null) {
        CircularProgressIndicator()
    } else {
        // Use account here





        var username by rememberStringSetting("account", account!!.username)
        var balance by remember { mutableStateOf("b") }

        var sel_tx by remember { mutableStateOf<Transaction?>(null) }
        val sheetState = rememberModalBottomSheetState()

        var showInputPopup by remember { mutableStateOf(false) }

        val transactions = remember { mutableStateListOf<Transaction>() }

        var token by rememberStringSetting("token", "")

        token = userSession.token



        LaunchedEffect(Unit) {
            //transactions.addAll(apiService.getTodaysTransactions())


        }



        val incomingSum by remember {
            derivedStateOf {
                transactions.filter { it.receiver == username }.sumOf { it.amount }
            }
        }
        val outgoingSum by remember {
            derivedStateOf {
                transactions.filter { it.sender == username }.sumOf { it.amount }
            }
        }

        val networthSum by remember{
            derivedStateOf {
                incomingSum - outgoingSum
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = contentPadding.calculateTopPadding() + 16.dp,
                    bottom = contentPadding.calculateBottomPadding() + 16.dp)),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(stringResource(Res.string.dashboard_title), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
                        Text("${stringResource(Res.string.greeting)}${account?.full_name}", color = Gray, fontSize = 14.sp)
                    }
                    IconButton(onClick = { showInputPopup = true }) {
                        Icon(Send, contentDescription = "Transfer", tint = Hannes_Gray)
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(stringResource(Res.string.balance_title), color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp)
                        //Text("${balance.formatCurrency()} HMB", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        Text("Account ID: $username", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.padding(top = 8.dp))
                        InfoCard(stringResource(Res.string.networth), "${if(networthSum > 0) "+" else ""}$networthSum HMB", if(networthSum > 0) GreenStart else Pink40, Modifier.weight(1f))
                    }

                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    InfoCard(stringResource(Res.string.incomes), "+${incomingSum.formatCurrency()} HMB", GreenStart, Modifier.weight(1f))
                    InfoCard(stringResource(Res.string.expenses), "-${outgoingSum.formatCurrency()} HMB", Pink40, Modifier.weight(1f))

                }
            }


            item {
                Text(stringResource(Res.string.running_chart_title), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(bottom = 8.dp))
                BalanceLineChart(transactions, username, 4.0)
            }


            item {
                Text(stringResource(Res.string.last_transactions_title), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(top = 8.dp))
            }

            items(transactions.take(20)) { tx ->
                TransactionRow(tx, username,
                    onClick = {sel_tx = tx})
            }
        }

        if(sel_tx != null){
            ModalBottomSheet(
                onDismissRequest = { sel_tx = null },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.background
            ) {
                TransactionDetailContent(
                    accountId = username,
                    transaction = sel_tx!!,
                    onClose = { sel_tx = null }
                )
            }

        }

        if (showInputPopup) {
            showInputPopup = false
        }
//        TransactionInputPopup(
//            account = account,
//            onDismiss = { showInputPopup = false },
//            onTransactionCreated = { newTx ->
//                // Add to the START of the list (index 0) so it appears at the top
//
//            }
//        )
//    }
    }

}


