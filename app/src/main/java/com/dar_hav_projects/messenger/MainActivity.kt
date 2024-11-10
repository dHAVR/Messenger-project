package com.dar_hav_projects.messenger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dar_hav_projects.messenger.domens.actions.I_NetworkActions
import com.dar_hav_projects.messenger.domens.models.IsSignedEnum
import com.dar_hav_projects.messenger.ui.screens.OnBoardingScreen
import com.dar_hav_projects.messenger.ui.screens.Splash
import com.dar_hav_projects.messenger.ui.screens.home.MainScreen
import com.dar_hav_projects.messenger.ui.screens.signInUp.SignInScreen
import com.dar_hav_projects.messenger.ui.screens.signInUp.SignUpScreen
import com.dar_hav_projects.messenger.ui.screens.signInUp.UserInfoScreen
import com.dar_hav_projects.messenger.ui.screens.signInUp.VerifyEmailScreen
import com.dar_hav_projects.messenger.ui.theme.MessengerTheme
import com.dar_hav_projects.messenger.utils.Routes
import com.dar_hav_projects.messenger.utils.appComponent
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.hybrid.HybridConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController

    @Inject
    lateinit var networkActions: I_NetworkActions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeTink()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        this.appComponent().inject(this)
        enableEdgeToEdge()
        setContent {
            MessengerTheme {
                val nextRoute = remember { mutableStateOf<String?>(Routes.Splash.name) }
                LaunchedEffect(Unit) {
                    delay(2000L)
                    val isSigned = networkActions.isSignedAsync()
                    nextRoute.value = when (isSigned) {
                        IsSignedEnum.IsSigned -> Routes.Main.name
                        IsSignedEnum.IsNotSigned -> Routes.OnBoarding.name
                        IsSignedEnum.DoNotHaveNickname -> Routes.UserInfo.name
                        IsSignedEnum.DoNotVerifyEmail -> Routes.VerifyEmail.name
                    }
                }
                if (nextRoute.value != null) {
                    navController = rememberNavController()
                    Box(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
                        NavHost(navController = navController, startDestination = nextRoute.value!!){
                            composable(Routes.SignIn.name) {
                                SignInScreen(){ route ->
                                    navController.navigate(route)
                                }
                            }
                            composable(Routes.SignUp.name){
                                SignUpScreen(){ route ->
                                    navController.navigate(route)
                                }
                            }
                            composable(Routes.Main.name) {
                                MainScreen(navController)
                            }

                            composable(Routes.OnBoarding.name){
                                OnBoardingScreen(){ route ->
                                    navController.navigate(route)
                                }
                            }

                            composable(Routes.UserInfo.name){
                                UserInfoScreen(){ route ->
                                    navController.navigate(route)
                                }
                            }

                            composable(Routes.VerifyEmail.name){
                                VerifyEmailScreen(){ route ->
                                    navController.navigate(route)
                                }
                            }

                            composable(Routes.Splash.name){
                                Splash()
                            }

                        }
                    }
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (this::navController.isInitialized) {
            CoroutineScope(Dispatchers.Main).launch {
                val currentRoute = navController.currentBackStackEntry?.destination?.route
                val isSigned = networkActions.isSignedAsync()

                val nextRoute = when (isSigned) {
                    IsSignedEnum.IsSigned -> Routes.Main.name
                    IsSignedEnum.DoNotHaveNickname -> Routes.UserInfo.name
                    IsSignedEnum.DoNotVerifyEmail -> Routes.VerifyEmail.name
                    else -> Routes.SignIn.name
                }

                if (currentRoute != nextRoute) {
                    navController.navigate(nextRoute)
                }
            }
        }
    }

    fun initializeTink() {
        AeadConfig.register()
        HybridConfig.register()
    }


}



