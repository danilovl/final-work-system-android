package com.finalworksystem.presentation.ui.user

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.finalworksystem.R
import com.finalworksystem.domain.user.model.User
import com.finalworksystem.domain.user.model.UserRole
import com.finalworksystem.infrastructure.locale.AppLanguage
import com.finalworksystem.infrastructure.popup.PopupMessageService
import com.finalworksystem.infrastructure.user.UserService
import com.finalworksystem.presentation.ui.component.BackNavigationButton
import com.finalworksystem.presentation.ui.user.component.ChangePasswordDialog
import com.finalworksystem.presentation.ui.user.component.UserAvatar
import com.finalworksystem.presentation.view_model.user.ChangePasswordViewModel
import com.finalworksystem.ui.designsystem.AppButton
import com.finalworksystem.ui.designsystem.AppButtonVariant
import com.finalworksystem.ui.designsystem.AppCard
import com.finalworksystem.ui.designsystem.AppDivider
import com.finalworksystem.ui.designsystem.AppDropdownMenu
import com.finalworksystem.ui.designsystem.AppDropdownMenuItem
import com.finalworksystem.ui.designsystem.AppIconButton
import com.finalworksystem.ui.designsystem.AppIconButtonVariant
import com.finalworksystem.ui.designsystem.AppInfoRow
import com.finalworksystem.ui.designsystem.AppLoadingState
import com.finalworksystem.ui.designsystem.AppScaffold
import com.finalworksystem.ui.designsystem.AppSectionHeader
import com.finalworksystem.ui.designsystem.AppTag
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.designsystem.AppTopBar
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun UserDetailScreen(
    userService: UserService = koinInject(),
    popupMessageService: PopupMessageService = koinInject(),
    changePasswordViewModel: ChangePasswordViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    // Safe flow: emits null after logout instead of throwing and crashing the screen.
    val user by userService.getUserFlowSafe().collectAsStateWithLifecycle(initialValue = null)
    val scope = rememberCoroutineScope()
    var showLanguageMenu by remember { mutableStateOf(false) }
    var showChangePassword by remember { mutableStateOf(false) }
    val resources = androidx.compose.ui.platform.LocalResources.current

    AppScaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.user_profile),
                navigationIcon = { BackNavigationButton(onClick = onNavigateBack) },
                actions = {
                    Box {
                        AppIconButton(
                            icon = AppIcons.Language,
                            contentDescription = stringResource(R.string.select_language),
                            onClick = { showLanguageMenu = true },
                            variant = AppIconButtonVariant.Tonal
                        )
                        AppDropdownMenu(
                            expanded = showLanguageMenu,
                            onDismissRequest = { showLanguageMenu = false }
                        ) {
                            AppDropdownMenuItem(
                                text = stringResource(R.string.language_en),
                                onClick = {
                                    scope.launch {
                                        AppLanguage.set("en")
                                        showLanguageMenu = false

                                        popupMessageService.showMessage(
                                            resources.getString(R.string.language_changed),
                                            PopupMessageService.MessageLevel.SUCCESS
                                        )
                                    }
                                }
                            )
                            AppDropdownMenuItem(
                                text = stringResource(R.string.language_ru),
                                onClick = {
                                    scope.launch {
                                        AppLanguage.set("ru")
                                        showLanguageMenu = false

                                        popupMessageService.showMessage(
                                            resources.getString(R.string.language_changed),
                                            PopupMessageService.MessageLevel.SUCCESS
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            user?.let { userInfo ->
                UserDetailContent(
                    user = userInfo,
                    onChangePasswordClick = { showChangePassword = true }
                )
            } ?: run {
                AppLoadingState()
            }
        }
    }

    val changePasswordState by changePasswordViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(changePasswordState) {
        if (changePasswordState == ChangePasswordViewModel.State.Success) {
            showChangePassword = false
            changePasswordViewModel.reset()
        }
    }

    if (showChangePassword) {
        ChangePasswordDialog(
            state = changePasswordState,
            onSubmit = changePasswordViewModel::changePassword,
            onDismiss = {
                showChangePassword = false
                changePasswordViewModel.reset()
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserDetailContent(user: User, onChangePasswordClick: () -> Unit) {
    val colors = AppTheme.colors
    val fullName = user.fullName ?: "${user.firstname} ${user.lastname}"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AppCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UserAvatar(
                    firstname = user.firstname,
                    lastname = user.lastname,
                    userId = user.id,
                    modifier = Modifier
                        .size(104.dp)
                        .border(3.dp, colors.primary.copy(alpha = 0.25f), CircleShape)
                )

                Spacer(modifier = Modifier.height(16.dp))

                AppText(
                    text = fullName,
                    style = AppTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                AppText(
                    text = "@${user.username}",
                    style = AppTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                if (user.email.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    AppTag(text = user.email, color = colors.primary, icon = AppIcons.Email)
                }
            }
        }

        val infoItems = buildList {
            add(Triple(stringResource(R.string.username), user.username, AppIcons.Person))
            add(Triple(stringResource(R.string.full_name), fullName, AppIcons.Person))
            add(Triple(stringResource(R.string.email), user.email, AppIcons.Email))
            if (!user.degreeBefore.isNullOrBlank()) {
                add(Triple(stringResource(R.string.degree_before), user.degreeBefore, AppIcons.Article))
            }
            if (!user.degreeAfter.isNullOrBlank()) {
                add(Triple(stringResource(R.string.degree_after), user.degreeAfter, AppIcons.Article))
            }
        }

        AppCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(16.dp)) {
            infoItems.forEachIndexed { index, (label, value, icon) ->
                UserInfoItem(label, value, icon, showDivider = index < infoItems.lastIndex)
            }
        }

        AppCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(16.dp)) {
            AppSectionHeader(title = stringResource(R.string.roles), icon = AppIcons.Groups)

            Spacer(modifier = Modifier.height(12.dp))

            if (user.roles.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    user.roles.forEach { role ->
                        AppTag(text = roleDisplayName(role), color = colors.secondary, icon = AppIcons.CheckCircle)
                    }
                }
            } else {
                AppText(
                    text = stringResource(R.string.no_roles_assigned),
                    style = AppTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
            }
        }

        AppCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(16.dp)) {
            AppSectionHeader(title = stringResource(R.string.security), icon = AppIcons.Lock)

            Spacer(modifier = Modifier.height(8.dp))

            AppText(
                text = stringResource(R.string.change_password_description),
                style = AppTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            AppButton(
                text = stringResource(R.string.change_password),
                onClick = onChangePasswordClick,
                variant = AppButtonVariant.Secondary,
                leadingIcon = AppIcons.Lock,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun roleDisplayName(role: String): String = try {
    UserRole.getDisplayName(role)
} catch (_: Exception) {
    role
}

@Composable
fun UserInfoItem(
    label: String,
    value: String,
    icon: ImageVector? = null,
    showDivider: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        AppInfoRow(label = label, value = value, icon = icon)
        if (showDivider) {
            AppDivider(modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}
