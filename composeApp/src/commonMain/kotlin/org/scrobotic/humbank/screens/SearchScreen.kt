package org.scrobotic.humbank.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import humbank.composeapp.generated.resources.Res
import humbank.composeapp.generated.resources.search_placeholder
import org.humbank.ktorclient.icons.imagevectors.Account
import org.jetbrains.compose.resources.stringResource
import org.scrobotic.humbank.AccountRepository
import org.scrobotic.humbank.ui.HumbankGradientScreen
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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 0.dp,
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 90.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header
            item {
                Spacer(modifier = Modifier.statusBarsPadding())
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Search",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = palette.title,
                    letterSpacing = (-0.5).sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Find accounts by name or username",
                    color = palette.muted,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Search field
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text(stringResource(Res.string.search_placeholder), color = palette.muted) },
                    leadingIcon = { Icon(Search, contentDescription = null, tint = palette.muted, modifier = Modifier.size(20.dp)) },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Close, contentDescription = "Clear", tint = palette.muted, modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = palette.inputFillFocused,
                        unfocusedContainerColor = palette.cardSurface,
                        focusedBorderColor = palette.inputBorderFocused,
                        unfocusedBorderColor = palette.cardStroke,
                        focusedTextColor = palette.title,
                        unfocusedTextColor = palette.title,
                        focusedLabelColor = palette.inputBorderFocused,
                        unfocusedLabelColor = palette.muted
                    )
                )
            }

            // Empty state
            if (query.isNotBlank() && searchResults.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No results", color = palette.title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("No accounts matching \"$query\"", color = palette.muted, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Results
            if (searchResults.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "${searchResults.size} result${if (searchResults.size != 1) "s" else ""}",
                        color = palette.muted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.3.sp
                    )
                }
            }

            items(searchResults) { account ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onNavigateToAccount(account.username) },
                    shape = RoundedCornerShape(18.dp),
                    color = palette.cardSurface,
                    border = BorderStroke(
                        1.dp,
                        Brush.verticalGradient(
                            colors = listOf(
                                palette.cardStroke.copy(alpha = 0.6f),
                                palette.cardStroke.copy(alpha = 0.1f)
                            )
                        )
                    ),
                    shadowElevation = 2.dp,
                    tonalElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(palette.primaryButton.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = account.full_name.firstOrNull()?.toString()?.uppercase() ?: "?",
                                color = palette.primaryButton,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                account.full_name,
                                color = palette.title,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                letterSpacing = (-0.2).sp
                            )
                            Text(
                                "@${account.username}",
                                color = palette.muted,
                                fontSize = 13.sp
                            )
                        }

                        // Role badge
                        if (account.role != "user") {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = palette.primaryButton.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    account.role.uppercase(),
                                    color = palette.primaryButton,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
