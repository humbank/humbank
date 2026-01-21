package org.scrobotic.humbank.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import humbank.composeapp.generated.resources.Res
import humbank.composeapp.generated.resources.selected_language
import humbank.composeapp.generated.resources.settings_button_lable
import org.scrobotic.humbank.domain.Language
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(
    language: Language,
    onLanguageChange: (Boolean) -> Unit,
    onUserSelected: (String) -> Unit,
    onSettingsClicked: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "${stringResource(Res.string.selected_language)}: ${language.name}",
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Switch(
            checked = language == Language.English,
            onCheckedChange = onLanguageChange
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.settings_button_lable),
            textAlign = TextAlign.Center
        )
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onSettingsClicked
        ) {
        }
    }
}