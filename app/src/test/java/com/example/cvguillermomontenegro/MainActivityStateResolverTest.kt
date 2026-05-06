package com.example.cvguillermomontenegro

import com.example.cvguillermomontenegro.domain.model.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MainActivityStateResolverTest {

    @Test
    fun findActiveUser_returnsOnlyUserMarkedActive() {
        val users = listOf(
            User(id = 1, name = "Inactive", isActive = false),
            User(id = 2, name = "Active", isActive = true),
            User(id = 3, name = "Also inactive", isActive = false)
        )

        assertEquals(2L, users.findActiveUser()?.id)
    }

    @Test
    fun findActiveUser_returnsNull_whenNoUserIsActive() {
        val users = listOf(
            User(id = 1, name = "First", isActive = false),
            User(id = 2, name = "Second", isActive = false)
        )

        assertNull(users.findActiveUser())
    }
}
