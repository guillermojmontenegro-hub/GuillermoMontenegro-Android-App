package com.example.cvguillermomontenegro.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.annotation.StringRes
import com.example.cvguillermomontenegro.feature.users.R
import com.example.cvguillermomontenegro.domain.model.User
import com.example.cvguillermomontenegro.domain.usecase.DeleteUserByIdUseCase
import com.example.cvguillermomontenegro.domain.usecase.GetUserByIdUseCase
import com.example.cvguillermomontenegro.domain.usecase.GetUsersUseCase
import com.example.cvguillermomontenegro.domain.usecase.SaveUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserFormState(
    val id: Long = 0,
    val name: String = "",
    val email: String = "",
    val role: String = "",
    val phone: String = "",
    @StringRes val nameError: Int? = null,
    @StringRes val emailError: Int? = null,
    val isSaving: Boolean = false
)

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val saveUserUseCase: SaveUserUseCase,
    private val deleteUserByIdUseCase: DeleteUserByIdUseCase
) : ViewModel() {

    init {
        ensureDefaultUser()
    }

    val users: StateFlow<List<User>> = getUsersUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _formState = MutableStateFlow(UserFormState())
    val formState: StateFlow<UserFormState> = _formState.asStateFlow()

    fun loadUser(userId: Long?) {
        if (userId == null || userId <= 0) {
            _formState.value = UserFormState()
            return
        }

        viewModelScope.launch {
            val user = getUserByIdUseCase(userId)
            _formState.value = user?.toFormState() ?: UserFormState()
        }
    }

    fun updateName(value: String) = _formState.update {
        it.copy(name = value, nameError = null)
    }

    fun updateEmail(value: String) = _formState.update {
        it.copy(email = value, emailError = null)
    }

    fun updateRole(value: String) = _formState.update { it.copy(role = value) }

    fun updatePhone(value: String) = _formState.update {
        it.copy(phone = PhoneNumberFormatter.normalize(value))
    }

    fun saveUser(onSuccess: () -> Unit) {
        val current = formState.value
        val validation = validate(current)
        if (!validation) return

        viewModelScope.launch {
            _formState.update { it.copy(isSaving = true) }
            val existingUser = current.id.takeIf { it > 0 }?.let { id ->
                users.value.firstOrNull { it.id == id }
            }
            saveUserUseCase(
                User(
                    id = current.id,
                    name = current.name.trim(),
                    email = current.email.trim(),
                    role = current.role.trim(),
                    phone = PhoneNumberFormatter.format(current.phone),
                    darkModeEnabled = existingUser?.darkModeEnabled ?: false,
                    languageTag = existingUser?.languageTag ?: "es",
                    isActive = existingUser?.isActive ?: false,
                    updatedAt = System.currentTimeMillis()
                )
            )
            _formState.value = UserFormState()
            onSuccess()
        }
    }

    fun deleteUser(id: Long) {
        viewModelScope.launch {
            val wasActive = users.value.firstOrNull { it.id == id }?.isActive == true
            deleteUserByIdUseCase(id)
            if (wasActive) {
                users.value.firstOrNull { it.id != id }?.let { selectActiveUser(it) }
            }
        }
    }

    fun setDarkMode(user: User, enabled: Boolean) {
        saveSettings(user.copy(darkModeEnabled = enabled))
    }

    fun selectActiveUser(user: User) {
        viewModelScope.launch {
            users.value.forEach { currentUser ->
                val shouldBeActive = currentUser.id == user.id
                if (currentUser.isActive != shouldBeActive) {
                    saveUserUseCase(
                        currentUser.copy(
                            isActive = shouldBeActive,
                            updatedAt = if (shouldBeActive) System.currentTimeMillis() else currentUser.updatedAt
                        )
                    )
                }
            }
        }
    }

    private fun validate(state: UserFormState): Boolean {
        val nameError = if (state.name.trim().isBlank()) R.string.user_error_name_required else null
        val emailError = if (!EMAIL_REGEX.matches(state.email.trim())) R.string.user_error_invalid_email else null
        _formState.update {
            it.copy(nameError = nameError, emailError = emailError)
        }
        return nameError == null && emailError == null
    }

    private fun User.toFormState(): UserFormState = UserFormState(
        id = id,
        name = name,
        email = email,
        role = role,
        phone = PhoneNumberFormatter.normalize(phone)
    )

    private fun saveSettings(user: User) {
        viewModelScope.launch {
            saveUserUseCase(user.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    private fun ensureDefaultUser() {
        viewModelScope.launch {
            val currentUsers = getUsersUseCase().first()
            if (currentUsers.isNotEmpty()) {
                currentUsers
                    .firstOrNull { it.email == DEFAULT_USER_EMAIL && it.role == "Developer" }
                    ?.let { saveUserUseCase(it.copy(role = DEFAULT_USER_ROLE)) }
                return@launch
            }

            saveUserUseCase(
                User(
                    name = DEFAULT_USER_NAME,
                    email = DEFAULT_USER_EMAIL,
                    role = DEFAULT_USER_ROLE,
                    phone = "",
                    darkModeEnabled = true,
                    languageTag = "es",
                    isActive = true,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        const val DEFAULT_USER_NAME = "Guillermo Montenegro"
        const val DEFAULT_USER_EMAIL = "guillermo@example.com"
        const val DEFAULT_USER_ROLE = "Desarrollador"
    }
}
