package com.finalworksystem.presentation.ui.task.component

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.finalworksystem.R
import com.finalworksystem.domain.work.model.Work
import com.finalworksystem.presentation.ui.component.PagedLazyColumn
import com.finalworksystem.ui.designsystem.AppButton
import com.finalworksystem.ui.designsystem.AppButtonVariant
import com.finalworksystem.ui.designsystem.AppDialog
import com.finalworksystem.ui.designsystem.AppIcon
import com.finalworksystem.ui.designsystem.AppIconButton
import com.finalworksystem.ui.designsystem.AppIconButtonVariant
import com.finalworksystem.ui.designsystem.AppRadioRow
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.designsystem.AppTextField
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

private const val SEARCH_DEBOUNCE_MS = 300L

@Composable
fun WorkSelectionDialog(
    isVisible: Boolean,
    selectedWork: Work?,
    works: Flow<PagingData<Work>>,
    initialQuery: String,
    onSearch: (String) -> Unit,
    onWorkSelected: (Work?) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isVisible) return

    val pagedWorks = works.collectAsLazyPagingItems()
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
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(560.dp)
                .padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppText(
                    text = stringResource(R.string.select_work),
                    style = AppTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.onSurface,
                    modifier = Modifier.weight(1f)
                )
                AppIconButton(
                    icon = AppIcons.Close,
                    contentDescription = stringResource(R.string.close),
                    onClick = onDismiss,
                    variant = AppIconButtonVariant.Tonal
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { AppText(stringResource(R.string.search)) },
                leadingIcon = {
                    AppIcon(
                        imageVector = AppIcons.Search,
                        contentDescription = stringResource(R.string.search),
                        tint = AppTheme.colors.onSurfaceVariant
                    )
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            PagedLazyColumn(
                items = pagedWorks,
                emptyText = stringResource(R.string.no_works_found),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) { work ->
                val isSelected = selectedWork?.id == work.id

                AppRadioRow(
                    selected = isSelected,
                    onClick = { onWorkSelected(work) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        AppText(
                            text = work.title,
                            style = AppTheme.typography.bodyLarge,
                            color = if (isSelected) AppTheme.colors.primary else AppTheme.colors.onSurface,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                        if (!work.shortcut.isNullOrBlank()) {
                            AppText(
                                text = work.shortcut,
                                style = AppTheme.typography.bodySmall,
                                color = AppTheme.colors.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
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
