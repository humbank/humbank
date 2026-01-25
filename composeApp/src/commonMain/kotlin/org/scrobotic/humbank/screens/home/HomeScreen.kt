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
import org.scrobotic.humbank.data.formatCurrency
import org.scrobotic.humbank.ui.*
import kotlin.time.Instant
import humbank.composeapp.generated.resources.Res
import humbank.composeapp.generated.resources.balance_title
import humbank.composeapp.generated.resources.dashboard_title
import humbank.composeapp.generated.resources.expenses
import humbank.composeapp.generated.resources.greeting
import humbank.composeapp.generated.resources.incomes
import humbank.composeapp.generated.resources.last_transactions_title
import humbank.composeapp.generated.resources.running_chart_title
import org.jetbrains.compose.resources.stringResource
import org.scrobotic.humbank.data.Transaction
import org.scrobotic.humbank.screens.home.components.BalanceLineChart
import org.scrobotic.humbank.screens.home.components.InfoCard
import org.scrobotic.humbank.screens.home.components.TransactionRow
import org.scrobotic.humbank.ui.elements.icons.processed.Send



@Composable
fun HomeScreen(
    onNavigateToTransfer: () -> Unit,
    onNavigateToProfile: (String) -> Unit
) {
    val accountId = "scrobotic"
    var balance by remember { mutableStateOf(-1.00) }

    val transactions = remember { mutableStateListOf<Transaction>() }

    LaunchedEffect(Unit) {
        if (transactions.isEmpty()) {
            transactions.addAll(
                listOf(
                    Transaction(
                        id = "tx_001",
                        sender = "acc_999",
                        receiver = "scrobotic",
                        amount = 10000.00,
                        created = Instant.parse("2022-05-01T09:15:00Z"),
                        pureDescription = "Miete Mai",
                        currentBalance = 10000.00
                    ),

                    Transaction(
                        id = "tx_002",
                        sender = "scrobotic",
                        receiver = "acc_888",
                        amount = 75.20,
                        created = Instant.parse("2022-05-02T12:45:00Z"),
                        pureDescription = "Tankstelle",
                        currentBalance = 9924.80,
                    ),
                    Transaction(
                        id = "tx_003",
                        sender = "scrobotic",
                        receiver = "acc_555",
                        amount = 500.0,
                        created = Instant.parse("2022-05-03T08:30:00Z"),
                        pureDescription = "Rückzahlung Freund",
                        currentBalance = 9924.80 - 500.0
                    ),

                    Transaction(
                        id = "tx_004",
                        sender = "scrobotic",
                        receiver = "acc_999",
                        amount = 19.99,
                        created = Instant.parse("2022-05-04T18:20:00Z"),
                        pureDescription = "Netflix Abo",
                        currentBalance =9924.80 - 500.0 - 19.99
                    ),

                    Transaction(
                        id = "tx_005",
                        sender = "acc_222",
                        receiver = "scrobotic",
                        amount = 150.0,
                        created = Instant.parse("2022-05-05T14:10:00Z"),
                        pureDescription = "Gehalt Nebenjob",
                        currentBalance = 9924.80 - 500.0 - 19.99+ 150.0
                    ),

                    Transaction(
                        id = "tx_006",
                        sender = "scrobotic",
                        receiver = "acc_333",
                        amount = 60.0,
                        created = Instant.parse("2022-05-06T20:05:00Z"),
                        pureDescription = "Supermarkt Einkauf",
                        currentBalance = 9924.80 - 500.0 - 19.99+ 150.0- 60.0
                    ),

                    Transaction(
                        id = "tx_007",
                        sender = "acc_444",
                        receiver = "scrobotic",
                        amount = 500.0,
                        created = Instant.parse("2022-05-07T11:50:00Z"),
                        pureDescription = "Steuererstattung",
                        currentBalance = 9924.80 - 500.0 - 19.99+ 150.0- 60.0+ 500.0
                    ),

                    Transaction(
                        id = "tx_008",
                        sender = "scrobotic",
                        receiver = "acc_555",
                        amount = 120.0,
                        created = Instant.parse("2022-05-08T16:35:00Z"),
                        pureDescription = "Restaurant",
                        currentBalance = 9924.80 - 500.0 - 19.99+ 150.0- 60.0+ 500.0 - 120.0
                    ),

                    Transaction(
                        id = "tx_009",
                        sender = "acc_666",
                        receiver = "scrobotic",
                        amount = 80.0,
                        created = Instant.parse("2022-05-09T09:00:00Z"),
                        pureDescription = "Verkauf Kleidung",
                        currentBalance = 9924.80 - 500.0 - 19.99+ 150.0- 60.0+ 500.0 - 120.0 + 80.0
                    ),

                    Transaction(
                        id = "tx_010",
                        sender = "scrobotic",
                        receiver = "acc_777",
                        amount = 35.50,
                        created = Instant.parse("2022-05-10T13:25:00Z"),
                        pureDescription = "Café Besuch",
                        currentBalance =  9924.80 - 500.0 - 19.99+ 150.0- 60.0+ 500.0 - 120.0 + 80.0 - 35.50
                    )





                )
            )
        }
        balance = transactions[transactions.size-1].currentBalance
    }



    val incomingSum by remember {
        derivedStateOf {
            transactions.filter { it.receiver == accountId }.sumOf { it.amount }
        }
    }
    val outgoingSum by remember {
        derivedStateOf {
            transactions.filter { it.sender == accountId }.sumOf { it.amount }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(Res.string.dashboard_title), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
                    Text("${stringResource(Res.string.greeting)}Cornelius Binder", color = Gray, fontSize = 14.sp)
                }
                IconButton(onClick = onNavigateToTransfer) {
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
                    Text("${balance.formatCurrency()} HMB", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text("Account ID: $accountId", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.padding(top = 8.dp))
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
            BalanceLineChart(transactions, accountId, balance)
        }


        item {
            Text(stringResource(Res.string.last_transactions_title), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(top = 8.dp))
        }

        items(transactions.take(20)) { tx ->
            TransactionRow(tx, accountId)
        }
    }
}


