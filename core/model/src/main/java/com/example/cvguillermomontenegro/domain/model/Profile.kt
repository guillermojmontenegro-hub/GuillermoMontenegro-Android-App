package com.example.cvguillermomontenegro.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val name: String,
    val headline: String,
    val summary: String,
    val skills: List<String>,
    val experience: List<ExperienceItem>,
    val projects: List<String>,
    val education: List<EducationItem>,
    val languages: List<LanguageItem>,
    val contact: ContactData
)

@Serializable
data class ExperienceItem(
    val company: String,
    val role: String,
    val period: String,
    val highlights: List<String>
)

@Serializable
data class EducationItem(
    val title: String,
    val period: String,
    val institution: String
)

@Serializable
data class LanguageItem(
    val name: String,
    val level: String
)

@Serializable
data class ContactData(
    val email: String,
    val location: String,
    val linkedin: String,
    val github: String
)
