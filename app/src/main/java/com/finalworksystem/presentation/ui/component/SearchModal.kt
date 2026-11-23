package com.finalworksystem.presentation.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.finalworksystem.R
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
fun SearchModal(
    isVisible: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onDismiss: () -> Unit,
    placeholder: String? = null
) {
    if (isVisible) {
        val submit = {
            onSearch(searchQuery)
            onDismiss()
        }

        AppDialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppIconBadge(icon = AppIcons.Search)
                    Spacer(modifier = Modifier.width(12.dp))
                    AppText(
                        text = stringResource(R.string.search),
                        style = AppTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    AppIconButton(
                        icon = AppIcons.Close,
                        contentDescription = stringResource(R.string.close),
                        onClick = onDismiss,
                        variant = AppIconButtonVariant.Tonal,
                        size = 36.dp,
                        iconSize = 18.dp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                AppTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    label = { AppText(stringResource(R.string.search)) },
                    placeholder = { AppText(placeholder ?: stringResource(R.string.search_placeholder)) },
                    leadingIcon = {
                        AppIcon(
                            imageVector = AppIcons.Search,
                            contentDescription = stringResource(R.string.search)
                        )
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { submit() }),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 1,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AppButton(
                        text = stringResource(R.string.cancel),
                        onClick = onDismiss,
                        variant = AppButtonVariant.Outline,
                        modifier = Modifier.weight(1f)
                    )
                    AppButton(
                        text = stringResource(R.string.search),
                        onClick = submit,
                        variant = AppButtonVariant.Primary,
                        leadingIcon = AppIcons.Search,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
