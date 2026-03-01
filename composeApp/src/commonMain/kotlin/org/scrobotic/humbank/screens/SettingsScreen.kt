package org.scrobotic.humbank.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.scrobotic.humbank.domain.Language
import org.scrobotic.humbank.ui.HumbankGradientScreen
import org.scrobotic.humbank.ui.HumbankPanelCard
import org.scrobotic.humbank.ui.elements.icons.processed.ArrowDropDown

@Composable
fun SettingsScreen(language: Language, onLanguageChange: (Language) -> Unit, onBack: () -> Boolean) {
    var expanded by remember { mutableStateOf(false) }

    HumbankGradientScreen {
        HumbankPanelCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.TopCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text("Settings", color = Color.White)
                Text("Language", color = Color(0xFFAFA6D4))

                Box {
                    OutlinedButton(onClick = { expanded = true }) {
                        Text(if (language == Language.English) "English 🇬🇧" else "Deutsch 🇩🇪")
                        androidx.compose.material3.Icon(ArrowDropDown, contentDescription = null)
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

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Choose your preferred language for labels and content.",
                    color = Color(0xFF9A92BE)
                )
            }
        }
    }
}
