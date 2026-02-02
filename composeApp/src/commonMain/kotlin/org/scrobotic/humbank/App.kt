package org.scrobotic.humbank

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import dev.burnoo.compose.remembersetting.rememberStringSetting
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.scrobotic.humbank.NetworkClient.ApiRepository
import org.scrobotic.humbank.NetworkClient.ApiRepositoryImpl
import org.scrobotic.humbank.NetworkClient.ApiServiceImpl
import org.scrobotic.humbank.NetworkClient.createNetworkClient
import org.scrobotic.humbank.data.Account
import org.scrobotic.humbank.data.Transaction
import org.scrobotic.humbank.data.UserSession
import org.scrobotic.humbank.data.generateRandomId
import org.scrobotic.humbank.domain.Language
import org.scrobotic.humbank.domain.Localization
import org.scrobotic.humbank.screens.LoginScreen
import org.scrobotic.humbank.screens.home.HomeScreen
import org.scrobotic.humbank.screens.Navigator
import org.scrobotic.humbank.screens.ProfileScreen
import org.scrobotic.humbank.screens.Screen
import org.scrobotic.humbank.screens.SettingsScreen
import org.scrobotic.humbank.screens.UserProfileScreen
import org.scrobotic.humbank.screens.SearchScreen
import org.scrobotic.humbank.screens.TransactionInputScreen
import org.scrobotic.humbank.ui.HumbankUITheme
import org.scrobotic.humbank.ui.elements.navigation.BottomNavigationBar
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@OptIn(ExperimentalTime::class)
@Composable
@Preview
fun App(navigator: Navigator, database: Database) {

    val scope = rememberCoroutineScope()

    val httpClient = createNetworkClient()

    var userSession: UserSession? by remember { mutableStateOf(null) }



    val apiService = ApiServiceImpl(
        httpClient = httpClient,
        baseUrl = "https://humbank.cv"
    )

    val apiRepository: ApiRepository =
        ApiRepositoryImpl(apiService)

    val repo = AccountRepository(database)
    val transactions = remember { mutableStateListOf<Transaction>() }

    repo.syncAccounts(
        listOf(
        Account(
            username = "user1",
            fullName = "Cornelius Binder",
            balance = 67.0,
            role = "Admin"
        )
        )
    )



    LaunchedEffect(Unit) {
        if (transactions.isEmpty()) {
            transactions.addAll(
                listOf(
                    Transaction(
                        id = "tx_${generateRandomId()}",
                        sender = "scrobotic",
                        receiver = "acc_777",
                        amount = 35.50,
                        created = Instant.parse("2022-05-10T13:25:00Z"),
                        pureDescription = "Café Besuch",
                        currentBalance = 3389.31 - 35.50 // 3353.81
                    ),
                    Transaction(
                        id = "tx_${generateRandomId()}",
                        sender = "acc_666",
                        receiver = "scrobotic",
                        amount = 80.0,
                        created = Instant.parse("2022-05-09T09:00:00Z"),
                        pureDescription = "Verkauf Kleidung",
                        currentBalance = 3309.31 + 80.0 // 3389.31
                    ),
                    Transaction(
                        id = "tx_${generateRandomId()}",
                        sender = "scrobotic",
                        receiver = "acc_555",
                        amount = 120.0,
                        created = Instant.parse("2022-05-08T16:35:00Z"),
                        pureDescription = "Restaurant",
                        currentBalance = 3429.31 - 120.0 // 3309.31
                    ),
                    Transaction(
                        id = "tx_${generateRandomId()}",
                        sender = "acc_444",
                        receiver = "scrobotic",
                        amount = 500.0,
                        created = Instant.parse("2022-05-07T11:50:00Z"),
                        pureDescription = "Steuererstattung",
                        currentBalance = 2929.31 + 500.0 // 3429.31
                    ),
                    Transaction(
                        id = "tx_${generateRandomId()}",
                        sender = "scrobotic",
                        receiver = "acc_333",
                        amount = 60.0,
                        created = Instant.parse("2022-05-06T20:05:00Z"),
                        pureDescription = "Supermarkt Einkauf",
                        currentBalance = 2989.31 - 60.0 // 2929.31
                    ),
                    Transaction(
                        id = "tx_${generateRandomId()}",
                        sender = "acc_222",
                        receiver = "scrobotic",
                        amount = 150.0,
                        created = Instant.parse("2022-05-05T14:10:00Z"),
                        pureDescription = "Gehalt Nebenjob",
                        currentBalance = 2839.31 + 150.0 // 2989.31
                    ),
                    Transaction(
                        id = "tx_${generateRandomId()}",
                        sender = "scrobotic",
                        receiver = "acc_999",
                        amount = 19.99,
                        created = Instant.parse("2022-05-04T18:20:00Z"),
                        pureDescription = "Netflix Abo",
                        currentBalance = 2859.30 - 19.99 // 2839.31
                    ),
                    Transaction(
                        id = "tx_${generateRandomId()}",
                        sender = "scrobotic",
                        receiver = "acc_555",
                        amount = 500.0,
                        created = Instant.parse("2022-05-03T08:30:00Z"),
                        pureDescription = "Rückzahlung Freund",
                        currentBalance = 3359.30 - 500.0 // 2859.30
                    ),
                    Transaction(
                        id = "tx_${generateRandomId()}",
                        sender = "scrobotic",
                        receiver = "acc_888",
                        amount = 75.20,
                        created = Instant.parse("2022-05-02T12:45:00Z"),
                        pureDescription = "Tankstelle",
                        currentBalance = 3434.50 - 75.20 // 3359.30
                    ),
                    Transaction(
                        id = "tx_${generateRandomId()}",
                        sender = "scrobotic",
                        receiver = "acc_999",
                        amount = 565.50,
                        created = Instant.parse("2022-05-01T15:00:00Z"),
                        pureDescription = "Miete Mai",
                        currentBalance = 4000.00 - 565.50 // 3434.50
                    ),
                    Transaction(
                        id = "tx_${generateRandomId()}",
                        sender = "SYSTEM",
                        receiver = "scrobotic",
                        amount = 4000.00,
                        created = Instant.parse("2022-05-01T09:00:00Z"),
                        pureDescription = "Anfangssaldo & Bonus",
                        currentBalance = 0.0 + 4000.00 // Start balance
                    )


                )
            )
        }
    }


    HumbankUITheme {
        val localization = koinInject<Localization>()
        var languageIso by rememberStringSetting(
            key = "savedLanguageIso",
            defaultValue = Language.German.iso
        )

        localization.applyLanguage(languageIso)

        val selectedLanguage by derivedStateOf {
            Language.entries.first { it.iso == languageIso }
        }



        Scaffold(bottomBar = {
            if (navigator.current !is Screen.Login) {
                BottomNavigationBar(
                    onHomeClicked = {
                        userSession?.let {
                            navigator.replace(Screen.Home(it))
                        }
                    },
                    onSettingsClicked = { navigator.push(Screen.Settings) },
                    onNotificationsClicked = { navigator.push(Screen.Search) },
                    onAccountClicked = { navigator.push(Screen.UserProfile) }
                )
            }
        }) { innerPadding ->
            when (val screen = navigator.current) {

                is Screen.Home -> HomeScreen(
                    userSession = screen.userSession,
                    contentPadding = innerPadding,
                    onNavigateToTransfer = {
                        scope.launch {
                            try {
                                val accounts = apiService.getAllAccounts()
                                println(accounts)
                                // Do something with accounts (e.g., navigate or update state)
                            } catch (e: Exception) {
                                // Handle the error (e.g., show a Snackbar)
                            }
                        }
                    },
                    onNavigateToProfile = { },
                    repo = repo
                )

                Screen.UserProfile -> UserProfileScreen(
                    language = selectedLanguage,
                    onBack = { navigator.pop() },
                    account = repo.getAccount("scrobotic")
                )

                Screen.Settings -> SettingsScreen(
                    language = selectedLanguage,
                    onLanguageChange = { selectedLanguage ->
                        languageIso = selectedLanguage.iso
                        localization.applyLanguage(languageIso)
                    },

                    onBack = { navigator.pop() }
                )

                Screen.Search -> SearchScreen(
                    repository = repo,
                    onNavigateToAccount = { username ->
                        navigator.push(Screen.Profile(receiverAccount = repo.getAccount(username)))
                    }
                )

                is Screen.Profile -> ProfileScreen(
                    receiverAccount = screen.receiverAccount,
                    onTransaction = { receiverAccount->
                        navigator.push(Screen.TransactionInput(receiverAccount = receiverAccount, senderAccount = repo.getAccount(
                            userSession?.username ?: ""
                        )))
                    },
                    onBack = { navigator.pop() })

                is Screen.TransactionInput -> TransactionInputScreen(
                    senderAccount =screen.senderAccount,
                    receiverAccount = screen.receiverAccount,
                    onNavigateBack = { navigator.pop() },
                    onTransactionCreated = { tx ->
                        transactions.add(tx)
                    }
                )

                Screen.Login -> LoginScreen(
                    onLogin = { username, password ->
                        apiRepository.login(username, password)
                    },
                    onLoginSuccess = { session ->
                        navigator.push(Screen.Home(session))
                    }
                )
            }
        }

    }
}