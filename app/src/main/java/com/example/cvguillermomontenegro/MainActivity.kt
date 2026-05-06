package com.example.cvguillermomontenegro

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cvguillermomontenegro.ui.i18n.ProvideAppLanguage
import com.example.cvguillermomontenegro.ui.i18n.toSupportedLanguageTag
import com.example.cvguillermomontenegro.ui.navigation.AppNavHost
import com.example.cvguillermomontenegro.ui.screens.collectAsStateWithLifecycleCompat
import com.example.cvguillermomontenegro.ui.theme.CVTheme
import com.example.cvguillermomontenegro.ui.users.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashStartedAt = SystemClock.uptimeMillis()
        installSplashScreen().setKeepOnScreenCondition {
            SystemClock.uptimeMillis() - splashStartedAt < 900
        }
        super.onCreate(savedInstanceState)
        setContent {
            val userViewModel: UserViewModel = hiltViewModel()
            val users by userViewModel.users.collectAsStateWithLifecycleCompat()
            val activeUser = users.findActiveUser()
            val languageTag = activeUser?.languageTag ?: LocalConfiguration.current.toSupportedLanguageTag()
            val darkTheme = activeUser?.darkModeEnabled ?: true

            ProvideAppLanguage(languageTag = languageTag) {
                CVTheme(darkTheme = darkTheme) {
                    AppNavHost(
                        userViewModel = userViewModel,
                        activeUser = activeUser,
                        languageTag = languageTag,
                        darkModeEnabled = darkTheme
                    )
                }
            }
        }
    }
}

internal fun List<com.example.cvguillermomontenegro.domain.model.User>.findActiveUser() =
    firstOrNull { it.isActive }
