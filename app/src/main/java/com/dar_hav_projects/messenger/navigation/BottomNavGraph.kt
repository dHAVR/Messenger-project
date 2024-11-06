package com.dar_hav_projects.messenger.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dar_hav_projects.messenger.ui.screens.home.ChatsScreen
import com.dar_hav_projects.messenger.ui.screens.home.ContactsScreen
import com.dar_hav_projects.messenger.ui.screens.home.SettingsScreen
import com.dar_hav_projects.messenger.utils.Routes
import com.dar_hav_projects.messenger.utils.appComponent
import com.dar_hav_projects.messenger.view_models.ChatsViewModel
import com.dar_hav_projects.messenger.view_models.ContactsViewModel

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    mainNavController: NavHostController,
    onNavigate: (String) -> Unit
) {
    val chatViewModel: ChatsViewModel = viewModel(
        key = "Chat",
        factory = ChatsViewModel.provideFactory(
            LocalContext.current.appComponent(),
            owner = LocalSavedStateRegistryOwner.current
        )
    )

    val contactsViewModel: ContactsViewModel = viewModel(
        key = "Contact",
        factory = ContactsViewModel.provideFactory(
            LocalContext.current.appComponent(),
            owner = LocalSavedStateRegistryOwner.current
        )
    )

    NavHost(navController = navController, startDestination = Routes.Chats.name) {
        composable(Routes.Chats.name) {
            ChatsScreen(chatViewModel)
        }
        composable(Routes.Settings.name) {
            SettingsScreen()
        }
        composable(Routes.Contacts.name) {
            ContactsScreen(contactsViewModel)
        }
    }

}