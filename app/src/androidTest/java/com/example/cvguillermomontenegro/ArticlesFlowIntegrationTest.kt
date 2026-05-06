package com.example.cvguillermomontenegro

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.example.cvguillermomontenegro.R
import com.example.cvguillermomontenegro.feature.articles.R as ArticlesR
import com.example.cvguillermomontenegro.feature.onboarding.R as OnboardingR
import com.example.cvguillermomontenegro.feature.profile.R as ProfileR
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ArticlesFlowIntegrationTest {

    @Test
    fun onboarding_home_articles_and_detail_flow_work() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        clearAppState(appContext)

        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val nextText = appContext.getString(OnboardingR.string.onboarding_next)
        val enterText = appContext.getString(OnboardingR.string.onboarding_enter)
        val exploreArticlesText = appContext.getString(ProfileR.string.home_explore_articles)
        val libraryTitleText = appContext.getString(ArticlesR.string.articles_library_title)
        val articleScreenTitle = appContext.getString(R.string.nav_title_article)
        val articleTitle = "Tools for LLMs step by step"

        ActivityScenario.launch(MainActivity::class.java).use {
            assertTrue(device.wait(Until.hasObject(By.text(nextText)), 5_000))
            device.findObject(By.text(nextText)).click()
            assertTrue(device.wait(Until.hasObject(By.text(nextText)), 5_000))
            device.findObject(By.text(nextText)).click()
            assertTrue(device.wait(Until.hasObject(By.text(enterText)), 5_000))
            device.findObject(By.text(enterText)).click()

            assertTrue(device.wait(Until.hasObject(By.text(exploreArticlesText)), 8_000))
            device.findObject(By.text(exploreArticlesText)).click()

            assertTrue(device.wait(Until.hasObject(By.text(libraryTitleText)), 8_000))
            assertTrue(device.wait(Until.hasObject(By.text(articleTitle)), 8_000))
            device.findObject(By.text(articleTitle)).click()

            assertTrue(device.wait(Until.hasObject(By.text(articleScreenTitle)), 8_000))
        }
    }

    private fun clearAppState(context: Context) {
        context.getSharedPreferences("cv_app_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        context.deleteDatabase("cv_database")
    }
}
