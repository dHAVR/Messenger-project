package com.dar_hav_projects.messenger.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dar_hav_projects.messenger.R
import com.dar_hav_projects.messenger.ui.small_composables.RoundButton
import com.dar_hav_projects.messenger.ui.theme.body1
import com.dar_hav_projects.messenger.ui.theme.h2
import com.dar_hav_projects.messenger.utils.Routes

@Composable
fun OnBoardingScreen( onNavigate:(String) -> Unit){

    val isSystemInDarkTheme = isSystemInDarkTheme()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,

    ){ paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally,

        ) {
            Column(
                modifier = Modifier
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Icon(
                    painter = if (isSystemInDarkTheme) {
                        painterResource(id = R.drawable.ic_on_boarding_dark)
                    } else {
                        painterResource(id = R.drawable.ic_on_boarding_light)
                    },
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(300.dp)
                        .padding(bottom = 50.dp)

                )

                Text(
                    "Connect easily with your family and friends over countries",
                    style = h2,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 30.dp)
                )
            }



            Column(
                modifier = Modifier
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    "Terms & Privacy Policy",
                    style = body1,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .clickable {

                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                RoundButton(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    title = "Start Messaging",
                    onClick = {
                        onNavigate(Routes.SignIn.name)
                    }
                )
            }


        }
    }

}