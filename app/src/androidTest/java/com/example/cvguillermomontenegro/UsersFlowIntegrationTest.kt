package com.example.cvguillermomontenegro

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.example.cvguillermomontenegro.R
import com.example.cvguillermomontenegro.feature.onboarding.R as OnboardingR
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
        val nextText = appContext.getString(OnboardingR.string.onboarding_next)
        val enterText = appContext.getString(OnboardingR.string.onboarding_enter)
        val drawerUsersText = appContext.getString(R.string.drawer_users_list)
        val usersTitle = appContext.getString(UsersR.string.users_admin_title)
        val editDescription = appContext.getString(UsersR.string.users_edit_content_description)
        val editScreenTitle = appContext.getString(UsersR.string.user_form_edit_title)

        ActivityScenario.launch(MainActivity::class.java).use {
            assertTrue(device.wait(Until.hasObject(By.text(nextText)), 5_000))
            device.findObject(By.text(nextText)).click()
            assertTrue(device.wait(Until.hasObject(By.text(nextText)), 5_000))
            device.findObject(By.text(nextText)).click()
            assertTrue(device.wait(Until.hasObject(By.text(enterText)), 5_000))
            device.findObject(By.text(enterText)).click()

            openDrawer(device)

            assertTrue(device.wait(Until.hasObject(By.text(drawerUsersText)), 5_000))
            device.findObject(By.text(drawerUsersText)).click()

            assertTrue(device.wait(Until.hasObject(By.text(usersTitle)), 8_000))
            assertTrue(device.wait(Until.hasObject(By.desc(editDescription)), 8_000))
            device.findObject(By.desc(editDescription)).click()

            assertTrue(device.wait(Until.hasObject(By.text(editScreenTitle)), 8_000))
        }
    }

    private fun openDrawer(device: UiDevice) {
        val width = device.displayWidth
        val height = device.displayHeight
        device.swipe(0, height / 2, width / 2, height / 2, 24)
    }

    private fun clearAppState(context: Context) {
        context.getSharedPreferences("cv_app_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        context.deleteDatabase("cv_database")
    }
}
