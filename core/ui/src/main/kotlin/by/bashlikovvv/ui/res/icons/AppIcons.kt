package by.bashlikovvv.ui.res.icons

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
data class AppIcons(
    val icAdd: ImageVector,
    val icSettings: ImageVector,
)

val LocalAppIcons = staticCompositionLocalOf<AppIcons> {
    error("Icons is not provided")
}