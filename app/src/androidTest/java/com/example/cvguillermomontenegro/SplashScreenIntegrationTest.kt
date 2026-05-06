package com.example.cvguillermomontenegro

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SplashScreenIntegrationTest {
    @Test
    fun launch_showsOnboardingAfterSplash() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        clearAppState(appContext)

        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val onboardingTitles = localizedStringCandidates(appContext, R.string.onboarding_title)

        ActivityScenario.launch(MainActivity::class.java).use {
            assertTrue(device.waitForAnyText(onboardingTitles, 8_000) != null)
        }
    }
}
