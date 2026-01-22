package org.scrobotic.humbank.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import humbank.composeapp.generated.resources.Res
import org.humbank.ktorclient.icons.imagevectors.FastFood
import org.jetbrains.compose.resources.stringResource
import org.scrobotic.humbank.domain.Language
import org.scrobotic.humbank.ui.elements.icons.processed.ArrowDropDown

@Composable
fun SettingsScreen(language: Language, onLanguageChange: (Language) -> Unit, onBack: () -> Boolean) {



    // LANGUAGE PICKER
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally // Centers children horizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Wrap the Button and Menu in a Box so the menu knows where the button is
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(if (language == Language.English) "English 🇬🇧" else "Deutsch 🇩🇪")
                Icon(ArrowDropDown, contentDescription = null)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("English 🇬🇧") },
                    onClick = {
                        onLanguageChange(Language.English)
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Deutsch 🇩🇪") },
                    onClick = {
                        onLanguageChange(Language.German)
                        expanded = false
                    }
                )
            }
        }
    }
}