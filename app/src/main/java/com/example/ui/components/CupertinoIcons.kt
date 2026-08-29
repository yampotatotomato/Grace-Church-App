package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Cupertino / iOS SF Symbols styled iconography collection
 */
object CupertinoIcons {
    // Navigation / Tabs
    val House: ImageVector = Icons.Outlined.Home
    val HouseFill: ImageVector = Icons.Filled.Home

    val Book: ImageVector = Icons.Outlined.MenuBook
    val BookFill: ImageVector = Icons.Filled.MenuBook

    val Headphones: ImageVector = Icons.Outlined.Headphones
    val HeadphonesFill: ImageVector = Icons.Filled.Headphones

    val Heart: ImageVector = Icons.Outlined.FavoriteBorder
    val HeartFill: ImageVector = Icons.Filled.Favorite

    val SquareAndPencil: ImageVector = Icons.Outlined.EditNote
    val SquareAndPencilFill: ImageVector = Icons.Filled.EditNote

    val Person2: ImageVector = Icons.Outlined.Groups
    val Person2Fill: ImageVector = Icons.Filled.Groups

    val Person: ImageVector = Icons.Outlined.Person
    val PersonFill: ImageVector = Icons.Filled.Person

    // Journal & Notes
    val DocText: ImageVector = Icons.Outlined.Description
    val DocTextFill: ImageVector = Icons.Filled.Description
    val Pencil: ImageVector = Icons.Outlined.Create
    val Bookmark: ImageVector = Icons.Outlined.BookmarkBorder
    val BookmarkFill: ImageVector = Icons.Filled.Bookmark
    val Star: ImageVector = Icons.Outlined.StarBorder
    val StarFill: ImageVector = Icons.Filled.Star
    val FlameFill: ImageVector = Icons.Filled.LocalFireDepartment

    // Actions & Media
    val PlayFill: ImageVector = Icons.Filled.PlayArrow
    val PauseFill: ImageVector = Icons.Filled.Pause
    val PlayCircleFill: ImageVector = Icons.Filled.PlayCircle
    val PauseCircleFill: ImageVector = Icons.Filled.PauseCircle
    val Forward10: ImageVector = Icons.Filled.Forward10
    val Replay10: ImageVector = Icons.Filled.Replay10

    // UI Navigation & Controls
    val ChevronRight: ImageVector = Icons.AutoMirrored.Filled.ArrowForward
    val ChevronLeft: ImageVector = Icons.AutoMirrored.Filled.ArrowBack
    val ArrowRight: ImageVector = Icons.AutoMirrored.Filled.ArrowForward
    val ArrowLeft: ImageVector = Icons.AutoMirrored.Filled.ArrowBack
    val ChevronDown: ImageVector = Icons.Filled.KeyboardArrowDown
    val ChevronUp: ImageVector = Icons.Filled.KeyboardArrowUp
    val Search: ImageVector = Icons.Outlined.Search
    val Bell: ImageVector = Icons.Outlined.Notifications
    val BellFill: ImageVector = Icons.Filled.Notifications
    val Gear: ImageVector = Icons.Outlined.Settings
    val Plus: ImageVector = Icons.Filled.Add
    val Trash: ImageVector = Icons.Outlined.Delete
    val Share: ImageVector = Icons.Outlined.Share
    val TextSize: ImageVector = Icons.Outlined.FormatSize
    val Checkmark: ImageVector = Icons.Filled.Check
    val Xmark: ImageVector = Icons.Filled.Close
    val Sparkles: ImageVector = Icons.Filled.AutoAwesome
    val SunMax: ImageVector = Icons.Outlined.WbSunny
    val Sliders: ImageVector = Icons.Outlined.Tune
    val HandsSparkles: ImageVector = Icons.Filled.VolunteerActivism
    val Calendar: ImageVector = Icons.Outlined.CalendarToday
    val Location: ImageVector = Icons.Outlined.LocationOn
    val Phone: ImageVector = Icons.Outlined.Phone
    val Email: ImageVector = Icons.Outlined.Email
    val Quote: ImageVector = Icons.Filled.FormatQuote
}
