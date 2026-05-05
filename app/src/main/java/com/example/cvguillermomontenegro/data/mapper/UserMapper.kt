package com.example.cvguillermomontenegro.data.mapper

import com.example.cvguillermomontenegro.data.local.UserEntity
import com.example.cvguillermomontenegro.domain.model.User

fun UserEntity.toDomain(): User = User(
    id = id,
    name = name,
    email = email,
    role = role,
    phone = phone,
    updatedAt = updatedAt
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    name = name,
    email = email,
    role = role,
    phone = phone,
    updatedAt = updatedAt
)
