package com.example.cvguillermomontenegro.data.repository

import android.content.Context
import com.example.cvguillermomontenegro.domain.model.Profile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json
) {
    suspend fun getProfile(language: String = "es"): Profile {
        val fileName = if (language == "en") "profile/profile.en.json" else "profile/profile.es.json"
        val raw = context.assets.open(fileName).bufferedReader().use { it.readText() }
        return json.decodeFromString(raw)
    }
}
