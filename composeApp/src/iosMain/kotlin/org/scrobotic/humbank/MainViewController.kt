package org.scrobotic.humbank

import androidx.compose.ui.window.ComposeUIViewController
import org.scrobotic.humbank.domain.initializeKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initializeKoin() }
) { App() }