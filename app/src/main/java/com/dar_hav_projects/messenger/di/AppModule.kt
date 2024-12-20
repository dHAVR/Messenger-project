package com.dar_hav_projects.messenger.di

import com.dar_hav_projects.messenger.MainActivity
import com.dar_hav_projects.messenger.domens.actions.FirebaseRepository
import com.dar_hav_projects.messenger.encryption.Encryption
import com.dar_hav_projects.messenger.view_models.AccountViewModel
import com.dar_hav_projects.messenger.view_models.ChatsViewModel
import com.dar_hav_projects.messenger.view_models.ContactsViewModel
import com.dar_hav_projects.messenger.view_models.MessagesViewModel
import com.dar_hav_projects.messenger.view_models.SignViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    NetworkActionsModule::class,
    ContextModule::class,
    EncryptionModule::class,
    MessagesRepositoryModule::class
])
interface AppComponent {
    fun inject(signViewModel: SignViewModel)
    fun inject(chatsViewModel: ChatsViewModel)
    fun inject(accountViewModel: AccountViewModel)
    fun inject(encryptionModule: Encryption)
    fun inject(messagesViewModel: MessagesViewModel)
    fun inject(contactsViewModel: ContactsViewModel)
    fun inject(mainActivity: MainActivity)
    fun inject(firebaseRepository: FirebaseRepository)

}