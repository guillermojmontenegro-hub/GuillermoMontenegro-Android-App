package com.example.cvguillermomontenegro

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.example.cvguillermomontenegro.feature.articles.R as ArticlesR
import com.example.cvguillermomontenegro.feature.profile.R as ProfileR
import com.example.cvguillermomontenegro.feature.users.R as UsersR
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppVideoShowcaseIntegrationTest {

    @Test
    fun walkthroughsEveryMainSectionForVideoCapture() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        clearAppState(appContext)

        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val drawerUsersLabels = localizedStringCandidates(appContext, R.string.drawer_users_list)
        val articleFabDescriptions = localizedStringCandidates(appContext, R.string.fab_view_articles_content_description)
        val articleLibraryTitles = localizedStringCandidates(appContext, ArticlesR.string.articles_library_title)
        val goBackDescriptions = localizedStringCandidates(appContext, R.string.nav_go_back)
        val userAdminTitles = localizedStringCandidates(appContext, UsersR.string.users_admin_title)
        val newUserTitles = localizedStringCandidates(appContext, UsersR.string.user_form_new_title)
        val editDescriptions = localizedStringCandidates(appContext, UsersR.string.users_edit_content_description)
        val editUserTitles = localizedStringCandidates(appContext, UsersR.string.user_form_edit_title)
        val createLabels = localizedStringCandidates(appContext, UsersR.string.user_form_create)
        val selectUserLabels = localizedStringCandidates(appContext, UsersR.string.users_select_user)
        val contactTitles = localizedStringCandidates(appContext, ProfileR.string.home_contact)
        val articleTitle = "Tools for LLMs step by step"
        val showcaseUserName = "Ada Lovelace"
        val showcaseUserEmail = "ada.video@example.com"
        val showcaseUserRole = "AI Engineer"
        val showcaseUserPhone = "+54 9 11 2345-6789"

        ActivityScenario.launch(MainActivity::class.java).use {
            completeOnboardingSlowly(device, appContext, pauseMs = 2_000L)
            assertTrue(waitForHomeReady(device, appContext) != null)
            pauseForVideo(2_000L)

            device.slowSwipeUp(times = 4, pauseMs = 1_400L)
            assertTrue(device.waitForAnyText(contactTitles, 8_000) != null)
            pauseForVideo(1_000L)

            assertTrue(device.waitForAnyDescription(articleFabDescriptions, 8_000) != null)
            pauseForVideo(1_200L)
            device.waitForAnyDescription(articleFabDescriptions, 8_000)?.click()

            assertTrue(device.waitForAnyText(articleLibraryTitles, 8_000) != null)
            pauseForVideo(2_000L)
            device.slowSwipeUp(times = 1, pauseMs = 1_200L)
            assertTrue(device.scrollToAnyText(listOf(articleTitle), maxSwipes = 6) != null)
            pauseForVideo(1_500L)
            device.scrollToAnyText(listOf(articleTitle), maxSwipes = 2)?.click()

            assertTrue(device.waitForAnyText(listOf(articleTitle), 8_000) != null)
            pauseForVideo(2_000L)
            device.slowSwipeUp(times = 3, pauseMs = 1_300L)
            assertTrue(device.waitForAnyDescription(goBackDescriptions, 8_000) != null)
            device.waitForAnyDescription(goBackDescriptions, 8_000)?.click()
            pauseForVideo(1_500L)

            openDrawerFromAvatar(device, appContext)

            assertTrue(device.waitForAnyText(drawerUsersLabels, 8_000) != null)
            pauseForVideo(2_000L)
            device.waitForAnyText(drawerUsersLabels, 8_000)?.click()

            assertTrue(device.waitForAnyText(userAdminTitles, 8_000) != null)
            pauseForVideo(2_000L)
            device.tapRelative(widthRatio = 0.9f, heightRatio = 0.9f)

            assertTrue(device.waitForAnyText(newUserTitles, 8_000) != null)
            pauseForVideo(1_500L)
            val formFields = device.waitForObjectsByClass(
                className = "android.widget.EditText",
                minimumCount = 4,
                timeoutMs = 8_000L
            )
            assertTrue(formFields.size >= 4)
            formFields[0].text = showcaseUserName
            pauseForVideo(700L)
            formFields[1].text = showcaseUserEmail
            pauseForVideo(700L)
            formFields[2].text = showcaseUserRole
            pauseForVideo(700L)
            formFields[3].text = showcaseUserPhone
            pauseForVideo(1_200L)

            assertTrue(device.waitForAnyText(createLabels, 8_000) != null)
            device.waitForAnyText(createLabels, 8_000)?.click()

            assertTrue(device.waitForAnyText(userAdminTitles, 8_000) != null)
            assertTrue(device.waitForAnyText(listOf(showcaseUserName), 8_000) != null)
            pauseForVideo(2_000L)
            assertTrue(device.waitForAnyText(selectUserLabels, 8_000) != null)
            device.waitForAnyText(selectUserLabels, 8_000)?.click()
            pauseForVideo(2_000L)

            device.slowSwipeUp(times = 1, pauseMs = 1_000L)
            assertTrue(device.waitForAnyDescription(editDescriptions, 15_000) != null)
            pauseForVideo(1_500L)
            device.waitForAnyDescription(editDescriptions, 8_000)?.click()

            assertTrue(device.waitForAnyText(editUserTitles, 8_000) != null)
            pauseForVideo(2_500L)
            device.slowSwipeUp(times = 1, pauseMs = 1_000L)
            device.pressBack()
            pauseForVideo(2_000L)
            device.pressBack()
            pauseForVideo(2_000L)
        }
    }
}
