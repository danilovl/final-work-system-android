package com.finalworksystem

import android.app.Application
import com.finalworksystem.di.dataModule
import com.finalworksystem.di.domainModule
import com.finalworksystem.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class FinalWorkSystemApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@FinalWorkSystemApp)
            modules(listOf(
                dataModule,
                domainModule,
                presentationModule
            ))
        }
    }
}
