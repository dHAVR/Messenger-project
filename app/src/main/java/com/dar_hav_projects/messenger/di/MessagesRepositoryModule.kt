package com.dar_hav_projects.messenger.di

import android.app.Application
import android.content.Context
import com.dar_hav_projects.messenger.db.MessagesDAO
import com.dar_hav_projects.messenger.db.MessagesRepoImpl
import com.dar_hav_projects.messenger.db.MessagesRepository
import dagger.Module
import dagger.Provides

@Module
class MessagesRepositoryModule(private val dao: MessagesDAO) {

    @Provides
    fun provideMessagesRepositoryModule(): MessagesRepository = MessagesRepoImpl(dao)
}