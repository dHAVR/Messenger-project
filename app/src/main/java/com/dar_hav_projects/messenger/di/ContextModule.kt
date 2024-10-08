package com.dar_hav_projects.messenger.di

import android.app.Application
import android.content.Context
import com.dar_hav_projects.messenger.domens.actions.FirebaseRepository
import com.dar_hav_projects.messenger.domens.actions.I_NetworkActions
import dagger.Module
import dagger.Provides

@Module
class ContextModule(private val application: Application) {

    @Provides
    fun provideContextModule(): Context = application.applicationContext
}