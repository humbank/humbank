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
import org.scrobotic.humbank.ui.HumbankUITheme
import org.scrobotic.humbank.ui.elements.navigation.BottomNavigationBar

@Composable
@Preview
fun App(navigator: Navigator) {

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
                onNotificationsClicked = {},
                onAccountClicked = {}
            )
        }){
            when (val screen = navigator.current) {

                Screen.Home -> HomeScreen(
                    onNavigateToTransfer = {  },
                    onNavigateToProfile ={}
                )

                is Screen.UserProfile -> UserProfileScreen(
                    language = selectedLanguage,
                    onLanguageChange = {
                        languageIso = if (it) Language.English.iso
                        else Language.German.iso
                        localization.applyLanguage(languageIso)
                    },

                    username = screen.username,
                    onBack = { navigator.pop() }
                )

                Screen.Settings -> SettingsScreen(
                    language = selectedLanguage,
                    onLanguageChange = { selectedLanguage ->
                        languageIso = selectedLanguage.iso
                        localization.applyLanguage(languageIso)
                    },

                    onBack = { navigator.pop() }
                )
            }
        }

    }
}