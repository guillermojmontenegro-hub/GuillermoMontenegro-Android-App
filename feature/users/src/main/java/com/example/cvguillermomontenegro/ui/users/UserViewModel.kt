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
            val user = getUserByIdUseCase(userId) ?: return@launch
            _formState.value = user.toFormState()
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
        it.copy(phone = formatPhoneNumber(value))
    }

    fun saveUser(onSuccess: () -> Unit) {
        val current = formState.value
        val validation = validate(current)
        if (!validation) return

        viewModelScope.launch {
            _formState.update { it.copy(isSaving = true) }
            saveUserUseCase(
                User(
                    id = current.id,
                    name = current.name.trim(),
                    email = current.email.trim(),
                    role = current.role.trim(),
                    phone = current.phone.trim(),
                    updatedAt = System.currentTimeMillis()
                )
            )
            _formState.value = UserFormState()
            onSuccess()
        }
    }

    fun deleteUser(id: Long) {
        viewModelScope.launch {
            deleteUserByIdUseCase(id)
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
        phone = formatPhoneNumber(phone)
    )

    private fun formatPhoneNumber(input: String): String {
        val digits = input.filter(Char::isDigit).take(13)
        if (digits.isEmpty()) return ""

        val groups = listOf(2, 1, 2, 4, 4)
        var index = 0
        val parts = mutableListOf<String>()

        for (size in groups) {
            if (index >= digits.length) break
            val end = (index + size).coerceAtMost(digits.length)
            parts += digits.substring(index, end)
            index = end
        }

        return buildString {
            append("+")
            append(parts.joinToString("-"))
        }
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    }
}
