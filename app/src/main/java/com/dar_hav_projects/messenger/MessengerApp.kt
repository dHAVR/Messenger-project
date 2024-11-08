package com.dar_hav_projects.messenger

import android.app.Application
import androidx.room.Room
import com.dar_hav_projects.messenger.db.MainDb
import com.dar_hav_projects.messenger.db.MessagesDAO
import com.dar_hav_projects.messenger.di.AppComponent
import com.dar_hav_projects.messenger.di.ContextModule
import com.dar_hav_projects.messenger.di.DaggerAppComponent
import com.dar_hav_projects.messenger.di.MessagesRepositoryModule
import com.dar_hav_projects.messenger.di.NetworkActionsModule

class MessengerApp : Application() {

    lateinit var appComponent: AppComponent
    lateinit var database: MainDb
    lateinit var messagesDao: MessagesDAO

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            applicationContext,
            MainDb::class.java,
            "main_db"
        ).build()

        messagesDao = database.listItemDao

        appComponent = DaggerAppComponent
            .builder()
            .messagesRepositoryModule(MessagesRepositoryModule(messagesDao))
            .networkActionsModule(NetworkActionsModule(applicationContext))
            .contextModule(ContextModule(this))
            .build()

    }
}