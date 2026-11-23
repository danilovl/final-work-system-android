package com.finalworksystem.presentation.ui.conversation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.domain.conversation.model.ConversationParticipant
import com.finalworksystem.presentation.ui.user.component.UserAvatar
import com.finalworksystem.ui.designsystem.AppBottomSheet
import com.finalworksystem.ui.designsystem.AppButton
import com.finalworksystem.ui.designsystem.AppButtonVariant
import com.finalworksystem.ui.designsystem.AppCard
import com.finalworksystem.ui.designsystem.AppCardStyle
import com.finalworksystem.ui.designsystem.AppIconBadge
import com.finalworksystem.ui.designsystem.AppIconButton
import com.finalworksystem.ui.designsystem.AppIconButtonVariant
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun ParticipantsList(participants: List<ConversationParticipant>) {
    var showParticipantsSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        AppButton(
            text = stringResource(R.string.participants_count, participants.size),
            onClick = { showParticipantsSheet = true },
            variant = AppButtonVariant.Secondary,
            leadingIcon = AppIcons.Person,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showParticipantsSheet) {
        AppBottomSheet(
            onDismissRequest = { showParticipantsSheet = false }
        ) {
            ParticipantsFullList(
                participants = participants,
                onDismiss = { showParticipantsSheet = false }
            )
        }
    }
}

@Composable
fun ParticipantsFullList(
    participants: List<ConversationParticipant>,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIconBadge(icon = AppIcons.Groups, size = 40)

            Spacer(modifier = Modifier.width(12.dp))

            AppText(
                text = stringResource(R.string.participants_count, participants.size),
                style = AppTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
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

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(participants) { participant ->
                ParticipantFullWidthItem(participant = participant)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ParticipantFullWidthItem(participant: ConversationParticipant) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        style = AppCardStyle.Tonal,
        shape = AppTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserAvatar(
                firstname = participant.user.firstname,
                lastname = participant.user.lastname,
                userId = participant.user.id,
                modifier = Modifier.size(44.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                AppText(
                    text = participant.user.fullName ?: "${participant.user.firstname} ${participant.user.lastname}",
                    style = AppTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                participant.user.degreeBefore?.let { degree ->
                    AppText(
                        text = degree,
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colors.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ParticipantItem(participant: ConversationParticipant) {
    AppCard(
        modifier = Modifier.width(120.dp),
        style = AppCardStyle.Tonal,
        shape = AppTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UserAvatar(
                firstname = participant.user.firstname,
                lastname = participant.user.lastname,
                userId = participant.user.id,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            AppText(
                text = "${participant.user.firstname} ${participant.user.lastname}",
                style = AppTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            participant.user.degreeBefore?.let { degree ->
                AppText(
                    text = degree,
                    style = AppTheme.typography.labelSmall,
                    color = AppTheme.colors.onSurfaceVariant
                )
            }
        }
    }
}
