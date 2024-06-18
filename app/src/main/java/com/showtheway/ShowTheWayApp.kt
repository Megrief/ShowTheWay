package com.showtheway

import android.app.Application
import com.showtheway.di.contextModule
import com.showtheway.di.presentationModule
import com.yandex.mapkit.MapKitFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ShowTheWayApp : Application() {

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(BuildConfig.MAP_KIT_API_KEY)

        startKoin {
            androidContext(this@ShowTheWayApp)

            modules(
                contextModule,
                presentationModule
            )
        }
    }
}