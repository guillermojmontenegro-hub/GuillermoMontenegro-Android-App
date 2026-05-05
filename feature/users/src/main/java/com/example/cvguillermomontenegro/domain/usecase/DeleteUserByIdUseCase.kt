package com.example.cvguillermomontenegro.domain.usecase

import com.example.cvguillermomontenegro.data.repository.UserRepository
import javax.inject.Inject

class DeleteUserByIdUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(id: Long) = userRepository.deleteUserById(id)
}
