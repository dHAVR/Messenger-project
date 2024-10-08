package com.dar_hav_projects.messenger.di

import android.content.Context
import com.dar_hav_projects.messenger.domens.actions.FirebaseRepository
import com.dar_hav_projects.messenger.domens.actions.I_NetworkActions
import dagger.Module
import dagger.Provides

@Module
class NetworkActionsModule(private val context: Context) {

    @Provides
    fun provideNetworkActionsModule(): I_NetworkActions {
        return FirebaseRepository(context)
    }
}
