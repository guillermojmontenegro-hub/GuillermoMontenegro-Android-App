package com.example.cvguillermomontenegro.data.remote

import com.example.cvguillermomontenegro.domain.model.Article

data class ArticleDto(
    val slug: String,
    val title: String,
    val date: String,
    val description: String,
    val author: String,
    val previewImageUrl: String,
    val tags: List<String>,
    val content: String
)

data class ArticlesResponseDto(
    val articles: List<ArticleDto>
)

fun ArticleDto.toDomain(): Article = Article(
    slug = slug,
    title = title,
    date = date,
    description = description,
    author = author,
    previewImageUrl = previewImageUrl,
    tags = tags,
    content = content
)
