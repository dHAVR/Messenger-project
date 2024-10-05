package com.dar_hav_projects.messanger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.dar_hav_projects.messanger.ui.screens.OnBoardingScreen
import com.dar_hav_projects.messanger.ui.theme.MessangerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MessangerTheme {
                OnBoardingScreen()
            }
        }
    }
}

