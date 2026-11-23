package com.finalworksystem.presentation.ui.work.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.finalworksystem.R
import com.finalworksystem.presentation.ui.component.PagedLazyColumn
import com.finalworksystem.presentation.view_model.work.WorkDetailViewModel

@Composable
fun VersionTabContent(
    workDetailViewModel: WorkDetailViewModel,
    workId: Int
) {
    val versions = workDetailViewModel.versions.collectAsLazyPagingItems()

    LaunchedEffect(workId) {
        workDetailViewModel.loadVersionsForWork(workId)
    }

    PagedLazyColumn(
        items = versions,
        emptyText = stringResource(R.string.no_versions_available),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) { version ->
        VersionCard(version = version)
    }
}
