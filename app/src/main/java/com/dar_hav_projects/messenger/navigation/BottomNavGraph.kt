package com.dar_hav_projects.messenger.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.navArgument
import com.dar_hav_projects.messenger.ui.screens.home.ChatScreen
import com.dar_hav_projects.messenger.ui.screens.home.ChatsListScreen
import com.dar_hav_projects.messenger.ui.screens.home.ContactsListScreen
import com.dar_hav_projects.messenger.ui.screens.home.SearchContactScreen
import com.dar_hav_projects.messenger.ui.screens.home.SettingsScreen
import com.dar_hav_projects.messenger.utils.Routes
import com.dar_hav_projects.messenger.utils.appComponent
import com.dar_hav_projects.messenger.view_models.ChatsViewModel
import com.dar_hav_projects.messenger.view_models.ContactsViewModel
import com.dar_hav_projects.messenger.view_models.MessagesViewModel
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
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


    val messagesViewModel: MessagesViewModel = viewModel(
        key = "Message",
        factory = MessagesViewModel.provideFactory(
            LocalContext.current.appComponent(),
            owner = LocalSavedStateRegistryOwner.current
        )
    )

    NavHost(navController = navController, startDestination = Routes.ChatsList.name) {

        composable(Routes.ChatsList.name) {
            ChatsListScreen(chatViewModel){ route ->
                navController.navigate(route)
            }
        }

        composable(
            route = "${Routes.Chat.name}/{chatID}",
            arguments = listOf(navArgument("chatID") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("chatID")?.let {
                ChatScreen(chatViewModel, it, messagesViewModel)
            }
        }

        composable(Routes.Settings.name) {
            SettingsScreen()
        }
        composable(Routes.ContactsList.name) {
            ContactsListScreen(chatViewModel , contactsViewModel){ route ->
                navController.navigate(route)
            }
        }
        composable(Routes.SearchContact.name) {
            SearchContactScreen(contactsViewModel){ route ->
                navController.navigate(route)
            }
        }
    }

}