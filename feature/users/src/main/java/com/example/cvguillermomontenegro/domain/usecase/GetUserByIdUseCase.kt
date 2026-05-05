package com.example.cvguillermomontenegro.domain.usecase

import com.example.cvguillermomontenegro.data.repository.UserRepository
import com.example.cvguillermomontenegro.domain.model.User
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(id: Long): User? = userRepository.getUserById(id)
}
