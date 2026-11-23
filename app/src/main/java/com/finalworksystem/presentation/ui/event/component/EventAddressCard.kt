package com.finalworksystem.presentation.ui.event.component

import com.finalworksystem.presentation.ui.component.HtmlText
import com.finalworksystem.ui.theme.AppIcons
import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.domain.event.model.EventAddress
import com.finalworksystem.presentation.ui.component.BaseCard
import com.finalworksystem.ui.designsystem.AppIconBadge
import com.finalworksystem.ui.designsystem.AppIconButton
import com.finalworksystem.ui.designsystem.AppIconButtonVariant
import com.finalworksystem.ui.designsystem.AppSectionHeader
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@SuppressLint("UnrememberedMutableState")
@Composable
fun EventAddressCard(address: EventAddress) {
    var showFullScreenMap by remember { mutableStateOf(false) }

    BaseCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppSectionHeader(
                title = stringResource(R.string.location),
                icon = AppIcons.LocationOn
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                AppIconBadge(
                    icon = AppIcons.LocationOn,
                    size = 36,
                    tint = AppTheme.colors.secondary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    address.name?.let { name ->
                        AppText(
                            text = name,
                            style = AppTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    address.street?.let { street ->
                        AppText(
                            text = street,
                            style = AppTheme.typography.bodyMedium,
                            color = AppTheme.colors.onSurfaceVariant
                        )
                    }
                    address.description?.let { description ->
                        HtmlText(
                            html = description,
                            textColor = AppTheme.colors.onSurfaceVariant,
                            textSize = 12f
                        )
                    }
                }
            }

            if (address.latitude != null && address.longitude != null) {
                val mapShape = AppTheme.shapes.medium
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .clip(mapShape)
                        .border(1.dp, AppTheme.colors.outlineVariant, mapShape)
                ) {
                    val location = LatLng(address.latitude, address.longitude)
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(location, 15f)
                    }

                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(
                            mapType = MapType.NORMAL,
                            isMyLocationEnabled = false,
                            isTrafficEnabled = false
                        ),
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = true,
                            compassEnabled = true,
                            mapToolbarEnabled = false,
                            myLocationButtonEnabled = false,
                            rotationGesturesEnabled = true,
                            scrollGesturesEnabled = true,
                            scrollGesturesEnabledDuringRotateOrZoom = false,
                            tiltGesturesEnabled = true,
                            zoomGesturesEnabled = true
                        )
                    ) {
                        val markerState = remember { MarkerState(position = location) }
                        Marker(
                            state = markerState,
                            title = address.name ?: stringResource(R.string.location),
                            snippet = address.street
                        )
                    }

                    AppIconButton(
                        icon = AppIcons.OpenInFull,
                        contentDescription = stringResource(R.string.expand_to_fullscreen),
                        onClick = { showFullScreenMap = true },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        variant = AppIconButtonVariant.Filled,
                        size = 38.dp,
                        iconSize = 20.dp,
                        containerColor = AppTheme.colors.surfaceElevated,
                        tint = AppTheme.colors.onSurface
                    )
                }

                if (showFullScreenMap) {
                    FullScreenMapDialog(
                        address = address,
                        onDismiss = { showFullScreenMap = false }
                    )
                }
            }
        }
    }
}
