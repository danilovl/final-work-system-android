package com.finalworksystem.di

import android.app.Application
import android.content.Context
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.verify.verify

/**
 * Verifies that every declared constructor dependency of every definition
 * can be resolved from the application modules (no missing `get()`).
 */
class KoinModulesTest {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `all app modules resolve their dependencies`() {
        val appModule = module {
            includes(dataModule, domainModule, presentationModule)
        }

        appModule.verify(extraTypes = listOf(Context::class, Application::class))
    }
}
