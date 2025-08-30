package com.finalworksystem.presentation.ui.work.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalworksystem.domain.common.util.DateUtils
import com.finalworksystem.domain.work.model.Work
import com.finalworksystem.presentation.ui.component.BaseCard

@Composable
fun WorkItem(
    work: Work,
    onClick: () -> Unit
) {
    BaseCard(
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = work.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Type: ${work.type.name}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Status: ${work.status.name}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Deadline: ${DateUtils.formatToYmd(work.deadline)}",
                style = MaterialTheme.typography.bodySmall
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Author: ${work.author?.fullName ?: work.author?.let { "${it.firstname} ${it.lastname}" } ?: "N/A"}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "ID: ${work.id}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
