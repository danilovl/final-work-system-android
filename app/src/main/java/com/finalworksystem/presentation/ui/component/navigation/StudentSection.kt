package com.finalworksystem.presentation.ui.component.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.finalworksystem.R
import com.finalworksystem.domain.user.model.User
import com.finalworksystem.domain.user.model.UserRole
import com.finalworksystem.ui.designsystem.AppDrawerItem
import com.finalworksystem.ui.designsystem.AppDrawerSectionTitle
import com.finalworksystem.ui.designsystem.AppDrawerState
import com.finalworksystem.ui.theme.AppIcons
import kotlinx.coroutines.launch

@Composable
fun StudentSection(
    user: User,
    drawerState: AppDrawerState,
    onNavigateToWorkList: (String) -> Unit,
    onNavigateToCalendarReservation: () -> Unit,
    onNavigateToTaskListSolver: () -> Unit
) {
    val scope = rememberCoroutineScope()

    if (user.roles.contains(UserRole.STUDENT.value)) {
        AppDrawerSectionTitle(stringResource(R.string.student))

        AppDrawerItem(
            label = stringResource(R.string.author_work),
            icon = AppIcons.Article,
            onClick = {
                scope.launch {
                    drawerState.close()
                    onNavigateToWorkList("author")
                }
            }
        )

        AppDrawerItem(
            label = stringResource(R.string.calendar_reservation),
            icon = AppIcons.DateRange,
            onClick = {
                scope.launch {
                    drawerState.close()
                    onNavigateToCalendarReservation()
                }
            }
        )

        AppDrawerItem(
            label = stringResource(R.string.assigned_task),
            icon = AppIcons.Task,
            onClick = {
                scope.launch {
                    drawerState.close()
                    onNavigateToTaskListSolver()
                }
            }
        )
    }
}
