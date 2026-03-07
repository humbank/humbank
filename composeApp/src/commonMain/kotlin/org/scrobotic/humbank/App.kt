package org.scrobotic.humbank

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.burnoo.compose.remembersetting.rememberStringSetting
import humbank.composeapp.generated.resources.Res
import humbank.composeapp.generated.resources.loading_panel
import humbank.composeapp.generated.resources.retry
import humbank.composeapp.generated.resources.unknown_error
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
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
import org.scrobotic.humbank.screens.Navigator
import org.scrobotic.humbank.screens.ProfileScreen
import org.scrobotic.humbank.screens.Screen
import org.scrobotic.humbank.screens.SearchScreen
import org.scrobotic.humbank.screens.SettingsScreen
import org.scrobotic.humbank.screens.TransactionInputScreen
import org.scrobotic.humbank.screens.UserProfileScreen
import org.scrobotic.humbank.screens.home.HomeScreen
import org.scrobotic.humbank.ui.HumbankGradientScreen
import org.scrobotic.humbank.ui.HumbankPalette
import org.scrobotic.humbank.ui.HumbankUITheme
import org.scrobotic.humbank.ui.elements.navigation.BottomNavigationBar
import org.scrobotic.humbank.ui.humbankPalette
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun App(navigator: Navigator, database: Database, backProgress: Float = 0f) {
    val scope = rememberCoroutineScope()
    val httpClient = createNetworkClient()

    var token by rememberStringSetting("token", "")
    var username by rememberStringSetting("username", "")

    var userSession: UserSession? by remember {
        mutableStateOf(
            if (token.isNotEmpty() && username.isNotEmpty()) {
                UserSession(token = token, username = username)
            } else null
        )
    }

    val apiService = ApiServiceImpl(httpClient = httpClient, baseUrl = "https://humbank.cv")
    val apiRepository: ApiRepository = ApiRepositoryImpl(apiService)
    val repo = AccountRepository(database)
    val transactions = remember { mutableStateListOf<Transaction>() }

    HumbankUITheme {
        val palette = humbankPalette()
        val localization = koinInject<Localization>()
        var languageIso by rememberStringSetting("savedLanguageIso", Language.German.iso)
        localization.applyLanguage(languageIso)

        val selectedLanguage by derivedStateOf {
            Language.entries.first { it.iso == languageIso }
        }

        val selectedNavIndex = when (navigator.current) {
            is Screen.Home -> 0
            is Screen.Search -> 1
            is Screen.Settings -> 2
            is Screen.UserProfile -> 3
            else -> 0
        }

        var previousScreen by remember { mutableStateOf(navigator.current) }
        var currentScreen by remember { mutableStateOf(navigator.current) }
        if (navigator.current != currentScreen) {
            if (!navigator.isGoingBack) {
                previousScreen = currentScreen
            }
            currentScreen = navigator.current
        }

        val onTokenInvalid: () -> Unit = {
            token = ""
            username = ""
            userSession = null
            navigator.replace(Screen.Login)
        }
        val onLogout: () -> Unit = {
            token = ""
            username = ""
            userSession = null
            navigator.replace(Screen.Login)
        }
        val onLanguageChange: (Language) -> Unit = { lang ->
            languageIso = lang.iso
            localization.applyLanguage(languageIso)
        }

        Scaffold(
            containerColor = palette.gradientTop,
            bottomBar = {
                val showNav = navigator.current !is Screen.Login &&
                        navigator.current !is Screen.TransactionInput &&
                        navigator.current !is Screen.AdminPanel
                if (showNav) {
                    BottomNavigationBar(
                        selectedIndex = selectedNavIndex,
                        onHomeClicked = {
                            userSession?.let { navigator.replace(Screen.Home(it)) }
                        },
                        onSettingsClicked = { navigator.push(Screen.Settings) },
                        onNotificationsClicked = { navigator.push(Screen.Search) },
                        onAccountClicked = { navigator.push(Screen.UserProfile) }
                    )
                }
            }
        ) { innerPadding ->
            Box {
                // Previous screen beneath during back gesture
                // Previous screen beneath during back gesture
                if (backProgress > 0.01f) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) { /* block all touches on background */ }
                            .graphicsLayer {
                                val scale = 0.88f + (backProgress * 0.12f)
                                scaleX = scale
                                scaleY = scale
                                alpha = 0.6f + (backProgress * 0.4f)
                            }
                    ) {
                        ScreenContent(
                            screen = previousScreen,
                            navigator = navigator,
                            innerPadding = innerPadding,
                            userSession = userSession,
                            selectedLanguage = selectedLanguage,
                            repo = repo,
                            apiRepository = apiRepository,
                            transactions = transactions,
                            scope = scope,
                            palette = palette,
                            onTokenInvalid = onTokenInvalid,
                            onLogout = onLogout,
                            onLanguageChange = onLanguageChange
                        )
                    }
                }

                // Current screen on top
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            val scale = 1f - (backProgress * 0.12f)  // was 0.08f
                            val offset = backProgress * 280f          // was 120f
                            scaleX = scale
                            scaleY = scale
                            translationX = offset
                            clip = true
                            shape = RoundedCornerShape((backProgress * 24).dp)
                        }
                ) {
                    AnimatedContent(
                        targetState = navigator.current,
                        transitionSpec = {
                            val direction = if (navigator.isGoingBack) -1 else 1
                            fadeIn(animationSpec = tween(220)) + slideInHorizontally(
                                animationSpec = tween(220),
                                initialOffsetX = { it / 20 * direction }
                            ) togetherWith fadeOut(animationSpec = tween(180)) + slideOutHorizontally(
                                animationSpec = tween(220),
                                targetOffsetX = { it / 20 * -direction }
                            )
                        },
                        label = "screenTransition"
                    ) { screen ->
                        ScreenContent(
                            screen = screen,
                            navigator = navigator,
                            innerPadding = innerPadding,
                            userSession = userSession,
                            selectedLanguage = selectedLanguage,
                            repo = repo,
                            apiRepository = apiRepository,
                            transactions = transactions,
                            scope = scope,
                            palette = palette,
                            onTokenInvalid = onTokenInvalid,
                            onLogout = onLogout,
                            onLanguageChange = onLanguageChange
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun ScreenContent(
    screen: Screen,
    navigator: Navigator,
    innerPadding: PaddingValues,
    userSession: UserSession?,
    selectedLanguage: Language,
    repo: AccountRepository,
    apiRepository: ApiRepository,
    transactions: androidx.compose.runtime.snapshots.SnapshotStateList<Transaction>,
    scope: CoroutineScope,
    palette: HumbankPalette,
    onTokenInvalid: () -> Unit,
    onLogout: () -> Unit,
    onLanguageChange: (Language) -> Unit,
) {
    when (screen) {
        is Screen.Home -> HomeScreen(
            userSession = screen.userSession,
            contentPadding = innerPadding,
            onTokenInvalid = onTokenInvalid,
            repo = repo,
            apiRepository = apiRepository,
        )
        Screen.UserProfile -> UserProfileScreen(
            language = selectedLanguage,
            onBack = { navigator.pop() },
            account = userSession?.let { repo.getAccount(it.username) },
            onLogout = onLogout,
            onAdminPanelClick = { navigator.push(Screen.AdminPanel) }
        )
        Screen.Settings -> SettingsScreen(
            language = selectedLanguage,
            onLanguageChange = onLanguageChange,
            onBack = { navigator.pop() }
        )
        Screen.Search -> SearchScreen(
            repository = repo,
            onNavigateToAccount = { uname, balance ->
                navigator.push(
                    Screen.Profile(
                        receiverAccount = repo.getAccount(uname)!!,
                        currentBalance = balance
                    )
                )
            },
            innerPadding = innerPadding
        )
        is Screen.Profile -> ProfileScreen(
            receiverAccount = screen.receiverAccount,
            senderAccount = userSession?.let { repo.getAccount(it.username) },
            currentBalance = screen.currentBalance,
            apiRepository = apiRepository,
            onTransactionSuccess = { navigator.pop() },
            onBack = { navigator.pop() }
        )
        is Screen.TransactionInput -> TransactionInputScreen(
            senderAccount = screen.senderAccount,
            receiverAccount = screen.receiverAccount,
            userToken = userSession?.token ?: "",
            apiRepository = apiRepository,
            onNavigateBack = { navigator.pop() },
            onTransactionSuccess = {
                scope.launch {
                    try {
                        val updatedTransactions = apiRepository.getTodaysTransactions()
                        transactions.clear()
                        transactions.addAll(updatedTransactions)
                        repo.syncAccounts(apiRepository.getAllAccounts())
                    } catch (e: Exception) {
                        println("Failed to reload after transfer: ${e.message}")
                    }
                }
            }
        )
        Screen.Login -> LoginScreen(
            onLogin = { u, p -> apiRepository.login(u, p) },
            onLoginSuccess = { session ->
                navigator.replace(Screen.Home(session))
            }
        )
        is Screen.AdminPanel -> {
            var isLoading by remember { mutableStateOf(true) }
            var error by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(Unit) {
                try {
                    repo.syncAccounts(apiRepository.getAllAccounts())
                    isLoading = false
                } catch (e: Exception) {
                    error = "Failed to load accounts: ${e.message}"
                    isLoading = false
                }
            }

            HumbankGradientScreen {
                when {
                    isLoading -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(
                                color = palette.primaryButton,
                                strokeWidth = 2.5.dp
                            )
                            Text(
                                stringResource(Res.string.loading_panel),
                                color = palette.muted,
                                fontSize = 14.sp
                            )
                        }
                    }
                    error != null -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = error ?: stringResource(Res.string.unknown_error),
                                color = palette.errorText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(Modifier.height(4.dp))
                            Button(
                                onClick = { isLoading = true; error = null },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = palette.primaryButton,
                                    contentColor = palette.primaryButtonText
                                )
                            ) {
                                Text(stringResource(Res.string.retry))
                            }
                        }
                    }
                    else -> AdminPanelScreen(
                        apiRepository = apiRepository,
                        onBack = { navigator.pop() }
                    )
                }
            }
        }
    }
}