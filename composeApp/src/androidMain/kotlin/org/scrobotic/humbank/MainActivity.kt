package org.scrobotic.humbank

import DriverFactory
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import org.koin.android.ext.koin.androidContext
import org.scrobotic.humbank.domain.initializeKoin
import org.scrobotic.humbank.screens.Navigator
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import createDatabase
import org.koin.core.context.GlobalContext
import org.scrobotic.humbank.misc.FirstLaunchManager
import org.scrobotic.humbank.screens.rememberNavigator
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import dev.burnoo.compose.remembersetting.rememberStringSetting
import org.scrobotic.humbank.data.UserSession
import org.scrobotic.humbank.screens.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val settings = provideSettings(this)
        val firstLaunchManager = FirstLaunchManager(settings)
        firstLaunchManager.runOnce {
            deleteDatabase("Humbank.db")
        }

        if (GlobalContext.getOrNull() == null) {
            initializeKoin(
                config = { androidContext(this@MainActivity) }
            )
        }

        setContent {

            var savedToken by rememberStringSetting("token", "")
            var savedUsername by rememberStringSetting("username", "")

            println("🔍 STARTUP: token='${savedToken}', username='${savedUsername}'")
            println("🔍 STARTUP: token.isNotEmpty()=${savedToken.isNotEmpty()}, username.isNotEmpty()=${savedUsername.isNotEmpty()}")

            val startScreen = if (savedToken.isNotEmpty() && savedUsername.isNotEmpty()) {
                println("✅ STARTUP: Starting at HOME")
                Screen.Home(UserSession(token = savedToken, username = savedUsername))
            } else {
                println("❌ STARTUP: Starting at LOGIN")
                Screen.Login
            }

            println("🔍 STARTUP: startScreen = $startScreen")

            val navigator = rememberNavigator(start = startScreen)

            println("🔍 STARTUP: navigator.current = ${navigator.current}")

            val driverFactory = DriverFactory(context = applicationContext)
            val database = createDatabase(driverFactory)

            BackHandler {
                val handled = navigator.pop()
                if (!handled) {
                    // Let Android close the app (root screen)
                }
            }

            App(navigator = navigator, database = database)
        }
    }
}

fun provideSettings(context: Context): Settings {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    return SharedPreferencesSettings(prefs)
}