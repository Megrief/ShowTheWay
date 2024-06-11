package com.showtheway

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class ShowTheWayApp : Application() {

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(BuildConfig.MAP_KIT_API_KEY)
    }
}