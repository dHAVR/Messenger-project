package com.dar_hav_projects.messenger.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dar_hav_projects.messenger.ui.screens.MainScreen
import com.dar_hav_projects.messenger.ui.screens.OnBoardingScreen
import com.dar_hav_projects.messenger.ui.screens.Splash
import com.dar_hav_projects.messenger.ui.screens.signInUp.SignInScreen
import com.dar_hav_projects.messenger.ui.screens.signInUp.SignUpScreen
import com.dar_hav_projects.messenger.ui.screens.signInUp.UserInfoScreen
import com.dar_hav_projects.messenger.ui.screens.signInUp.VerifyEmailScreen
import com.dar_hav_projects.messenger.utils.Routes

@Composable
fun NavigationGraph(navController: NavHostController, nextRoute: String, onNavigate:(String) -> Unit) {

    NavHost(navController = navController, startDestination = nextRoute ){
        composable(Routes.SignIn.name) {
            SignInScreen(){ route ->
                onNavigate(route)
            }
        }
        composable(Routes.SignUp.name){
            SignUpScreen(){ route ->
                onNavigate(route)
            }
        }
        composable(Routes.Main.name){
            MainScreen(){ route ->
                onNavigate(route)
            }
        }

        composable(Routes.OnBoarding.name){
            OnBoardingScreen(){ route ->
                onNavigate(route)
            }
        }

        composable(Routes.UserInfo.name){
            UserInfoScreen(){ route ->
                onNavigate(route)
            }
        }

        composable(Routes.VerifyEmail.name){
            VerifyEmailScreen(){ route ->
                onNavigate(route)
            }
        }

        composable(Routes.Splash.name){
            Splash()
        }

    }
}