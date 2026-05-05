package com.example.cvguillermomontenegro.domain.usecase

import com.example.cvguillermomontenegro.data.repository.ProfileRepository
import com.example.cvguillermomontenegro.domain.model.Profile
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(language: String = "es"): Profile {
        return profileRepository.getProfile(language)
    }
}
