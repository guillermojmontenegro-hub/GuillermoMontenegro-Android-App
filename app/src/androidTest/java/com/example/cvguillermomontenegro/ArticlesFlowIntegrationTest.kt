package com.example.cvguillermomontenegro

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.example.cvguillermomontenegro.R
import com.example.cvguillermomontenegro.feature.articles.R as ArticlesR
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
        val articleFabDescriptions = localizedStringCandidates(appContext, R.string.fab_view_articles_content_description)
        val libraryTitles = localizedStringCandidates(appContext, ArticlesR.string.articles_library_title)
        val goBackDescriptions = localizedStringCandidates(appContext, R.string.nav_go_back)
        val articleTitle = "Tools for LLMs step by step"

        ActivityScenario.launch(MainActivity::class.java).use {
            completeOnboarding(device, appContext)
            assertTrue(waitForHomeReady(device, appContext) != null)

            assertTrue(device.waitForAnyDescription(articleFabDescriptions, 8_000) != null)
            device.waitForAnyDescription(articleFabDescriptions, 8_000)?.click()

            assertTrue(device.waitForAnyText(libraryTitles, 8_000) != null)
            assertTrue(device.scrollToAnyText(listOf(articleTitle)) != null)
            device.scrollToAnyText(listOf(articleTitle))?.click()

            assertTrue(device.waitForAnyDescription(goBackDescriptions, 8_000) != null)
        }
    }
}
