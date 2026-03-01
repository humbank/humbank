package org.scrobotic.humbank.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import humbank.composeapp.generated.resources.Res
import humbank.composeapp.generated.resources.search_placeholder
import org.humbank.ktorclient.icons.imagevectors.Account
import org.jetbrains.compose.resources.stringResource
import org.scrobotic.humbank.AccountRepository
import org.scrobotic.humbank.ui.HumbankGradientScreen
import org.scrobotic.humbank.ui.HumbankPanelCard
import org.scrobotic.humbank.ui.elements.icons.processed.Close
import org.scrobotic.humbank.ui.elements.icons.processed.Search
import org.scrobotic.humbank.ui.humbankPalette

@Composable
fun SearchScreen(
    repository: AccountRepository,
    onNavigateToAccount: (account: String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val palette = humbankPalette()

    val searchResults by repository.searchAccounts(query).collectAsState(initial = emptyList())

    HumbankGradientScreen {
        HumbankPanelCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentPadding = PaddingValues(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text(stringResource(Res.string.search_placeholder), color = palette.subtitle) },
                        leadingIcon = { Icon(Search, contentDescription = null, tint = palette.subtitle) },
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = { query = "" }) {
                                    Icon(Close, contentDescription = "Clear search", tint = palette.subtitle)
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = palette.inputFillFocused,
                            unfocusedContainerColor = palette.inputFillUnfocused,
                            focusedBorderColor = palette.inputBorderFocused,
                            unfocusedBorderColor = palette.inputBorderUnfocused,
                            focusedTextColor = palette.title,
                            unfocusedTextColor = palette.title
                        )
                    )
                }

                if (query.isNotBlank() && searchResults.isEmpty()) {
                    item {
                        Text(
                            text = "No matching accounts",
                            color = palette.muted,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                items(searchResults) { account ->
                    ListItem(
                        headlineContent = { Text(account.full_name, color = palette.title) },
                        supportingContent = { Text("@${account.username}", color = palette.subtitle) },
                        leadingContent = { Icon(Account, contentDescription = null, tint = Color(0xFFD6C7FF)) },
                        modifier = Modifier.clickable { onNavigateToAccount(account.username) }
                    )
                }
            }
        }
    }
}
