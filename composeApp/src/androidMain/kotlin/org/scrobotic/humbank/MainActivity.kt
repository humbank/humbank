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
import createDatabase
import org.koin.core.context.GlobalContext
import org.scrobotic.humbank.misc.FirstLaunchManager
import org.scrobotic.humbank.screens.rememberNavigator
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

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
            val navigator = rememberNavigator()
            val driverFactory = DriverFactory(context= applicationContext)
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
//
//@Preview
//@Composable
//fun AppAndroidPreview() {
//    val navigator = remember { Navigator() }
//    val driverFactory = DriverFactory(context = a)
//    val database = createDatabase(driverFactory)
//    App(navigator = navigator, database = data)
//}

fun provideSettings(context: Context): Settings {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    return SharedPreferencesSettings(prefs)
}
