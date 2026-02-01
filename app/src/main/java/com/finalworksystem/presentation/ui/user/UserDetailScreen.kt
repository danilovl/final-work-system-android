package com.finalworksystem.presentation.ui.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.domain.user.model.User
import com.finalworksystem.domain.user.model.UserRole
import com.finalworksystem.infrastructure.popup.PopupMessageService
import com.finalworksystem.infrastructure.session.SessionManager
import com.finalworksystem.infrastructure.user.UserService
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    userService: UserService = koinInject(),
    sessionManager: SessionManager = koinInject(),
    popupMessageService: PopupMessageService = koinInject(),
    onNavigateBack: () -> Unit
) {
    val user by userService.getUserFlow().collectAsState(initial = null)
    val scope = rememberCoroutineScope()
    var showLanguageMenu by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.user_profile)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showLanguageMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = stringResource(R.string.select_language)
                            )
                        }
                        DropdownMenu(
                            expanded = showLanguageMenu,
                            onDismissRequest = { showLanguageMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.language_en)) },
                                onClick = {
                                    scope.launch {
                                        sessionManager.saveLanguage("en")
                                        showLanguageMenu = false

                                        popupMessageService.showMessage(
                                            context.getString(R.string.language_changed),
                                            PopupMessageService.MessageLevel.SUCCESS
                                        )
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.language_ru)) },
                                onClick = {
                                    scope.launch {
                                        sessionManager.saveLanguage("ru")
                                        showLanguageMenu = false

                                        popupMessageService.showMessage(
                                            context.getString(R.string.language_changed),
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
                UserDetailContent(user = userInfo)
            } ?: run {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun UserDetailContent(user: User) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        UserInfoItem(stringResource(R.string.username), user.username)
        UserInfoItem(stringResource(R.string.full_name), user.fullName ?: "${user.firstname} ${user.lastname}")
        UserInfoItem(stringResource(R.string.email), user.email)

        if (!user.degreeBefore.isNullOrBlank()) {
            UserInfoItem(stringResource(R.string.degree_before), user.degreeBefore)
        }

        if (!user.degreeAfter.isNullOrBlank()) {
            UserInfoItem(stringResource(R.string.degree_after), user.degreeAfter)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.roles),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        if (user.roles.isNotEmpty()) {
            user.roles.forEach { role ->
                val displayName = try {
                    UserRole.getDisplayName(role)
                } catch (_: Exception) {
                    role
                }

                Text(
                    text = "• $displayName",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        } else {
            Text(
                text = "• ${stringResource(R.string.no_roles_assigned)}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun UserInfoItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
    }
}
