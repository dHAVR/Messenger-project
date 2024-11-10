package com.dar_hav_projects.messenger.di

import android.content.Context
import com.dar_hav_projects.messenger.domens.actions.FirebaseRepository
import com.dar_hav_projects.messenger.domens.actions.I_NetworkActions
import com.dar_hav_projects.messenger.encryption.Encryption
import com.dar_hav_projects.messenger.encryption.I_Encryprion
import dagger.Module
import dagger.Provides

@Module
class EncryptionModule(private val context: Context) {

    @Provides
    fun provideEncryptionModule(): I_Encryprion {
        return Encryption(context)
    }
}