package com.dar_hav_projects.messenger.utils

sealed class Routes(val name: String) {
    data object SignIn : Routes("signIn")
    data object SignUp : Routes("signUp")
    data object OnBoarding : Routes("onboarding")
    data object Main : Routes("main")
    data object UserInfo : Routes("user_info")
    data object VerifyEmail : Routes("verify_email")
    data object Splash : Routes("splash")

    data object Contacts : Routes("contacts")
    data object Chats : Routes("chats")
    data object Settings : Routes("settings")
}