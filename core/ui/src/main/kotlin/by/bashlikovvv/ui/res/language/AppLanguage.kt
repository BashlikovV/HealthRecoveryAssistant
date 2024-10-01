package by.bashlikovvv.ui.res.language

import android.os.Parcelable
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.parcelize.Parcelize

enum class AppLanguage(val code: String) {
    EN("rn"),
    RU("ru")
}

@Parcelize
enum class LanguageUiType : Parcelable {
    EN, RU,
}

val LocalAppLanguage = staticCompositionLocalOf<AppLanguage> {
    error("Language is not provided")
}

fun fetchAppLanguage(languageUiType: LanguageUiType) = when(languageUiType) {
    LanguageUiType.RU -> AppLanguage.RU
    LanguageUiType.EN -> AppLanguage.EN
}