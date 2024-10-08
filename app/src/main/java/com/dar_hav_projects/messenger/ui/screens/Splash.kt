package com.dar_hav_projects.messenger.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dar_hav_projects.messenger.R
import com.dar_hav_projects.messenger.ui.small_composables.TopAppBarWithTitle
import com.dar_hav_projects.messenger.ui.theme.h2
import com.dar_hav_projects.messenger.utils.Routes

@Composable
fun Splash(){
    val isDarkTheme = isSystemInDarkTheme()
    Scaffold(
        contentWindowInsets = WindowInsets(0,0,0,0),

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .consumeWindowInsets(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Icon(
                painter = if(isDarkTheme)
                    painterResource(id = R.drawable.ic_messenger_dark)
                else
                    painterResource(id = R.drawable.ic_messenger_light),
                contentDescription = "Logo",
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(170.dp)
                    .padding(bottom = 50.dp)

            )

            Text(
                text = "Bringing People Closer",
                style = h2,
                color = MaterialTheme.colorScheme.onBackground
                )
        }
    }
}