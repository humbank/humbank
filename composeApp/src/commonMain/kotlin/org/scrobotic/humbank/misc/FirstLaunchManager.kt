package org.scrobotic.humbank.misc

import com.russhwolf.settings.Settings

class FirstLaunchManager(
    private val settings: Settings
) {
    private val key = "has_run_before"

    fun runOnce(block: () -> Unit) {
        val hasRun = settings.getBoolean(key, false)

        if (!hasRun) {
            block()
            settings.putBoolean(key, true)
        }
    }
}