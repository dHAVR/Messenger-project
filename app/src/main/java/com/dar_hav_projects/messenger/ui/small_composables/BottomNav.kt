package com.dar_hav_projects.messenger.ui.small_composables


import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dar_hav_projects.messenger.ui.modelsUI.BottomNavItem
import com.dar_hav_projects.messenger.utils.Routes


@Composable
fun BottomNav(
    navController: NavController
) {
    val context = LocalContext.current

    val listItems = listOf(
        BottomNavItem.ContactsItem(context),
        BottomNavItem.ChatsItem(context),
        BottomNavItem.SettingsItem(context)
    )

    BottomNavigation(
        modifier = Modifier
            .fillMaxWidth(),
        backgroundColor = MaterialTheme.colorScheme.background
    ) {
        val backstackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backstackEntry?.destination?.route

        listItems.forEach { bottomNavItem ->
            val isSelected = when {
                currentRoute == bottomNavItem.route -> true
                else -> false
            }

            val iconSize by animateDpAsState(targetValue = if (isSelected) 24.dp else 20.dp)

            val labelOpacity by animateFloatAsState(targetValue = if (isSelected) 1f else 0.5f)

            BottomNavigationItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        when(bottomNavItem) {
                            is BottomNavItem.ChatsItem -> {
                                navController.navigate(Routes.Chats.name) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }

                            is BottomNavItem.ContactsItem -> {
                                navController.navigate(Routes.Contacts.name) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }

                            is BottomNavItem.SettingsItem -> {
                                navController.navigate(Routes.Settings.name) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }

                    } else {
                        navController.navigate(bottomNavItem.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(painter =
                    painterResource(id = bottomNavItem.icon),
                        contentDescription = "icon",
                        modifier = Modifier.size(iconSize))
                },
                label = {
                    Text(text = bottomNavItem.title,
                        modifier = Modifier.alpha(labelOpacity)
                    )
                },
                selectedContentColor = MaterialTheme.colorScheme.onBackground,
                unselectedContentColor = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
