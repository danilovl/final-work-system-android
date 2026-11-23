package com.finalworksystem.ui.designsystem

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale
import kotlin.math.abs

private const val MILLIS_PER_DAY = 24L * 60 * 60 * 1000

/** Current UI locale, observable by composition (re-reads on configuration change). */
@Composable
@androidx.compose.runtime.ReadOnlyComposable
fun currentLocale(): Locale = androidx.compose.ui.platform.LocalConfiguration.current.locales[0]

/** Converts UTC-midnight millis (picker convention) to [LocalDate]. */
fun utcMillisToLocalDate(millis: Long): LocalDate = LocalDate.ofEpochDay(Math.floorDiv(millis, MILLIS_PER_DAY))

/** Converts [LocalDate] to UTC-midnight millis (picker convention). */
fun LocalDate.toUtcMillis(): Long = toEpochDay() * MILLIS_PER_DAY

/**
 * Date picker dialog. Dates are exchanged as UTC-midnight millis (same convention as before),
 * use [utcMillisToLocalDate] / [toUtcMillis] for conversions.
 */
@Composable
fun AppDatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    initialSelectedDateMillis: Long? = null,
    isDateSelectable: (LocalDate) -> Boolean = { true },
    title: String? = null
) {
    var selected by remember { mutableStateOf(initialSelectedDateMillis?.let(::utcMillisToLocalDate)) }
    val locale = currentLocale()

    AppDialog(onDismissRequest = onDismissRequest, modifier = modifier) {
        Column(Modifier.padding(20.dp)) {
            if (title != null) {
                AppText(title, style = AppTheme.typography.labelLarge, color = AppTheme.colors.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
            }
            AppText(
                text = selected?.format(DateTimeFormatter.ofPattern("EEE, d MMM yyyy", locale)) ?: "—",
                style = AppTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(16.dp))
            AppCalendarMonth(
                selectedDate = selected,
                onDateClick = { selected = it },
                isDateSelectable = isDateSelectable
            )
            Spacer(Modifier.height(16.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                AppButton(text = stringResource(R.string.cancel), onClick = onDismissRequest, variant = AppButtonVariant.Ghost)
                AppButton(
                    text = stringResource(R.string.ok),
                    onClick = { onDateSelected(selected?.toUtcMillis()) },
                    enabled = selected != null
                )
            }
        }
    }
}

/** Month grid with navigation; reusable inside custom layouts. */
@Composable
fun AppCalendarMonth(
    selectedDate: LocalDate?,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    isDateSelectable: (LocalDate) -> Boolean = { true }
) {
    val c = AppTheme.colors
    val locale = currentLocale()
    val firstDayOfWeek = remember(locale) { WeekFields.of(locale).firstDayOfWeek }
    var month by remember { mutableStateOf(YearMonth.from(selectedDate ?: LocalDate.now())) }
    val today = remember { LocalDate.now() }

    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppText(
                text = month.format(DateTimeFormatter.ofPattern("LLLL yyyy", locale)).replaceFirstChar { it.titlecase(locale) },
                style = AppTheme.typography.titleMedium,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )
            AppIconButton(
                icon = AppIcons.ArrowBack,
                contentDescription = stringResource(R.string.previous_month),
                onClick = { month = month.minusMonths(1) },
                variant = AppIconButtonVariant.Tonal,
                size = 36.dp,
                iconSize = 18.dp
            )
            Spacer(Modifier.width(8.dp))
            AppIconButton(
                icon = AppIcons.ArrowForward,
                contentDescription = stringResource(R.string.next_month),
                onClick = { month = month.plusMonths(1) },
                variant = AppIconButtonVariant.Tonal,
                size = 36.dp,
                iconSize = 18.dp
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth()) {
            (0 until 7).forEach { i ->
                val day = firstDayOfWeek.plus(i.toLong())
                AppText(
                    text = day.getDisplayName(TextStyle.NARROW_STANDALONE, locale),
                    style = AppTheme.typography.labelMedium,
                    color = c.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Spacer(Modifier.height(6.dp))

        AnimatedContent(
            targetState = month,
            transitionSpec = {
                val forward = targetState > initialState
                (slideInHorizontally(tween(220)) { if (forward) it / 3 else -it / 3 } + fadeIn(tween(220))) togetherWith
                    (slideOutHorizontally(tween(180)) { if (forward) -it / 3 else it / 3 } + fadeOut(tween(160)))
            },
            label = "calendarMonth"
        ) { shownMonth ->
            val first = shownMonth.atDay(1)
            val leading = ((first.dayOfWeek.value - firstDayOfWeek.value) + 7) % 7
            val days = shownMonth.lengthOfMonth()
            val rows = (leading + days + 6) / 7
            Column {
                for (row in 0 until rows) {
                    Row(Modifier.fillMaxWidth()) {
                        for (col in 0 until 7) {
                            val dayNumber = row * 7 + col - leading + 1
                            Box(
                                Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (dayNumber in 1..days) {
                                    val date = shownMonth.atDay(dayNumber)
                                    val isSelected = date == selectedDate
                                    val isToday = date == today
                                    val enabled = isDateSelectable(date)
                                    Box(
                                        Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) c.primary else androidx.compose.ui.graphics.Color.Transparent, CircleShape)
                                            .then(if (isToday && !isSelected) Modifier.border(1.5.dp, c.primary, CircleShape) else Modifier)
                                            .clickable(enabled = enabled) { onDateClick(date) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AppText(
                                            text = dayNumber.toString(),
                                            style = AppTheme.typography.bodyMedium,
                                            color = when {
                                                isSelected -> c.onPrimary
                                                !enabled -> c.onSurface.copy(alpha = 0.3f)
                                                isToday -> c.primary
                                                date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY -> c.onSurfaceVariant
                                                else -> c.onSurface
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Time picker dialog with two snapping wheels (hours / minutes).
 */
@Composable
fun AppTimePickerDialog(
    onDismissRequest: () -> Unit,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    modifier: Modifier = Modifier,
    initialHour: Int = 9,
    initialMinute: Int = 0,
    title: String? = null
) {
    var hour by remember { mutableStateOf(initialHour.coerceIn(0, 23)) }
    var minute by remember { mutableStateOf(initialMinute.coerceIn(0, 59)) }

    AppDialog(onDismissRequest = onDismissRequest, modifier = modifier) {
        Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            if (title != null) {
                AppText(
                    title,
                    style = AppTheme.typography.labelLarge,
                    color = AppTheme.colors.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
            }
            Box(contentAlignment = Alignment.Center) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(WheelItemHeight)
                        .background(AppTheme.colors.primaryContainer, RoundedCornerShape(14.dp))
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppWheel(count = 24, selected = hour, onSelected = { hour = it }, modifier = Modifier.width(88.dp))
                    AppText(":", style = AppTheme.typography.headlineMedium, color = AppTheme.colors.onPrimaryContainer)
                    AppWheel(count = 60, selected = minute, onSelected = { minute = it }, modifier = Modifier.width(88.dp))
                }
            }
            Spacer(Modifier.height(20.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                AppButton(text = stringResource(R.string.cancel), onClick = onDismissRequest, variant = AppButtonVariant.Ghost)
                AppButton(text = stringResource(R.string.ok), onClick = { onTimeSelected(hour, minute) })
            }
        }
    }
}

private val WheelItemHeight = 48.dp
private const val WheelVisibleItems = 5

@Composable
private fun AppWheel(
    count: Int,
    selected: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val padding = WheelVisibleItems / 2
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selected)
    val flingBehavior = rememberSnapFlingBehavior(listState)
    val c = AppTheme.colors

    val centerIndex by remember {
        derivedStateOf {
            val layout = listState.layoutInfo
            val viewportCenter = (layout.viewportStartOffset + layout.viewportEndOffset) / 2
            layout.visibleItemsInfo.minByOrNull { abs(it.offset + it.size / 2 - viewportCenter) }?.index?.minus(padding)
                ?: listState.firstVisibleItemIndex
        }
    }

    androidx.compose.runtime.LaunchedEffect(centerIndex) {
        val value = centerIndex.coerceIn(0, count - 1)
        if (value != selected) onSelected(value)
    }

    LazyColumn(
        state = listState,
        flingBehavior = flingBehavior,
        modifier = modifier.height(WheelItemHeight * WheelVisibleItems),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(count + padding * 2) { index ->
            val value = index - padding
            Box(Modifier.height(WheelItemHeight).fillMaxWidth(), contentAlignment = Alignment.Center) {
                if (value in 0 until count) {
                    val distance = abs(value - centerIndex)
                    AppText(
                        text = "%02d".format(value),
                        style = if (distance == 0) AppTheme.typography.headlineMedium else AppTheme.typography.titleLarge,
                        color = if (distance == 0) c.onPrimaryContainer else c.onSurfaceVariant,
                        modifier = Modifier.graphicsLayer { alpha = (1f - distance * 0.3f).coerceAtLeast(0.15f) }
                    )
                }
            }
        }
    }
}
