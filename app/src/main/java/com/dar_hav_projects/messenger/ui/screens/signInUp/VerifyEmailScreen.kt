package com.dar_hav_projects.messenger.ui.screens.signInUp

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dar_hav_projects.messenger.R
import com.dar_hav_projects.messenger.ui.small_composables.RoundButton
import com.dar_hav_projects.messenger.ui.small_composables.TopAppBarWithBackAction
import com.dar_hav_projects.messenger.ui.theme.h2
import com.dar_hav_projects.messenger.utils.Routes
import com.dar_hav_projects.messenger.utils.appComponent
import com.dar_hav_projects.messenger.view_models.SignViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun VerifyEmailScreen(onNavigate:(String) -> Unit){

    val snackbarHostState = remember { SnackbarHostState() }
    val isDarkTheme = isSystemInDarkTheme()
    val coroutineScope = rememberCoroutineScope()

    val viewModel: SignViewModel = viewModel(
        factory = SignViewModel.provideFactory(
            LocalContext.current.appComponent(),
            owner = LocalSavedStateRegistryOwner.current
        )
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBarWithBackAction {
                onNavigate(Routes.OnBoarding.name)
            }
        }
    ) { paddingValues ->

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .consumeWindowInsets(paddingValues)
                .padding(horizontal = 24.dp)
                .padding(vertical = 20.dp)
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "In order to use Messenger you have to verify your email",
                style = h2.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = if (isDarkTheme)
                    painterResource(id = R.drawable.ic_email_dark)
                else
                    painterResource(id = R.drawable.ic_email_light),
                contentDescription = null,
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Inside
            )

            Spacer(modifier = Modifier.height(20.dp))

            RoundButton(title = "Send Verification") {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Verification letter was sent!")
                }
                coroutineScope.launch(Dispatchers.Default) {
                    viewModel.verifyEmail()
                        .onSuccess{
                            onNavigate(Routes.UserInfo.name)
                    }
                        .onFailure {
                            snackbarHostState.showSnackbar(it.message.toString())
                    }
                }
            }
        }
    }
}