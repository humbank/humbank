package org.scrobotic.humbank.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.scrobotic.humbank.domain.Language
import org.scrobotic.humbank.ui.elements.navigation.BottomNavigationBar

@Composable
fun HomeScreen(
    language: Language,
    onSettingsClicked: () -> Unit
) {
    Scaffold(
    ){ padding ->
        Column(
            modifier = Modifier.
                fillMaxSize().
                padding(padding)
        ){

            WalletSection()
            BusinessesSection()
            Spacer(modifier = Modifier.height(16.dp))
            //FinanceSection()
            //TransactionsSection()
        }

    }
}