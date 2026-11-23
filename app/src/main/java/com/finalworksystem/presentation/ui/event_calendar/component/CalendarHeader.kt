package com.finalworksystem.presentation.ui.event_calendar.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.ui.designsystem.AppIconButton
import com.finalworksystem.ui.designsystem.AppIconButtonVariant
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun CalendarHeader(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppIconButton(
            icon = AppIcons.ArrowBack,
            contentDescription = stringResource(R.string.previous_month),
            onClick = onPreviousMonth,
            variant = AppIconButtonVariant.Tonal,
            size = 38.dp,
            iconSize = 18.dp
        )

        AppText(
            text = currentMonth
                .format(DateTimeFormatter.ofPattern("LLLL yyyy", LocalConfiguration.current.locales[0]))
                .replaceFirstChar { it.titlecase(LocalConfiguration.current.locales[0]) },
            style = AppTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = AppTheme.colors.onSurface
        )

        AppIconButton(
            icon = AppIcons.ArrowForward,
            contentDescription = stringResource(R.string.next_month),
            onClick = onNextMonth,
            variant = AppIconButtonVariant.Tonal,
            size = 38.dp,
            iconSize = 18.dp
        )
    }
}
