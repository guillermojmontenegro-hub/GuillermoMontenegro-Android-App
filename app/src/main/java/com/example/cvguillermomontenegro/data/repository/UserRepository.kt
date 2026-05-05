package com.example.cvguillermomontenegro.data.repository

import com.example.cvguillermomontenegro.data.local.UserDao
import com.example.cvguillermomontenegro.data.mapper.toDomain
import com.example.cvguillermomontenegro.data.mapper.toEntity
import com.example.cvguillermomontenegro.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers().map { users ->
        users.map { it.toDomain() }
    }

    suspend fun getUserById(id: Long): User? = userDao.getUserById(id)?.toDomain()

    suspend fun saveUser(user: User): Long {
        return if (user.id == 0L) {
            userDao.insertUser(user.toEntity())
        } else {
            userDao.updateUser(user.toEntity())
            user.id
        }
    }

    suspend fun deleteUserById(id: Long) = userDao.deleteUserById(id)
}
