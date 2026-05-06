package com.example.cvguillermomontenegro.ui.i18n

import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

val LocalAppLanguageTag = compositionLocalOf { "es" }

fun Configuration.toSupportedLanguageTag(): String {
    val language = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        locales.get(0)?.language
    } else {
        @Suppress("DEPRECATION")
        locale?.language
    }
    return if (language == "en") "en" else "es"
}

@Composable
fun ProvideAppLanguage(
    languageTag: String,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalAppLanguageTag provides languageTag) {
        content()
    }
}

@Composable
@ReadOnlyComposable
fun localizedStringResource(@StringRes id: Int): String {
    val context = LocalContext.current
    val languageTag = LocalAppLanguageTag.current
    val resources = context.createLocalizedContext(languageTag).resources
    return resources.getString(id)
}

@Composable
@ReadOnlyComposable
fun localizedStringResource(@StringRes id: Int, vararg formatArgs: Any): String {
    val context = LocalContext.current
    val languageTag = LocalAppLanguageTag.current
    val localizedContext = context.createLocalizedContext(languageTag)
    return localizedContext.resources.getString(id, *formatArgs)
}

private fun android.content.Context.createLocalizedContext(languageTag: String): android.content.Context {
    val locale = Locale.forLanguageTag(languageTag)
    val configuration = Configuration(resources.configuration)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        configuration.setLocales(LocaleList(locale))
    } else {
        @Suppress("DEPRECATION")
        configuration.locale = locale
    }
    return createConfigurationContext(configuration)
}
