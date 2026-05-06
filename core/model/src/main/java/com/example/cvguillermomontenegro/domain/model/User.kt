package com.example.cvguillermomontenegro.domain.model

data class User(
    val id: Long = 0,
    val name: String = "",
    val email: String = "",
    val role: String = "",
    val phone: String = "",
    val darkModeEnabled: Boolean = false,
    val languageTag: String = "es",
    val isActive: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
