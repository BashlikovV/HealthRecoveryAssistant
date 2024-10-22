package by.bashlikovvv.ui.res

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import by.bashlikovvv.ui.res.icons.AppIcons
import by.bashlikovvv.ui.res.icons.LocalAppIcons
import by.bashlikovvv.ui.res.language.AppLanguage
import by.bashlikovvv.ui.res.language.LocalAppLanguage
import by.bashlikovvv.ui.res.strings.AppStrings
import by.bashlikovvv.ui.res.strings.LocalAppStrings

@Immutable
object AppRes {
    val language: AppLanguage
        @[Composable ReadOnlyComposable] get() = LocalAppLanguage.current

    val strings: AppStrings
        @[Composable ReadOnlyComposable] get() = LocalAppStrings.current

    val icons: AppIcons
        @[Composable ReadOnlyComposable] get() = LocalAppIcons.current
}