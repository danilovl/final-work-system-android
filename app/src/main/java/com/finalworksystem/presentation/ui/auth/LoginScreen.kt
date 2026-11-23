package com.finalworksystem.presentation.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.finalworksystem.R
import com.finalworksystem.infrastructure.locale.AppLanguage
import com.finalworksystem.infrastructure.popup.PopupMessageService
import com.finalworksystem.presentation.view_model.auth.AuthViewModel
import com.finalworksystem.ui.designsystem.AppButton
import com.finalworksystem.ui.designsystem.AppButtonSize
import com.finalworksystem.ui.designsystem.AppButtonVariant
import com.finalworksystem.ui.designsystem.AppCard
import com.finalworksystem.ui.designsystem.AppDropdownMenu
import com.finalworksystem.ui.designsystem.AppDropdownMenuItem
import com.finalworksystem.ui.designsystem.AppIcon
import com.finalworksystem.ui.designsystem.AppIconButton
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.designsystem.AppTextField
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    popupMessageService: PopupMessageService
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val loginState by authViewModel.loginState.collectAsStateWithLifecycle()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsStateWithLifecycle()

    var showLanguageMenu by remember { mutableStateOf(false) }
    val resources = LocalResources.current
    val isLoading = loginState is AuthViewModel.LoginState.Loading

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

    val submit: () -> Unit = {
        if (username.isNotBlank() && password.isNotBlank()) {
            authViewModel.login(username, password)
        } else {
            popupMessageService.showMessage(
                resources.getString(R.string.login_error_empty),
                PopupMessageService.MessageLevel.ERROR
            )
        }
    }

    val selectLanguage: (String) -> Unit = { code ->
        scope.launch {
            AppLanguage.set(code)
            showLanguageMenu = false

            popupMessageService.showMessage(
                resources.getString(R.string.language_changed),
                PopupMessageService.MessageLevel.SUCCESS
            )
        }
    }

    val colors = AppTheme.colors

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            // Gradient hero
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(listOf(colors.primary, colors.primaryGradientEnd)),
                        shape = RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp)
                    )
                    .statusBarsPadding()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 72.dp)
            ) {
                Box(modifier = Modifier.align(Alignment.TopEnd)) {
                    AppIconButton(
                        icon = AppIcons.Language,
                        contentDescription = stringResource(R.string.select_language),
                        onClick = { showLanguageMenu = true },
                        tint = colors.onPrimary,
                        containerColor = colors.onPrimary.copy(alpha = 0.16f)
                    )
                    AppDropdownMenu(
                        expanded = showLanguageMenu,
                        onDismissRequest = { showLanguageMenu = false }
                    ) {
                        AppDropdownMenuItem(
                            text = stringResource(R.string.language_en),
                            onClick = { selectLanguage("en") }
                        )
                        AppDropdownMenuItem(
                            text = stringResource(R.string.language_ru),
                            onClick = { selectLanguage("ru") }
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(104.dp)
                            .background(colors.onPrimary.copy(alpha = 0.16f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_launcher_foreground),
                            contentDescription = null,
                            modifier = Modifier.size(104.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    AppText(
                        text = stringResource(R.string.app_name),
                        style = AppTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.onPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Form card overlapping the hero
            AppCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-44).dp),
                shape = AppTheme.shapes.extraLarge,
                contentPadding = PaddingValues(24.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    AppText(
                        text = stringResource(R.string.login),
                        style = AppTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    AppTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { AppText(stringResource(R.string.username)) },
                        leadingIcon = { AppIcon(AppIcons.Person, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    AppTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { AppText(stringResource(R.string.password)) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { if (!isLoading) submit() }),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    AppButton(
                        text = stringResource(R.string.login),
                        onClick = submit,
                        enabled = !isLoading,
                        loading = isLoading,
                        variant = AppButtonVariant.Primary,
                        size = AppButtonSize.Large,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}
