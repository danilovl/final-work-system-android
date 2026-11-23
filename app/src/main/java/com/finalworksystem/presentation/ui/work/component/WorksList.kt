package com.finalworksystem.presentation.ui.work.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.finalworksystem.R
import com.finalworksystem.domain.work.model.Work
import com.finalworksystem.presentation.ui.component.PagedLazyColumn

@Composable
fun WorksList(
    works: LazyPagingItems<Work>,
    onWorkClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    PagedLazyColumn(
        items = works,
        emptyText = stringResource(R.string.no_works_found),
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 160.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) { work ->
        WorkItem(
            work = work,
            onClick = { onWorkClick(work.id) }
        )
    }
}
