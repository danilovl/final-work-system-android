package com.finalworksystem.presentation.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.infrastructure.popup.PopupMessageService
import com.finalworksystem.infrastructure.session.SessionManager
import com.finalworksystem.presentation.view_model.auth.AuthViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    popupMessageService: PopupMessageService,
    sessionManager: SessionManager = koinInject()
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val loginState by authViewModel.loginState.collectAsState()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    var showLanguageMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            onLoginSuccess()
        }
    }

    LaunchedEffect(loginState) {
        when (loginState) {
            is AuthViewModel.LoginState.Success -> {
                authViewModel.resetLoginState()
            }
            is AuthViewModel.LoginState.Error -> {
                authViewModel.resetLoginState()
            }
            else -> {
                // nothing
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Box {
                IconButton(onClick = { showLanguageMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = stringResource(R.string.select_language)
                    )
                }
                DropdownMenu(
                    expanded = showLanguageMenu,
                    onDismissRequest = { showLanguageMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.language_en)) },
                        onClick = {
                            scope.launch {
                                sessionManager.saveLanguage("en")
                                showLanguageMenu = false

                                popupMessageService.showMessage(
                                    context.getString(R.string.language_changed),
                                    PopupMessageService.MessageLevel.SUCCESS
                                )
                            }
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.language_ru)) },
                        onClick = {
                            scope.launch {
                                sessionManager.saveLanguage("ru")
                                showLanguageMenu = false

                                popupMessageService.showMessage(
                                    context.getString(R.string.language_changed),
                                    PopupMessageService.MessageLevel.SUCCESS
                                )
                            }
                        }
                    )
                }
            }
        }

        Text(
            text = stringResource(R.string.login),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(stringResource(R.string.username)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (username.isNotBlank() && password.isNotBlank()) {
                    authViewModel.login(username, password)
                } else {
                    popupMessageService.showMessage(
                        context.getString(R.string.login_error_empty), 
                        PopupMessageService.MessageLevel.ERROR
                    )
                }
            },
            enabled = loginState !is AuthViewModel.LoginState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (loginState is AuthViewModel.LoginState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(end = 8.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            }
            Text(stringResource(R.string.login))
        }
    }
}
