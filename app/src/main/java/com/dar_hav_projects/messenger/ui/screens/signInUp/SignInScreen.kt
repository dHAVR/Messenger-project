package com.dar_hav_projects.messenger.ui.screens.signInUp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dar_hav_projects.messenger.R
import com.dar_hav_projects.messenger.domens.models.IsSignedEnum
import com.dar_hav_projects.messenger.ui.small_composables.EmailTextField
import com.dar_hav_projects.messenger.ui.small_composables.LoadingDialog
import com.dar_hav_projects.messenger.ui.small_composables.PasswordTextField
import com.dar_hav_projects.messenger.ui.small_composables.RoundButton
import com.dar_hav_projects.messenger.ui.small_composables.TopAppBarWithBackAction
import com.dar_hav_projects.messenger.ui.theme.Blue300
import com.dar_hav_projects.messenger.ui.theme.Blue400
import com.dar_hav_projects.messenger.ui.theme.body1
import com.dar_hav_projects.messenger.ui.theme.body2
import com.dar_hav_projects.messenger.ui.theme.h2
import com.dar_hav_projects.messenger.ui.theme.meta1
import com.dar_hav_projects.messenger.utils.Routes
import com.dar_hav_projects.messenger.utils.appComponent
import com.dar_hav_projects.messenger.view_models.SignViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SignInScreen( onNavigate:(String) -> Unit) {

    val viewModel: SignViewModel = viewModel(
        factory = SignViewModel.provideFactory(
            LocalContext.current.appComponent(),
            owner = LocalSavedStateRegistryOwner.current
        )
    )

    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val user by viewModel.user
    val coroutineScope = rememberCoroutineScope()
    var showValidation by remember { mutableStateOf(false) }
    var isEmailValid by remember { mutableStateOf(true) }
    var isPasswordValid by remember { mutableStateOf(true) }



    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBarWithBackAction(){
                onNavigate(Routes.OnBoarding.name)
            }
        }
    ) {
            paddingValues ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ){
            Text(
                text = "Enter Your"+'\n'+" Email and Password",
                style = h2,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(50.dp))
            
            EmailTextField(
                email = user.email ,
                title = "Email",
                isValid = isEmailValid,
                showValidation = showValidation,
                onEmailChanged = { newEmail ->
                    viewModel.updateUser(user.copy(email = newEmail))
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            PasswordTextField(
                password = user.password,
                title = "Password",
                showValidation = showValidation,
                onPasswordChange = { newPassword ->
                    viewModel.updateUser(user.copy(password = newPassword))
                },
                fieldValidStatus = FieldValidStatus(
                    isValid = isPasswordValid,
                    errorMessage = "*Must be as minimum 8 digits long, alphanumeric, and with at least 1 special character"
                ),
                imeAction = ImeAction.Done,
                modifier = Modifier
                    .fillMaxWidth()
            ) 
            Spacer(modifier = Modifier.height(20.dp))
            
            RoundButton(title = "Sign In") {
                isEmailValid = viewModel.isEmailValid()
                isPasswordValid = viewModel.isPasswordValid(user.password)
                showValidation = true
                if (isEmailValid && isPasswordValid) {
                    keyboardController?.hide()
                    coroutineScope.launch(Dispatchers.Default) {
                        viewModel.signIn()
                            .onSuccess { res->
                                withContext(Dispatchers.Main) {
                                    viewModel.closeLoadingDialog()
                                    withContext(Dispatchers.Main) {
                                        when(res){
                                            IsSignedEnum.IsSigned ->{
                                                onNavigate(Routes.Main.name)
                                            }
                                            IsSignedEnum.DoNotVerifyEmail ->{
                                                onNavigate(Routes.VerifyEmail.name)
                                            }
                                            IsSignedEnum.DoNotHaveNickname ->{
                                                onNavigate(Routes.UserInfo.name)
                                            }
                                            IsSignedEnum.IsNotSigned ->{
                                                onNavigate(Routes.SignIn.name)
                                            }

                                        }

                                    }

                                }
                            }
                            .onFailure {
                                viewModel.closeLoadingDialog()
                                snackbarHostState.showSnackbar(it.message.toString())
                            }
                    }
                }
            }
            
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Text(
                    text = "Don`t have an account?",
                    style = body1.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                )
                TextButton(
                    onClick = {
                        onNavigate(Routes.SignUp.name)
                    },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text ="Sign up",
                        style = body1.copy(
                            fontWeight = FontWeight.Bold,
                            color = Blue300,
                        )
                    )
                }
            }

        }
    }

    if (viewModel.showLoadingDialog.value) {
        LoadingDialog()
    }
}

data class FieldValidStatus(
    val isValid: Boolean = false,
    val errorMessage: String = ""
)


