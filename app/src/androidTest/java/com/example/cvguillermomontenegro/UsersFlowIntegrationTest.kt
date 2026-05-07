package com.example.cvguillermomontenegro

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.example.cvguillermomontenegro.R
import com.example.cvguillermomontenegro.feature.users.R as UsersR
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UsersFlowIntegrationTest {

    @Test
    fun canOpenUsersScreen_andNavigateToEditForm() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        clearAppState(appContext)

        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val drawerUsersLabels = localizedStringCandidates(appContext, R.string.drawer_users_list)
        val usersTitles = localizedStringCandidates(appContext, UsersR.string.users_admin_title)
        val editDescriptions = localizedStringCandidates(appContext, UsersR.string.users_edit_content_description)
        val editScreenTitles = localizedStringCandidates(appContext, UsersR.string.user_form_edit_title)

        ActivityScenario.launch(MainActivity::class.java).use {
            completeOnboarding(device, appContext)
            assertTrue(waitForHomeReady(device, appContext) != null)
            openDrawerFromAvatar(device, appContext)

            assertTrue(device.waitForAnyText(drawerUsersLabels, 8_000) != null)
            device.waitForAnyText(drawerUsersLabels, 8_000)?.click()

            assertTrue(device.waitForAnyText(usersTitles, 8_000) != null)
            assertTrue(device.waitForAnyDescription(editDescriptions, 15_000) != null)
            device.waitForAnyDescription(editDescriptions, 8_000)?.click()

            assertTrue(device.waitForAnyText(editScreenTitles, 8_000) != null)
        }
    }

}
