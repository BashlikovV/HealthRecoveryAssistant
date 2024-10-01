package by.bashlikovvv.ui.res.strings

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import by.bashlikovvv.ui.res.language.AppLanguage

@Immutable
data class AppStrings(
    val helloText: String,
)

val LocalAppStrings = staticCompositionLocalOf<AppStrings> {
    error("Core Strings is not provided")
}

fun fetchCoreStrings(language: AppLanguage) =
    when(language) {
        AppLanguage.EN -> englishStrings
        AppLanguage.RU -> russianStrings
    }