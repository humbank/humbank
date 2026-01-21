package org.scrobotic.humbank.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import humbank.composeapp.generated.resources.Res
import humbank.composeapp.generated.resources.greeting
import humbank.composeapp.generated.resources.selected_language
import org.jetbrains.compose.resources.stringResource
import org.scrobotic.humbank.domain.Language

@Composable
fun SettingsScreen(language: Language, onLanguageChange: (Boolean) -> Unit, onBack: () -> Boolean) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(Res.string.greeting),
        textAlign = TextAlign.Center
    )
}