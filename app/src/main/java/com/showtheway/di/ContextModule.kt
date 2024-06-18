package com.showtheway.di

import android.content.Context
import com.showtheway.util.ConnectionChecker
import com.showtheway.util.PermissionsHandler
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val contextModule = module {

    factory {
        ConnectionChecker(androidContext())
    }

    factory { params ->
        val context: Context = params.get()

        PermissionsHandler(context)
    }

}