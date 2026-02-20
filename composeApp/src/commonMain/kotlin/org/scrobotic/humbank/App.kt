package org.scrobotic.humbank

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.burnoo.compose.remembersetting.rememberStringSetting
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.scrobotic.humbank.NetworkClient.ApiRepository
import org.scrobotic.humbank.NetworkClient.ApiRepositoryImpl
import org.scrobotic.humbank.NetworkClient.ApiServiceImpl
import org.scrobotic.humbank.NetworkClient.createNetworkClient
import org.scrobotic.humbank.data.Transaction
import org.scrobotic.humbank.data.UserSession
import org.scrobotic.humbank.domain.Language
import org.scrobotic.humbank.domain.Localization
import org.scrobotic.humbank.screens.AdminPanelScreen
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


@OptIn(ExperimentalTime::class)
@Composable
fun App(navigator: Navigator, database: Database) {

    val scope = rememberCoroutineScope()

    val httpClient = createNetworkClient()



    var token by rememberStringSetting("token", "")
    var username by rememberStringSetting("username", "")

    var userSession: UserSession? by remember {
        mutableStateOf(
            if (token.isNotEmpty() && username.isNotEmpty()) {
                UserSession(token = token, username = username)
            } else {
                null
            }
        )
    }




    val apiService = ApiServiceImpl(
        httpClient = httpClient,
        baseUrl = "https://humbank.cv"
    )

    val apiRepository: ApiRepository = ApiRepositoryImpl(apiService)

    val repo = AccountRepository(database)
    val transactions = remember { mutableStateListOf<Transaction>() }

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
            if (navigator.current !is Screen.Login && navigator.current !is Screen.TransactionInput && navigator.current !is Screen.AdminPanel) {
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
                    onNavigateToProfile = { username ->
                        val account = repo.getAccount(username)
                        navigator.push(Screen.Profile(receiverAccount = account))
                    },
                    onTokenInvalid = {
                        token = ""
                        username = ""

                        // Clear session
                        userSession = null

                        // Navigate to login
                        navigator.replace(Screen.Login)
                    },
                    repo = repo,
                    apiRepository = apiRepository,
                )

                Screen.UserProfile -> UserProfileScreen(
                    language = selectedLanguage,
                    onBack = { navigator.pop() },
                    account = userSession?.let { repo.getAccount(it.username) },
                    onLogout = {
                        // Clear saved credentials
                        token = ""
                        username = ""

                        // Clear session
                        userSession = null

                        // Navigate to login
                        navigator.replace(Screen.Login)

                    },
                    onAdminPanelClick = { navigator.push(Screen.AdminPanel)  }
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
                    onTransaction = { receiverAccount ->
                        userSession?.let { session ->
                            val senderAccount = repo.getAccount(session.username)
                            if (senderAccount != null) {
                                navigator.push(
                                    Screen.TransactionInput(
                                        receiverAccount = receiverAccount,
                                        senderAccount = senderAccount
                                    )
                                )
                            }
                        }
                    },
                    onBack = { navigator.pop() }

                )

                is Screen.TransactionInput -> TransactionInputScreen(
                    senderAccount = screen.senderAccount,
                    receiverAccount = screen.receiverAccount,
                    userToken = userSession?.token ?: "",
                    apiRepository = apiRepository,
                    onNavigateBack = { navigator.pop() },
                    onTransactionSuccess = {
                        // Reload transactions after successful transfer
                        scope.launch {
                            try {
                                val updatedTransactions = apiRepository.getTodaysTransactions()
                                transactions.clear()
                                transactions.addAll(updatedTransactions)

                                // Reload accounts to update balances
                                val allAccounts = apiRepository.getAllAccounts()
                                repo.syncAccounts(allAccounts)
                            } catch (e: Exception) {
                                println("Failed to reload after transfer: ${e.message}")
                            }
                        }
                    }
                )

                Screen.Login -> LoginScreen(
                    onLogin = { username, password ->
                        apiRepository.login(username, password)
                    },
                    onLoginSuccess = { session ->
                        userSession = session
                        navigator.replace(Screen.Home(session))
                    }
                )

                is Screen.AdminPanel -> {
                    var isLoading by remember { mutableStateOf(true) }
                    var error by remember { mutableStateOf<String?>(null) }

                    // Load accounts once when screen is first shown
                    LaunchedEffect(Unit) {
                        try {
                            repo.syncAccounts(apiRepository.getAllAccounts())
                            isLoading = false
                        } catch (e: Exception) {
                            error = "Failed to load accounts: ${e.message}"
                            isLoading = false
                        }
                    }

                    // Show loading or error while data loads
                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (error != null) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = error!!,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = {
                                isLoading = true
                                error = null
                            }) {
                                Text("Retry")
                            }
                        }
                    } else {
                        AdminPanelScreen(
                            apiRepository = apiRepository,
                            onBack = { navigator.pop() }
                        )
                    }
                }

            }
        }
    }
}