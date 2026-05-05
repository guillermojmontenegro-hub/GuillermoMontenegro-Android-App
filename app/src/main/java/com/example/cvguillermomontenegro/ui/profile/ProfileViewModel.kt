package com.example.cvguillermomontenegro.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cvguillermomontenegro.data.repository.ProfileRepository
import com.example.cvguillermomontenegro.domain.model.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = true,
    val profile: Profile? = null,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching { profileRepository.getProfile() }
                .onSuccess { _uiState.value = ProfileUiState(isLoading = false, profile = it) }
                .onFailure { _uiState.value = ProfileUiState(isLoading = false, error = it.message) }
        }
    }
}
