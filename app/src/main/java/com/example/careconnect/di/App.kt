package com.example.careconnect.di

import android.app.Application
import com.example.careconnect.util.ResourceHelper
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        ResourceHelper.init(this)

        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }
}
