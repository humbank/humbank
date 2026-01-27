package org.scrobotic.humbank

import DriverFactory
import androidx.compose.ui.window.ComposeUIViewController
import createDatabase
import org.scrobotic.humbank.domain.initializeKoin
import org.scrobotic.humbank.screens.Navigator

val driverFactory = DriverFactory()
val database = createDatabase(driverFactory)

val navigator = Navigator()

fun MainViewController() = ComposeUIViewController(
    configure = { initializeKoin() }
) { App(navigator = navigator, database = database) }