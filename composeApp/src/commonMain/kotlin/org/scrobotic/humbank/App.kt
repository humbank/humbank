package org.scrobotic.humbank

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import dev.burnoo.compose.remembersetting.rememberStringSetting

import org.koin.compose.koinInject
import org.scrobotic.humbank.domain.Language
import org.scrobotic.humbank.domain.Localization
import org.scrobotic.humbank.screens.home.HomeScreen
import org.scrobotic.humbank.screens.Navigator
import org.scrobotic.humbank.screens.Screen
import org.scrobotic.humbank.screens.SettingsScreen
import org.scrobotic.humbank.screens.UserProfileScreen
import org.scrobotic.humbank.screens.SearchScreen
import org.scrobotic.humbank.ui.HumbankUITheme
import org.scrobotic.humbank.ui.elements.navigation.BottomNavigationBar

@Composable
@Preview
fun App(navigator: Navigator, database: Database) {
    val repo = AccountRepository(database)
    val account = repo.getAccount("scrobotic")


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



        Scaffold(bottomBar= {
            BottomNavigationBar(
               onHomeClicked = {navigator.replace(Screen.Home)},
                onSettingsClicked = {navigator.push(Screen.Settings)},
                onNotificationsClicked = { navigator.push(Screen.Search) },
                onAccountClicked = { navigator.push(Screen.UserProfile) }
            )
        }){ innerPadding ->
            when (val screen = navigator.current) {

                Screen.Home -> HomeScreen(
                    contentPadding = innerPadding,
                    account = account,
                    onNavigateToTransfer = {  },
                    onNavigateToProfile ={  },
                    repo = repo
                )

                is Screen.UserProfile -> UserProfileScreen(
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
                    repository = repo
                )
            }
        }

    }
}