package com.dar_hav_projects.messenger.ui.screens.signInUp

import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.dar_hav_projects.messenger.ui.theme.MessengerTheme
import com.dar_hav_projects.messenger.ui.theme.body1
import com.dar_hav_projects.messenger.ui.theme.h2
import com.dar_hav_projects.messenger.ui.theme.sub2
import com.dar_hav_projects.messenger.utils.Routes
import com.dar_hav_projects.messenger.utils.appComponent
import com.dar_hav_projects.messenger.view_models.SignViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SignUpScreen(onNavigate: (String) -> Unit) {

    val ctx = LocalContext.current
    val viewModel: SignViewModel = viewModel(
        factory = SignViewModel.provideFactory(
            LocalContext.current.appComponent(),
            owner = LocalSavedStateRegistryOwner.current
        )
    )
    val keyboardController = LocalSoftwareKeyboardController.current

    val user by viewModel.user
    val confirmPassword by viewModel.confirmPassword
    val coroutineScope = rememberCoroutineScope()

    val showValidators = remember { mutableStateListOf(false, false, false) }
    var passValidStatus by remember { mutableStateOf(FieldValidStatus()) }
    var confirmPassValidStatus by remember { mutableStateOf(FieldValidStatus()) }
    val snackbarHostState = remember { SnackbarHostState() }


    fun checkPasswordFieldStatus(pass: String): FieldValidStatus {
        if (!viewModel.isPasswordValid(pass)) {
            return FieldValidStatus(errorMessage = "*Must be as minimum 8 digits long, alphanumeric, and with at least 1 special character")
        }
        if (user.password == confirmPassword) {
            passValidStatus = FieldValidStatus(true, "")
            confirmPassValidStatus = FieldValidStatus(true, "")
            return passValidStatus
        } else {
            return FieldValidStatus(errorMessage = "*Passwords are not equal")
        }
    }

    MessengerTheme {
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
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .consumeWindowInsets(paddingValues)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Create an account",
                    style = h2.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "New adventures is waiting for you!",
                    style = sub2.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(modifier = Modifier.height(40.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    EmailTextField(
                        email = user.email,
                        title = "Email",
                        isValid = viewModel.isEmailValid(),
                        showValidation = showValidators[0],
                        onEmailChanged = { newEmail ->
                            viewModel.updateUser(user.copy(email = newEmail))
                            if (!showValidators[0]) {
                                showValidators[0] = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    PasswordTextField(
                        password = user.password,
                        title = "Password",
                        showValidation = showValidators[1],
                        onPasswordChange = { newPassword ->
                            viewModel.updateUser(user.copy(password = newPassword))
                            passValidStatus = FieldValidStatus(
                                viewModel.isPasswordValid(newPassword),
                                "*Must be as minimum 8 digits long, alphanumeric, and with at least 1 special character"
                            )
                            if (!showValidators[1]) {
                                showValidators[1] = true
                            }
                        },
                        fieldValidStatus = passValidStatus,
                        imeAction = ImeAction.Next,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    PasswordTextField(
                        password = confirmPassword,
                        title = "Confirm password",
                        fieldValidStatus = confirmPassValidStatus,
                        showValidation = showValidators[2],
                        onPasswordChange = { newConfirmPassword ->
                            viewModel.updateConfirmPassword(newConfirmPassword)
                            confirmPassValidStatus = checkPasswordFieldStatus(confirmPassword)
                            if (!showValidators[2]) {
                                showValidators[2] = true
                            }
                        },
                        imeAction = ImeAction.Done,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))

                RoundButton(title = "Create an account") {
                    keyboardController?.hide()
                    if (viewModel.isEmailValid() && (confirmPassword == user.password) && passValidStatus.isValid) {
                        coroutineScope.launch(Dispatchers.Default) {
                            viewModel.signUp()
                                .onSuccess {
                                    viewModel.closeLoadingDialog()
                                    withContext(Dispatchers.Main){
                                        onNavigate(Routes.VerifyEmail.name)
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
                        text = "Already have an account?",
                        style = body1.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    )
                    TextButton(
                        onClick = {
                            onNavigate(Routes.SignIn.name)
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Sign in",
                            style = body1.copy(
                                fontWeight = FontWeight.Bold,
                                color = Blue300,
                            )
                        )
                    }
                }

            }

        }
    }

    if (viewModel.showLoadingDialog.value) {
        LoadingDialog()
    }

}