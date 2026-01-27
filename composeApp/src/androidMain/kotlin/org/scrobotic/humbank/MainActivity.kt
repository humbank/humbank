package org.scrobotic.humbank

import DriverFactory
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        if (GlobalContext.getOrNull() == null) {

            initializeKoin(
                config = { androidContext(this@MainActivity) }
            )
        }

        setContent {
            val navigator = remember { Navigator() }
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