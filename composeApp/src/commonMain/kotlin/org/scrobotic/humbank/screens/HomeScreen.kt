package org.scrobotic.humbank.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import humbank.composeapp.generated.resources.Res
import org.scrobotic.humbank.domain.Language
import org.jetbrains.compose.resources.stringResource
import org.scrobotic.humbank.ui.elements.navigation.BottomNavigationBar

@Composable
fun HomeScreen(
    language: Language,
    onLanguageChange: (Boolean) -> Unit,
    onUserSelected: (String) -> Unit,
    onSettingsClicked: () -> Unit
) {
    Scaffold(
        bottomBar= {
            BottomNavigationBar()
        }
    ){ padding ->
        Column(
            modifier = Modifier.
                fillMaxSize().
                padding(padding)
        ){
            //WalletSection()
            //BusinessesSection()
            Spacer(modifier = Modifier.height(15.dp))
            //FinanceSection()
            //TransactionsSection()
        }

    }
}