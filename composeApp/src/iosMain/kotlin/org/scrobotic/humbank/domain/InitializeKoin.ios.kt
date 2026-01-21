package org.scrobotic.humbank.domain


import org.koin.dsl.module

actual val targetModule = module {
    single<Localization> { Localization() }
}