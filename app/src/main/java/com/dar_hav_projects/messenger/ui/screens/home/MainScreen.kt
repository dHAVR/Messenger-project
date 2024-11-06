package com.dar_hav_projects.messenger.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dar_hav_projects.messenger.navigation.BottomNavGraph
import com.dar_hav_projects.messenger.ui.small_composables.BottomNav

@Composable
fun MainScreen(mainNavController: NavHostController){
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNav(navController = navController)
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .padding(paddingValues)){
            BottomNavGraph(
                navController = navController,
                mainNavController = mainNavController
            ){ route->
                navController.navigate(route)
            }
        }
    }
}