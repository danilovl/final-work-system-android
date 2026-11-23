package com.finalworksystem.presentation.ui.event_calendar.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.designsystem.pressScale
import com.finalworksystem.ui.theme.AppIndication
import com.finalworksystem.ui.theme.AppTheme
import java.time.LocalDate
import com.finalworksystem.domain.event_calendar.model.CalendarEvent as DomainCalendarEvent

@Composable
fun CalendarDay(
    date: LocalDate,
    isSelected: Boolean,
    eventsForDate: List<DomainCalendarEvent>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val isToday = date == LocalDate.now()
    val shape = RoundedCornerShape(12.dp)
    val interactionSource = remember { MutableInteractionSource() }

    val background by animateColorAsState(
        targetValue = when {
            isSelected -> colors.primary
            isToday -> colors.primary.copy(alpha = if (colors.isDark) 0.16f else 0.08f)
            else -> Color.Transparent
        },
        label = "calendarDayBackground"
    )
    val textColor = when {
        isSelected -> colors.onPrimary
        isToday -> colors.primary
        else -> colors.onSurface
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(3.dp)
            .pressScale(interactionSource, 0.9f)
            .clip(shape)
            .background(color = background, shape = shape)
            .then(
                if (isToday && !isSelected) {
                    Modifier.border(1.5.dp, colors.primary, shape)
                } else {
                    Modifier
                }
            )
            .clickable(interactionSource = interactionSource, indication = AppIndication) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppText(
                text = date.dayOfMonth.toString(),
                color = textColor,
                style = AppTheme.typography.bodyMedium,
                fontWeight = if (isSelected || isToday) FontWeight.SemiBold else FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                modifier = Modifier.heightIn(min = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                eventsForDate.take(3).forEach { event ->
                    val eventColor = parseColor(event.color)
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .then(
                                if (isSelected) {
                                    Modifier.border(0.75.dp, colors.onPrimary, CircleShape)
                                } else {
                                    Modifier
                                }
                            )
                            .background(color = eventColor, shape = CircleShape)
                    )
                }
                if (eventsForDate.size > 3) {
                    AppText(
                        text = "+",
                        style = AppTheme.typography.labelSmall,
                        color = if (isSelected) colors.onPrimary else colors.onSurfaceVariant
                    )
                }
            }
        }
    }
}
