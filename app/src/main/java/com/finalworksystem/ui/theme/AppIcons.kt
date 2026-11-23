package com.finalworksystem.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.finalworksystem.R

/**
 * Application icons backed by Material Symbols vector drawables (res/drawable/ic_*.xml).
 * Replaces the frozen `material-icons-extended` artifact: only the icons actually used ship with the app.
 */
object AppIcons {
    val Add: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_add)
    val ArrowBack: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_arrow_back)
    val ArrowDropDown: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_arrow_drop_down)
    val ArrowForward: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_arrow_forward)
    val Article: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_article)
    val BookmarkAdd: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_bookmark_add)
    val CalendarToday: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_calendar_today)
    val Check: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_check)
    val CheckCircle: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_check_circle)
    val Clear: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_close)
    val Close: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_close)
    val Comment: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_comment)
    val DateRange: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_date_range)
    val Delete: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_delete)
    val Edit: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_edit)
    val Email: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_mail)
    val ExitToApp: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_exit_to_app)
    val Groups: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_groups)
    val Home: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_home)
    val Info: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_info)
    val KeyboardArrowDown: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_keyboard_arrow_down)
    val KeyboardArrowUp: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_keyboard_arrow_up)
    val Language: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_language)
    val List: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_list)
    val Lock: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_lock)
    val LocationOn: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_location_on)
    val Notifications: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_notifications)
    val OpenInFull: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_open_in_full)
    val Person: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_person)
    val Refresh: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_refresh)
    val Search: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_search)
    val Send: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_send)
    val Task: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_task)
    val Today: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_today)
    val Visibility: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_visibility)
    val VisibilityOff: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_visibility_off)
    val WifiOff: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_wifi_off)
    val Work: ImageVector @Composable get() = ImageVector.vectorResource(R.drawable.ic_work)
}
