package org.scrobotic.humbank.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import humbank.composeapp.generated.resources.Res
import humbank.composeapp.generated.resources.search_placeholder
import org.humbank.ktorclient.icons.imagevectors.Account
import org.jetbrains.compose.resources.stringResource
import org.scrobotic.humbank.AccountRepository
import org.scrobotic.humbank.ui.elements.icons.processed.Close
import org.scrobotic.humbank.ui.elements.icons.processed.Search



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(repository: AccountRepository) {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    // This Flow updates every time 'query' changes
    val searchResults by repository.searchAccounts(query)
        .collectAsState(initial = emptyList())

    Box(Modifier.fillMaxSize()) {
        SearchBar(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 8.dp),
            query = query,
            onQueryChange = { query = it },
            onSearch = { active = false },
            active = active,
            onActiveChange = { active = it },
            placeholder = { Text(stringResource(Res.string.search_placeholder)) },
            leadingIcon = { Search },
            trailingIcon = {
                if (active) {
                    IconButton(onClick = { if (query.isNotEmpty()) query = "" else active = false }) {
                        Icon(Close, contentDescription = "Close search")
                    }
                }
            }
        ) {
            // This is the content shown when the search bar is ACTIVE
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(searchResults) { account ->
                    ListItem(
                        headlineContent = { Text(account.full_name) },
                        supportingContent = { Text("@${account.username}") },
                        leadingContent = { Icon(Account, contentDescription = null) },
                        modifier = Modifier.clickable {
                            // Logic for selecting an account
                            active = false
                        }
                    )
                }
            }
        }
    }
}