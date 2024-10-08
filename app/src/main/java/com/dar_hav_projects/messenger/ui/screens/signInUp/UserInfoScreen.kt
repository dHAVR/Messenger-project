package com.dar_hav_projects.messenger.ui.screens.signInUp

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dar_hav_projects.messenger.ui.small_composables.TopAppBarWithTitle
import com.dar_hav_projects.messenger.utils.Routes
import com.dar_hav_projects.messenger.utils.appComponent
import com.dar_hav_projects.messenger.view_models.SignViewModel
import com.dar_hav_projects.messenger.R
import com.dar_hav_projects.messenger.ui.small_composables.RoundButton
import com.dar_hav_projects.messenger.ui.theme.body1
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import coil.compose.rememberImagePainter
import com.dar_hav_projects.messenger.ui.small_composables.LoadingDialog
import com.dar_hav_projects.messenger.ui.small_composables.UserInfoTextFields
import com.dar_hav_projects.messenger.ui.theme.h2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun UserInfoScreen(onNavigate: (String) -> Unit){

    val viewModel: SignViewModel = viewModel(
        factory = SignViewModel.provideFactory(
            LocalContext.current.appComponent(),
            owner = LocalSavedStateRegistryOwner.current
        )
    )

    val nickname by viewModel.nickname.collectAsState()
    val name by viewModel.name.collectAsState()
    val surname by viewModel.surname.collectAsState()
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val showValidators = remember { mutableStateListOf(false, false, false) }

    val isDarkTheme = isSystemInDarkTheme()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current


    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            selectedImageUri = uri
        }
    )


    Scaffold(
        contentWindowInsets = WindowInsets(0,0,0,0),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBarWithTitle(title = "Profile") {
                onNavigate(Routes.SignUp.name)
            }
        }

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
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Text(
                text = "Almost done!"+'\n'+" Provide some information about yourself",
                style = h2,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 20.dp)
            )

            Spacer(modifier = Modifier.height(50.dp))

            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.BottomEnd,
            ){
                Image(
                    painter = if (selectedImageUri == null)
                        if(isDarkTheme)
                            painterResource(id = R.drawable.profile_dark)
                        else
                            painterResource(id = R.drawable.profile_light)
                    else
                        rememberImagePainter(data = selectedImageUri) ,
                    contentDescription = null,
                    modifier = Modifier
                        .size(240.dp)
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.surface, CircleShape),
                    contentScale = ContentScale.Crop
                )

                IconButton(
                    onClick = {
                        imagePickerLauncher.launch("image/*")
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.Transparent, CircleShape)
                        .border(1.dp, Color.Transparent, CircleShape),
                ) {
                    Icon(
                        painter =   painterResource(id = R.drawable.ic_add),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            UserInfoTextFields(
                placeholder = "Enter your nickname",
                value = nickname,
                showValidation = showValidators[0],
                imeAction = ImeAction.Next,
                isNotValid = nickname.isEmpty()
            ){
                viewModel.updateNickname(it)
                if (!showValidators[0]) {
                    showValidators[0] = true
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            UserInfoTextFields(
                placeholder ="Enter your name",
                value = name,
                showValidation = showValidators[1],
                imeAction = ImeAction.Next,
                isNotValid = name.isEmpty()
            ){
                viewModel.updateName(it)
                if (!showValidators[1]) {
                    showValidators[1] = true
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            UserInfoTextFields(
                placeholder ="Enter your surname",
                value = surname,
                showValidation = showValidators[2],
                imeAction = ImeAction.Done,
                isNotValid = surname.isEmpty()
            ){
                viewModel.updateSurname(it)
                if (!showValidators[2]) {
                    showValidators[2] = true
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            RoundButton(title = "Let's go!") {
                for (i in showValidators.indices) {
                    showValidators[i] = true
                }
                keyboardController?.hide()
                if (!name.isNullOrEmpty() && !nickname.isNullOrEmpty() && !surname.isNullOrEmpty()) {
                    coroutineScope.launch(Dispatchers.Default) {
                        viewModel.saveUserData(selectedImageUri)
                            .onSuccess {
                                withContext(Dispatchers.Main){
                                    viewModel.closeLoadingDialog()
                                    onNavigate(Routes.Main.name)
                                }
                            }
                            .onFailure {
                                snackbarHostState.showSnackbar(it.message.toString())
                            }
                    }
                }

            }
        }

    }

    if (viewModel.showLoadingDialog.value) {
        LoadingDialog()
    }

}