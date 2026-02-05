package org.scrobotic.humbank

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import dev.burnoo.compose.remembersetting.rememberStringSetting
import org.koin.compose.koinInject
import org.scrobotic.humbank.NetworkClient.ApiRepository
import org.scrobotic.humbank.NetworkClient.ApiRepositoryImpl
import org.scrobotic.humbank.NetworkClient.ApiServiceImpl
import org.scrobotic.humbank.NetworkClient.createNetworkClient
import org.scrobotic.humbank.data.Transaction
import org.scrobotic.humbank.data.UserSession
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





//    LaunchedEffect(Unit) {
//        scope.launch { println(apiRepository.getTodaysTransactions()) }
//    }


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
                    onNavigateToProfile = { },
                    repo = repo,
                    apiRepository = apiRepository
                )

                Screen.UserProfile -> UserProfileScreen(
                    language = selectedLanguage,
                    onBack = { navigator.pop() },
                    account = repo.getAccount(userSession?.username ?: "")
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
                        navigator.push(Screen.TransactionInput(
                            receiverAccount = receiverAccount, senderAccount = repo.getAccount(
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