package by.bashlikovvv.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import by.bashlikovvv.ui.res.icons.LocalAppIcons
import by.bashlikovvv.ui.res.icons.baseAppIcons
import by.bashlikovvv.ui.res.language.LanguageUiType
import by.bashlikovvv.ui.res.language.LocalAppLanguage
import by.bashlikovvv.ui.res.language.fetchAppLanguage
import by.bashlikovvv.ui.res.strings.LocalAppStrings
import by.bashlikovvv.ui.res.strings.fetchCoreStrings

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun HealthRecoveryAssistantTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    languageUiType: LanguageUiType = LanguageUiType.EN,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val appLanguage = fetchAppLanguage(languageUiType)
    val appStrings = fetchCoreStrings(appLanguage)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography
    ) {
        CompositionLocalProvider(
            LocalAppLanguage provides appLanguage,
            LocalAppStrings provides appStrings,
            LocalAppIcons provides baseAppIcons,
            content = content
        )
    }
}