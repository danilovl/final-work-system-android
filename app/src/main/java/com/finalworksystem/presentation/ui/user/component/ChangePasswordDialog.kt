package com.finalworksystem.presentation.ui.user.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.finalworksystem.R
import com.finalworksystem.presentation.view_model.user.ChangePasswordViewModel.State
import com.finalworksystem.ui.designsystem.AppButton
import com.finalworksystem.ui.designsystem.AppButtonVariant
import com.finalworksystem.ui.designsystem.AppDialog
import com.finalworksystem.ui.designsystem.AppIcon
import com.finalworksystem.ui.designsystem.AppIconBadge
import com.finalworksystem.ui.designsystem.AppIconButton
import com.finalworksystem.ui.designsystem.AppIconButtonVariant
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.designsystem.AppTextField
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun ChangePasswordDialog(
    state: State,
    onSubmit: (currentPassword: String, newPassword: String, confirmPassword: String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Plain remember: passwords must not end up in the saved instance state.
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val isSubmitting = state == State.Submitting

    val dismiss = {
        if (!isSubmitting) onDismiss()
    }
    val submit = { onSubmit(currentPassword, newPassword, confirmPassword) }

    AppDialog(
        onDismissRequest = dismiss,
        modifier = modifier,
        properties = DialogProperties(dismissOnClickOutside = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppIconBadge(icon = AppIcons.Lock)
                Spacer(modifier = Modifier.width(12.dp))
                AppText(
                    text = stringResource(R.string.change_password),
                    style = AppTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                AppIconButton(
                    icon = AppIcons.Close,
                    contentDescription = stringResource(R.string.close),
                    onClick = dismiss,
                    variant = AppIconButtonVariant.Tonal,
                    size = 36.dp,
                    iconSize = 18.dp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                PasswordField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = stringResource(R.string.current_password),
                    enabled = !isSubmitting,
                    imeAction = ImeAction.Next
                )
                PasswordField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = stringResource(R.string.new_password),
                    enabled = !isSubmitting,
                    imeAction = ImeAction.Next
                )
                PasswordField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = stringResource(R.string.confirm_new_password),
                    enabled = !isSubmitting,
                    imeAction = ImeAction.Done,
                    onDone = submit
                )
            }

            val error = state as? State.Error
            AnimatedVisibility(visible = error != null) {
                if (error != null) {
                    ErrorMessage(text = error.serverMessage ?: stringResource(error.messageRes))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppButton(
                    text = stringResource(R.string.cancel),
                    onClick = dismiss,
                    variant = AppButtonVariant.Outline,
                    enabled = !isSubmitting,
                    modifier = Modifier.weight(1f)
                )
                AppButton(
                    text = stringResource(R.string.save),
                    onClick = submit,
                    loading = isSubmitting,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean,
    imeAction: ImeAction,
    onDone: () -> Unit = {}
) {
    var visible by rememberSaveable { mutableStateOf(false) }

    AppTextField(
        value = value,
        onValueChange = onValueChange,
        label = { AppText(label) },
        enabled = enabled,
        singleLine = true,
        leadingIcon = { AppIcon(AppIcons.Lock, null, Modifier.size(20.dp)) },
        trailingIcon = {
            AppIconButton(
                icon = if (visible) AppIcons.VisibilityOff else AppIcons.Visibility,
                contentDescription = stringResource(if (visible) R.string.hide_password else R.string.show_password),
                onClick = { visible = !visible },
                size = 36.dp,
                iconSize = 20.dp
            )
        },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ErrorMessage(text: String) {
    val colors = AppTheme.colors
    Row(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .background(colors.errorContainer, AppTheme.shapes.medium)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppIcon(AppIcons.Info, null, Modifier.size(18.dp), tint = colors.onErrorContainer)
        Spacer(modifier = Modifier.width(10.dp))
        AppText(text = text, style = AppTheme.typography.bodyMedium, color = colors.onErrorContainer)
    }
}
