package com.example.cvguillermomontenegro.domain.model

data class Article(
    val slug: String,
    val title: String,
    val date: String,
    val description: String,
    val author: String,
    val previewImageUrl: String,
    val tags: List<String>,
    val content: String
)
