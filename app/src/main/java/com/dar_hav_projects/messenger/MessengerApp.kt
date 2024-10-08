package com.dar_hav_projects.messenger

import android.app.Application
import com.dar_hav_projects.messenger.di.AppComponent
import com.dar_hav_projects.messenger.di.ContextModule
import com.dar_hav_projects.messenger.di.DaggerAppComponent
import com.dar_hav_projects.messenger.di.NetworkActionsModule

class MessengerApp : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent
            .builder()
            .networkActionsModule(NetworkActionsModule(applicationContext))
            .contextModule(ContextModule(this))
            .build()

    }
}