package com.example.cvguillermomontenegro

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.annotation.StringRes
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import java.util.Locale

internal fun clearAppState(context: Context) {
    context.getSharedPreferences("cv_app_prefs", Context.MODE_PRIVATE)
        .edit()
        .clear()
        .commit()
    context.deleteDatabase("cv_database")
}

internal fun localizedStringCandidates(
    context: Context,
    @StringRes id: Int
): List<String> = listOf("es", "en")
    .map { languageTag -> context.localizedFor(languageTag).getString(id) }
    .distinct()

internal fun completeOnboarding(device: UiDevice, context: Context) {
    val nextLabels = localizedStringCandidates(
        context = context,
        id = com.example.cvguillermomontenegro.feature.onboarding.R.string.onboarding_next
    )
    val enterLabels = localizedStringCandidates(
        context = context,
        id = com.example.cvguillermomontenegro.feature.onboarding.R.string.onboarding_enter
    )

    repeat(2) {
        requireNotNull(device.waitForAnyText(nextLabels, 8_000)).click()
    }
    requireNotNull(device.waitForAnyText(enterLabels, 8_000)).click()
    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
}

internal fun waitForHomeReady(device: UiDevice, context: Context): UiObject2? {
    val homeSignals = localizedStringCandidates(context, com.example.cvguillermomontenegro.R.string.nav_title_home) +
        localizedStringCandidates(context, com.example.cvguillermomontenegro.R.string.fab_view_articles)
    return device.waitForAnyText(homeSignals.distinct(), 15_000)
}

internal fun UiDevice.waitForAnyText(
    candidates: List<String>,
    timeoutMs: Long
): UiObject2? = waitForAny(timeoutMs) { value -> findObject(By.text(value)) }(candidates)

internal fun UiDevice.waitForAnyDescription(
    candidates: List<String>,
    timeoutMs: Long
): UiObject2? = waitForAny(timeoutMs) { value -> findObject(By.desc(value)) }(candidates)

internal fun UiDevice.scrollToAnyText(
    candidates: List<String>,
    timeoutMsPerAttempt: Long = 2_000,
    maxSwipes: Int = 6
): UiObject2? {
    repeat(maxSwipes + 1) { attempt ->
        waitForAnyText(candidates, timeoutMsPerAttempt)?.let { return it }
        if (attempt < maxSwipes) swipeUp()
    }
    return null
}

private fun UiDevice.waitForAny(
    timeoutMs: Long,
    finder: UiDevice.(String) -> UiObject2?
): (List<String>) -> UiObject2? = { candidates ->
    val deadline = System.currentTimeMillis() + timeoutMs
    var match: UiObject2? = null
    while (match == null && System.currentTimeMillis() < deadline) {
        for (candidate in candidates) {
            match = finder(candidate)
            if (match != null) break
        }
        if (match == null) waitForIdle()
    }
    match
}

private fun UiDevice.swipeUp() {
    val x = displayWidth / 2
    val startY = (displayHeight * 0.8).toInt()
    val endY = (displayHeight * 0.25).toInt()
    swipe(x, startY, x, endY, 28)
    waitForIdle()
}

private fun Context.localizedFor(languageTag: String): Context {
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
