package org.scrobotic.humbank.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import humbank.composeapp.generated.resources.Res
import humbank.composeapp.generated.resources.account_not_found
import humbank.composeapp.generated.resources.admin_back
import humbank.composeapp.generated.resources.admin_panel_title
import humbank.composeapp.generated.resources.create_full_name
import humbank.composeapp.generated.resources.login_username
import humbank.composeapp.generated.resources.user_profile_role
import humbank.composeapp.generated.resources.user_profile_sign_out
import org.jetbrains.compose.resources.stringResource
import org.scrobotic.humbank.data.AllAccount
import org.scrobotic.humbank.domain.Language
import org.scrobotic.humbank.ui.HumbankGradientScreen
import org.scrobotic.humbank.ui.humbankPalette
import org.scrobotic.humbank.ui.elements.icons.processed.ArrowBack

@Composable
fun UserProfileScreen(
    account: AllAccount?,
    language: Language,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onAdminPanelClick: () -> Unit
) {
    val palette = humbankPalette()

    HumbankGradientScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .statusBarsPadding()
        ) {
            // Back button row
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(palette.cardSurface)
            ) {
                Icon(ArrowBack, contentDescription = stringResource(Res.string.admin_back), tint = palette.title, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (account != null) {
                // Avatar + name header
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(palette.primaryButton.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = account.full_name.firstOrNull()?.toString()?.uppercase() ?: "?",
                            color = palette.primaryButton,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = account.full_name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.title,
                        letterSpacing = (-0.4).sp
                    )
                    Text(
                        text = "@${account.username}",
                        fontSize = 14.sp,
                        color = palette.muted
                    )

                    // Role badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = palette.primaryButton.copy(alpha = 0.12f)
                    ) {
                        Text(
                            account.role.uppercase(),
                            color = palette.primaryButton,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Info card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = palette.cardSurface,
                    border = BorderStroke(
                        1.dp,
                        Brush.verticalGradient(
                            colors = listOf(palette.cardStroke.copy(alpha = 0.6f), palette.cardStroke.copy(alpha = 0.1f))
                        )
                    ),
                    shadowElevation = 4.dp,
                    tonalElevation = 0.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(0.dp)) {
                        ProfileInfoRow(stringResource(Res.string.create_full_name), account.full_name, palette)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = palette.cardStroke.copy(alpha = 0.4f), thickness = 0.5.dp)
                        ProfileInfoRow(stringResource(Res.string.login_username), "@${account.username}", palette)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = palette.cardStroke.copy(alpha = 0.4f), thickness = 0.5.dp)
                        ProfileInfoRow(stringResource(Res.string.user_profile_role), account.role.replaceFirstChar { it.uppercase() }, palette)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Actions
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (account.role == "admin") {
                        Button(
                            onClick = onAdminPanelClick,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = palette.primaryButton,
                                contentColor = palette.primaryButtonText
                            )
                        ) {
                            Text(stringResource(Res.string.admin_panel_title), fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        }
                    }

                    Button(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = palette.errorBackground,
                            contentColor = palette.errorText
                        )
                    ) {
                        Text(stringResource(Res.string.user_profile_sign_out), fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(Res.string.account_not_found),
                        color = palette.errorText,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String, palette: org.scrobotic.humbank.ui.HumbankPalette) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 13.sp, color = palette.muted, fontWeight = FontWeight.Medium)
        Text(value, fontSize = 13.sp, color = palette.title, fontWeight = FontWeight.SemiBold)
    }
}
