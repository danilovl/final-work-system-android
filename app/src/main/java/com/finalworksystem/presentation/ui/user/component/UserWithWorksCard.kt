package com.finalworksystem.presentation.ui.user.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.finalworksystem.domain.user.model.UserWithWorks
import com.finalworksystem.presentation.ui.component.BaseCard

@Composable
fun UserWithWorksCard(
    userWithWorks: UserWithWorks,
    onNavigateToWorkDetail: (Int) -> Unit
) {
    BaseCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Top
        ) {
            UserAvatar(
                firstname = userWithWorks.user.firstname,
                lastname = userWithWorks.user.lastname,
                userId = userWithWorks.user.id
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = userWithWorks.user.fullName ?: "${userWithWorks.user.firstname} ${userWithWorks.user.lastname}",
                    style = MaterialTheme.typography.titleMedium,
                 )

                if (!userWithWorks.user.degreeBefore.isNullOrBlank() || !userWithWorks.user.degreeAfter.isNullOrBlank()) {
                    val degree = listOfNotNull(
                        userWithWorks.user.degreeBefore,
                        userWithWorks.user.degreeAfter
                    ).joinToString(" ")

                    if (degree.isNotBlank()) {
                        Text(
                            text = degree,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (userWithWorks.user.email.isNotBlank()) {
                    Text(
                        text = userWithWorks.user.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = userWithWorks.user.username,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (userWithWorks.works.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    WorksList(
                        works = userWithWorks.works,
                        onNavigateToWorkDetail = onNavigateToWorkDetail
                    )
                }
            }
        }
    }
}
