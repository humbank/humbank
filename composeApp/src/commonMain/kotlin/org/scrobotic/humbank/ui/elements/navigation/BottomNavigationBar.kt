package org.scrobotic.humbank.ui.elements.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import humbank.composeapp.generated.resources.Res
import humbank.composeapp.generated.resources.account_title
import humbank.composeapp.generated.resources.home_title
import humbank.composeapp.generated.resources.notifications_title
import humbank.composeapp.generated.resources.wallet_title
import org.humbank.ktorclient.icons.imagevectors.Account
import org.humbank.ktorclient.icons.imagevectors.CarCrash
import org.humbank.ktorclient.icons.imagevectors.CreditCard
import org.humbank.ktorclient.icons.imagevectors.Home
import org.jetbrains.compose.resources.stringResource
import org.scrobotic.humbank.data.BottomNavigation


val items = listOf(
    BottomNavigation(
        title = Res.string.home_title,
        icon = Home
    ),

    BottomNavigation(
        title = Res.string.wallet_title,
        icon = CreditCard
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
fun BottomNavigationBar() {
    NavigationBar {
        Row(
            modifier = Modifier.background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {

            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = index == 0,
                    onClick = {},
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