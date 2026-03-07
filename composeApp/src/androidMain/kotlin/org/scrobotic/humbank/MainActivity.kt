package org.scrobotic.humbank

import DriverFactory
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.PredictiveBackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.koin.android.ext.koin.androidContext
import org.scrobotic.humbank.domain.initializeKoin
import createDatabase
import org.koin.core.context.GlobalContext
import org.scrobotic.humbank.misc.FirstLaunchManager
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import dev.burnoo.compose.remembersetting.rememberStringSetting
import org.scrobotic.humbank.data.UserSession
import org.scrobotic.humbank.screens.Screen
import org.scrobotic.humbank.screens.rememberNavigator
import kotlin.coroutines.cancellation.CancellationException

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

            val startScreen = if (savedToken.isNotEmpty() && savedUsername.isNotEmpty()) {
                Screen.Home(UserSession(token = savedToken, username = savedUsername))
            } else {
                Screen.Login
            }

            val navigator = rememberNavigator(start = startScreen)
            val driverFactory = DriverFactory(context = applicationContext)
            val database = createDatabase(driverFactory)

            var backProgress by remember { mutableStateOf(0f) }

            PredictiveBackHandler(enabled = navigator.canGoBack) { progress ->
                try {
                    progress.collect { backEvent ->
                        backProgress = backEvent.progress
                    }
                    backProgress = 0f
                    navigator.pop()
                } catch (e: CancellationException) {
                    backProgress = 0f
                }
            }
            App(navigator = navigator, database = database, backProgress = backProgress)
        }
    }
}

fun provideSettings(context: Context): Settings {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    return SharedPreferencesSettings(prefs)
}