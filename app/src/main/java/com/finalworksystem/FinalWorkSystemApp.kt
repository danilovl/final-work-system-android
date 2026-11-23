package com.finalworksystem

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.finalworksystem.di.dataModule
import com.finalworksystem.di.domainModule
import com.finalworksystem.di.presentationModule
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class FinalWorkSystemApp : Application(), SingletonImageLoader.Factory {
    override fun onCreate() {
        super.onCreate()

        // Release builds plant no tree, so nothing is written to logcat.
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

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

    /** Coil uses the authenticated image loader configured in Koin. */
    override fun newImageLoader(context: PlatformContext): ImageLoader = get()
}
