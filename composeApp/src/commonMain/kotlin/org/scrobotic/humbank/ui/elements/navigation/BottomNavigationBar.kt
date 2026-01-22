package org.scrobotic.humbank.ui.elements.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import humbank.composeapp.generated.resources.Res
import humbank.composeapp.generated.resources.account_title
import humbank.composeapp.generated.resources.home_title
import humbank.composeapp.generated.resources.notifications_title
import humbank.composeapp.generated.resources.settings_title
import org.humbank.ktorclient.icons.imagevectors.Account
import org.humbank.ktorclient.icons.imagevectors.CarCrash
import org.humbank.ktorclient.icons.imagevectors.CreditCard
import org.humbank.ktorclient.icons.imagevectors.Home
import org.jetbrains.compose.resources.stringResource
import org.scrobotic.humbank.data.BottomNavigation
import org.scrobotic.humbank.ui.elements.icons.processed.Settings


val items = listOf(
    BottomNavigation(
        title = Res.string.home_title,
        icon = Home
    ),

    BottomNavigation(
        title = Res.string.settings_title,
        icon = Settings
    ),

    BottomNavigation(
        title = Res.string.notifications_title,
        icon = CarCrash
    ),

    BottomNavigation(
        title = Res.string.account_title,
        icon = Account
    )
)

@Preview
@Composable
fun BottomNavigationBar(
    onHomeClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onNotificationsClicked: () -> Unit,
    onAccountClicked: () -> Unit
) {
    var selectedItemIndex by remember { mutableIntStateOf(0) }
    NavigationBar {
        Row(
            modifier = Modifier.background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {

            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = selectedItemIndex == index,
                    onClick = {
                        selectedItemIndex = index
                        when (item.title) {
                            Res.string.home_title -> onHomeClicked()
                            Res.string.settings_title -> onSettingsClicked()
                            Res.string.notifications_title -> onNotificationsClicked()
                            Res.string.account_title -> onAccountClicked()
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title.toString(),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(item.title),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                )
            }

        }
    }
}