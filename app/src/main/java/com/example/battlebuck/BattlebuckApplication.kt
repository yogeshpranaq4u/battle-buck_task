package com.example.battlebuck

import android.app.Application
import com.example.battlebuck.di.AppContainer
import com.example.battlebuck.di.DefaultAppContainer

class BattlebuckApplication : Application() {
    internal val container: AppContainer by lazy {
        DefaultAppContainer(applicationContext)
    }
}
