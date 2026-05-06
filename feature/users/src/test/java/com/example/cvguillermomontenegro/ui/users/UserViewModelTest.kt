package com.example.cvguillermomontenegro.ui.users

import androidx.annotation.VisibleForTesting
import com.example.cvguillermomontenegro.data.local.UserDao
import com.example.cvguillermomontenegro.data.local.UserEntity
import com.example.cvguillermomontenegro.data.repository.UserRepository
import com.example.cvguillermomontenegro.domain.model.User
import com.example.cvguillermomontenegro.domain.usecase.DeleteUserByIdUseCase
import com.example.cvguillermomontenegro.domain.usecase.GetUserByIdUseCase
import com.example.cvguillermomontenegro.domain.usecase.GetUsersUseCase
import com.example.cvguillermomontenegro.domain.usecase.SaveUserUseCase
import com.example.cvguillermomontenegro.feature.users.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_createsDefaultUser_whenRepositoryIsEmpty() = runViewModelTest {
        val dao = FakeUserDao()

        createViewModel(dao)
        advanceUntilIdle()

        val users = dao.snapshot()
        assertEquals(1, users.size)
        assertEquals("Guillermo Montenegro", users.single().name)
        assertEquals("guillermo@example.com", users.single().email)
        assertEquals("Desarrollador", users.single().role)
        assertTrue(users.single().darkModeEnabled)
        assertTrue(users.single().isActive)
    }

    @Test
    fun saveUser_withInvalidState_setsErrors_andSkipsPersistence() = runViewModelTest {
        val dao = FakeUserDao()
        val viewModel = createViewModel(dao)
        advanceUntilIdle()

        viewModel.updateName("   ")
        viewModel.updateEmail("invalid-email")
        viewModel.saveUser {}
        advanceUntilIdle()

        val state = viewModel.formState.value
        assertEquals(R.string.user_error_name_required, state.nameError)
        assertEquals(R.string.user_error_invalid_email, state.emailError)
        assertEquals(1, dao.snapshot().size)
    }

    @Test
    fun saveUser_persistsTrimmedValues_andFormatsPhone() = runViewModelTest {
        val dao = FakeUserDao()
        val viewModel = createViewModel(dao)
        advanceUntilIdle()

        var onSuccessCalled = false
        viewModel.updateName("  Ada Lovelace  ")
        viewModel.updateEmail("  ada@example.com ")
        viewModel.updateRole(" Engineer ")
        viewModel.updatePhone("+54 9 11 2345-6789")
        viewModel.saveUser { onSuccessCalled = true }
        advanceUntilIdle()

        val savedUser = dao.snapshot().first { it.email == "ada@example.com" }
        assertEquals("Ada Lovelace", savedUser.name)
        assertEquals("Engineer", savedUser.role)
        assertEquals("+54-9-11-2345-6789", savedUser.phone)
        assertTrue(onSuccessCalled)
        assertEquals(UserFormState(), viewModel.formState.value)
    }

    @Test
    fun deleteUser_whenActive_promotesAnotherUser() = runViewModelTest {
        val dao = FakeUserDao(
            initialUsers = listOf(
                userEntity(id = 1, name = "Active", email = "active@example.com", isActive = true),
                userEntity(id = 2, name = "Backup", email = "backup@example.com", updatedAt = 2_000L)
            )
        )
        val viewModel = createViewModel(dao)
        advanceUntilIdle()

        viewModel.deleteUser(1)
        advanceUntilIdle()

        val users = dao.snapshot()
        assertEquals(1, users.size)
        assertEquals(2L, users.single().id)
        assertTrue(users.single().isActive)
    }

    @Test
    fun loadUser_withExistingId_populatesFormState() = runViewModelTest {
        val dao = FakeUserDao(
            initialUsers = listOf(
                userEntity(
                    id = 8,
                    name = "Ada",
                    email = "ada@example.com",
                    role = "Engineer",
                    phone = "+54-9-11-2345-6789"
                )
            )
        )
        val viewModel = createViewModel(dao)
        advanceUntilIdle()

        viewModel.loadUser(8)
        advanceUntilIdle()

        val state = viewModel.formState.value
        assertEquals(8L, state.id)
        assertEquals("Ada", state.name)
        assertEquals("ada@example.com", state.email)
        assertEquals("Engineer", state.role)
        assertEquals("5491123456789", state.phone)
    }

    @Test
    fun setDarkMode_updatesPersistedUserSetting() = runViewModelTest {
        val dao = FakeUserDao(
            initialUsers = listOf(
                userEntity(id = 1, name = "Active", email = "active@example.com", darkModeEnabled = false)
            )
        )
        val viewModel = createViewModel(dao)
        advanceUntilIdle()

        val user = dao.snapshot().single()
        viewModel.setDarkMode(user, true)
        advanceUntilIdle()

        assertTrue(dao.snapshot().single().darkModeEnabled)
    }

    @Test
    fun selectActiveUser_marksOnlySelectedUserAsActive() = runViewModelTest {
        val dao = FakeUserDao(
            initialUsers = listOf(
                userEntity(id = 1, name = "Current", email = "current@example.com", isActive = true),
                userEntity(id = 2, name = "Target", email = "target@example.com", isActive = false)
            )
        )
        val viewModel = createViewModel(dao)
        advanceUntilIdle()

        val target = dao.snapshot().first { it.id == 2L }
        viewModel.selectActiveUser(target)
        advanceUntilIdle()

        val users = dao.snapshot()
        assertFalse(users.first { it.id == 1L }.isActive)
        assertTrue(users.first { it.id == 2L }.isActive)
    }

    @Test
    fun loadUser_whenUserDoesNotExist_resetsPreviousFormState() = runViewModelTest {
        val dao = FakeUserDao(
            initialUsers = listOf(
                userEntity(id = 1, name = "Ada", email = "ada@example.com", role = "Engineer")
            )
        )
        val viewModel = createViewModel(dao)
        advanceUntilIdle()

        viewModel.loadUser(1)
        advanceUntilIdle()
        assertEquals("Ada", viewModel.formState.value.name)

        viewModel.loadUser(999)
        advanceUntilIdle()

        assertEquals(UserFormState(), viewModel.formState.value)
    }

    private fun TestScope.createViewModel(dao: FakeUserDao): UserViewModel {
        val repository = UserRepository(dao)
        return UserViewModel(
            getUsersUseCase = GetUsersUseCase(repository),
            getUserByIdUseCase = GetUserByIdUseCase(repository),
            saveUserUseCase = SaveUserUseCase(repository),
            deleteUserByIdUseCase = DeleteUserByIdUseCase(repository)
        ).also { viewModel ->
            backgroundScope.launch {
                viewModel.users.collect {}
            }
        }
    }

    private fun runViewModelTest(
        testBody: suspend TestScope.() -> Unit
    ) = runTest(dispatcher, testBody = testBody)

    private fun userEntity(
        id: Long = 0,
        name: String,
        email: String,
        role: String = "Developer",
        phone: String = "",
        darkModeEnabled: Boolean = false,
        languageTag: String = "es",
        isActive: Boolean = false,
        updatedAt: Long = 1_000L
    ) = UserEntity(
        id = id,
        name = name,
        email = email,
        role = role,
        phone = phone,
        darkModeEnabled = darkModeEnabled,
        languageTag = languageTag,
        isActive = isActive,
        updatedAt = updatedAt
    )

    @VisibleForTesting
    internal class FakeUserDao(
        initialUsers: List<UserEntity> = emptyList()
    ) : UserDao {
        private val users = initialUsers.toMutableList()
        private val usersFlow = MutableStateFlow(sortUsers(users))
        private var nextId = (users.maxOfOrNull(UserEntity::id) ?: 0L) + 1L

        override fun getAllUsers(): Flow<List<UserEntity>> = usersFlow.map(::sortUsers)

        override suspend fun getUserById(id: Long): UserEntity? = users.firstOrNull { it.id == id }

        override suspend fun insertUser(user: UserEntity): Long {
            val assignedId = if (user.id == 0L) nextId++ else user.id
            users += user.copy(id = assignedId)
            emit()
            return assignedId
        }

        override suspend fun updateUser(user: UserEntity) {
            val index = users.indexOfFirst { it.id == user.id }
            if (index >= 0) {
                users[index] = user
                emit()
            }
        }

        override suspend fun deleteUser(user: UserEntity) {
            deleteUserById(user.id)
        }

        override fun deleteUserById(id: Long): Int {
            val before = users.size
            users.removeAll { it.id == id }
            emit()
            return before - users.size
        }

        fun snapshot(): List<User> = sortUsers(users).map {
            User(
                id = it.id,
                name = it.name,
                email = it.email,
                role = it.role,
                phone = it.phone,
                darkModeEnabled = it.darkModeEnabled,
                languageTag = it.languageTag,
                isActive = it.isActive,
                updatedAt = it.updatedAt
            )
        }

        private fun emit() {
            usersFlow.value = sortUsers(users)
        }

        private fun sortUsers(input: List<UserEntity>): List<UserEntity> =
            input.sortedWith(
                compareByDescending<UserEntity> { it.isActive }
                    .thenByDescending { it.updatedAt }
                    .thenBy { it.name }
            )
    }
}
