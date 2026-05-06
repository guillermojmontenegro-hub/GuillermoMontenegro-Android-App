package com.example.cvguillermomontenegro

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SplashScreenIntegrationTest {
    @Test
    fun launch_showsOnboardingAfterSplash() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        appContext.getSharedPreferences("cv_app_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()

        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val onboardingTitle = appContext.getString(R.string.onboarding_title)

        ActivityScenario.launch(MainActivity::class.java).use {
            assertTrue(
                device.wait(
                    Until.hasObject(By.text(onboardingTitle)),
                    5_000
                )
            )
        }
    }
}
