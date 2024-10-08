package com.dar_hav_projects.messenger.ui.small_composables

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.dar_hav_projects.messenger.R
import com.dar_hav_projects.messenger.ui.screens.signInUp.FieldValidStatus
import com.dar_hav_projects.messenger.ui.theme.MessengerTheme
import com.dar_hav_projects.messenger.ui.theme.body1
import com.dar_hav_projects.messenger.ui.theme.meta1
import com.dar_hav_projects.messenger.ui.theme.sub1
import com.dar_hav_projects.messenger.ui.theme.sub2

@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    title: String,
    password: String,
    fieldValidStatus: FieldValidStatus,
    showValidation: Boolean = true,
    imeAction: ImeAction,
    onPasswordChange: (String) -> Unit,
){
    var showPassword by remember { mutableStateOf(false) }
    val localFocusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    MessengerTheme{
        Column(
            modifier = modifier
        ) {
            Text(
                text = title,
                style = sub2.copy(
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                ),
            )
            TextField(
                value = password,
                onValueChange = onPasswordChange,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),

                textStyle = body1.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Normal
                ),
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = imeAction
                ),
                keyboardActions = KeyboardActions(
                    onNext = { localFocusManager.moveFocus(FocusDirection.Down) },
                    onDone = { keyboardController?.hide() }
                ),
                trailingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                showPassword = !showPassword
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = if (showPassword) R.drawable.eye else R.drawable.eye_off),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .size(dimensionResource(id = R.dimen.icon_size))
                            )
                        }
                        if (showValidation){
                            if (fieldValidStatus.isValid){
                                Icon(
                                    painter = painterResource(id = R.drawable.check),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .padding(end = 12.dp)
                                        .size(dimensionResource(id = R.dimen.icon_size))
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.error),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier
                                        .padding(end = 12.dp)
                                        .size(dimensionResource(id = R.dimen.icon_size))
                                )
                            }
                        }
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .border(
                        2.dp,
                        color = if (showValidation && !fieldValidStatus.isValid) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp)
                    ),
                shape = RoundedCornerShape(12.dp)
            )
            if (showValidation && !fieldValidStatus.isValid){
                Text(
                    text = fieldValidStatus.errorMessage,
                    style = meta1.copy(
                        color = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier
                        .padding(top = 8.dp)
                )
            }
        }
    }
}