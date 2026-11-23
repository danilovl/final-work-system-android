package com.finalworksystem.presentation.ui.component.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.domain.user.model.User
import com.finalworksystem.domain.user.model.UserRole
import com.finalworksystem.ui.designsystem.AppDrawerItem
import com.finalworksystem.ui.designsystem.AppDrawerSectionTitle
import com.finalworksystem.ui.designsystem.AppDrawerState
import com.finalworksystem.ui.designsystem.AppIcon
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
fun SupervisorSection(
    user: User,
    drawerState: AppDrawerState,
    onNavigateToWorkList: (String) -> Unit,
    onNavigateToCalendarManage: () -> Unit,
    onNavigateToTaskListOwner: () -> Unit,
    onNavigateToUserList: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var isUsersMenuExpanded by remember { mutableStateOf(false) }
    val arrowRotation by animateFloatAsState(if (isUsersMenuExpanded) 180f else 0f, label = "usersArrow")

    fun navigate(action: () -> Unit) {
        scope.launch {
            drawerState.close()
            action()
        }
    }

    if (user.roles.contains(UserRole.SUPERVISOR.value)) {
        AppDrawerSectionTitle(stringResource(R.string.supervisor))

        AppDrawerItem(
            label = stringResource(R.string.consultant_work),
            icon = AppIcons.Article,
            onClick = { navigate { onNavigateToWorkList("consultant") } }
        )

        AppDrawerItem(
            label = stringResource(R.string.supervisor_works),
            icon = AppIcons.Work,
            onClick = { navigate { onNavigateToWorkList("supervisor") } }
        )

        AppDrawerItem(
            label = stringResource(R.string.calendar_manage),
            icon = AppIcons.DateRange,
            onClick = { navigate(onNavigateToCalendarManage) }
        )

        AppDrawerItem(
            label = stringResource(R.string.task),
            icon = AppIcons.Task,
            onClick = { navigate(onNavigateToTaskListOwner) }
        )

        Box {
            AppDrawerItem(
                label = stringResource(R.string.users),
                icon = AppIcons.Groups,
                selected = isUsersMenuExpanded,
                onClick = { isUsersMenuExpanded = !isUsersMenuExpanded }
            )
            AppIcon(
                imageVector = AppIcons.KeyboardArrowDown,
                contentDescription = stringResource(if (isUsersMenuExpanded) R.string.collapse else R.string.expand),
                tint = AppTheme.colors.onSurfaceVariant,
                modifier = Modifier
                    .align(androidx.compose.ui.Alignment.CenterEnd)
                    .padding(end = 24.dp)
                    .rotate(arrowRotation)
            )
        }

        AnimatedVisibility(
            visible = isUsersMenuExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(Modifier.padding(start = 16.dp)) {
                listOf(
                    "author" to R.string.author,
                    "opponent" to R.string.opponent,
                    "consultant" to R.string.consultant,
                    "supervisor" to R.string.supervisor
                ).forEach { (type, labelRes) ->
                    AppDrawerItem(
                        label = stringResource(labelRes),
                        icon = AppIcons.Person,
                        onClick = { navigate { onNavigateToUserList(type) } }
                    )
                }
            }
        }
    }
}
