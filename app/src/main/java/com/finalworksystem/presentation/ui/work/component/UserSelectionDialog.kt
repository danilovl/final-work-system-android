package com.finalworksystem.presentation.ui.work.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.finalworksystem.R
import com.finalworksystem.domain.user.model.User
import com.finalworksystem.domain.user.model.UserWithWorks
import com.finalworksystem.presentation.ui.component.PagedLazyColumn
import com.finalworksystem.ui.designsystem.AppButton
import com.finalworksystem.ui.designsystem.AppButtonVariant
import com.finalworksystem.ui.designsystem.AppDialog
import com.finalworksystem.ui.designsystem.AppIcon
import com.finalworksystem.ui.designsystem.AppIconButton
import com.finalworksystem.ui.designsystem.AppRadioButton
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.designsystem.AppTextField
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

private const val SEARCH_DEBOUNCE_MS = 300L

@Composable
fun UserSelectionDialog(
    title: String,
    isVisible: Boolean,
    selectedUser: User?,
    users: Flow<PagingData<UserWithWorks>>,
    initialQuery: String,
    onSearch: (String) -> Unit,
    onUserSelected: (User?) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isVisible) {
        return
    }

    val pagedUsers = users.collectAsLazyPagingItems()
    var query by remember { mutableStateOf(initialQuery) }

    // Searches while typing, but not on every keystroke.
    LaunchedEffect(query) {
        if (query != initialQuery) {
            delay(SEARCH_DEBOUNCE_MS)
        }
        onSearch(query)
    }

    AppDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.height(550.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppText(
                    text = title,
                    style = AppTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                AppIconButton(
                    icon = AppIcons.Close,
                    contentDescription = stringResource(R.string.close),
                    onClick = onDismiss
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            AppTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { AppText(stringResource(R.string.search)) },
                leadingIcon = {
                    AppIcon(
                        imageVector = AppIcons.Search,
                        contentDescription = stringResource(R.string.search)
                    )
                },
                trailingIcon = if (query.isNotEmpty()) {
                    {
                        AppIconButton(
                            icon = AppIcons.Clear,
                            contentDescription = null,
                            onClick = { query = "" },
                            size = 32.dp,
                            iconSize = 18.dp
                        )
                    }
                } else null,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            PagedLazyColumn(
                items = pagedUsers,
                emptyText = stringResource(R.string.no_users_found),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(0.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                header = {
                    item {
                        UserOption(
                            text = stringResource(R.string.none),
                            isSelected = selectedUser == null,
                            onClick = { onUserSelected(null) }
                        )
                    }
                }
            ) { userWithWorks ->
                val user = userWithWorks.user

                UserOption(
                    text = user.displayName(),
                    secondaryText = user.email,
                    isSelected = selectedUser?.id == user.id,
                    onClick = { onUserSelected(user) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppButton(
                    text = stringResource(R.string.cancel),
                    onClick = onDismiss,
                    variant = AppButtonVariant.Ghost
                )
            }
        }
    }
}

@Composable
private fun UserOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    secondaryText: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppTheme.shapes.small)
            .background(if (isSelected) AppTheme.colors.primaryContainer.copy(alpha = 0.6f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppRadioButton(
            selected = isSelected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            AppText(
                text = text,
                style = AppTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (secondaryText != null) {
                AppText(
                    text = secondaryText,
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun User.displayName(): String {
    val namePart = if (!fullName.isNullOrBlank()) fullName else "$firstname $lastname".trim()

    return buildString {
        if (!degreeBefore.isNullOrBlank()) append("$degreeBefore ")
        append(namePart.ifEmpty { username })
        if (!degreeAfter.isNullOrBlank()) append(" $degreeAfter")
    }
}
