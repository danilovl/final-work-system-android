package com.finalworksystem.presentation.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.finalworksystem.ui.designsystem.AppIcon
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.LocalAppContentColor

@Composable
fun DoubleCheckIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalAppContentColor.current,
    size: Dp = 22.dp
) {
    Row(modifier = modifier.offset(x = 4.dp)) {
        AppIcon(
            imageVector = AppIcons.Check,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(size)
        )
        AppIcon(
            imageVector = AppIcons.Check,
            contentDescription = null,
            tint = tint,
            modifier = Modifier
                .size(size)
                .offset(x = (-8).dp)
        )
    }
}
