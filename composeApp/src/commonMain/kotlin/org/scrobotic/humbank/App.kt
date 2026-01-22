package org.scrobotic.humbank

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.burnoo.compose.remembersetting.rememberStringSetting
import org.jetbrains.compose.resources.painterResource

import humbank.composeapp.generated.resources.Res
import humbank.composeapp.generated.resources.compose_multiplatform
import org.koin.compose.koinInject
import org.scrobotic.humbank.domain.Language
import org.scrobotic.humbank.domain.Localization
import org.scrobotic.humbank.screens.HomeScreen
import org.scrobotic.humbank.screens.Navigator
import org.scrobotic.humbank.screens.Screen
import org.scrobotic.humbank.screens.SettingsScreen
import org.scrobotic.humbank.screens.UserProfileScreen

@Composable
@Preview
fun App(navigator: Navigator) {
    MaterialTheme {
        val localization = koinInject<Localization>()
        var languageIso by rememberStringSetting(
            key = "savedLanguageIso",
            defaultValue = Language.German.iso
        )

        localization.applyLanguage(languageIso)

        val selectedLanguage by derivedStateOf {
            Language.entries.first { it.iso == languageIso }
        }

        when (val screen = navigator.current) {

            Screen.Home -> HomeScreen(
                language = selectedLanguage,
                onLanguageChange = {
                    languageIso = if (it) Language.English.iso
                    else Language.German.iso
                    localization.applyLanguage(languageIso)
                                   },

                onUserSelected = { username ->
                    navigator.push(Screen.UserProfile(username))
                },
                onSettingsClicked = {
                    navigator.push(Screen.Settings)
                }
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
                onLanguageChange = {
                    languageIso = if (it) Language.English.iso
                    else Language.German.iso
                    localization.applyLanguage(languageIso)
                },

                onBack = { navigator.pop() }
            )
        }

    }
}