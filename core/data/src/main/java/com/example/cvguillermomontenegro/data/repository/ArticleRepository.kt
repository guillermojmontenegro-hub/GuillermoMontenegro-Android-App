package com.example.cvguillermomontenegro.data.repository

import com.example.cvguillermomontenegro.data.remote.ArticleApi
import com.example.cvguillermomontenegro.data.remote.toDomain
import com.example.cvguillermomontenegro.domain.model.Article
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArticleRepository @Inject constructor(
    private val articleApi: ArticleApi
) {
    suspend fun getArticles(): List<Article> = articleApi.getArticles().articles.map { it.toDomain() }

    suspend fun getArticle(slug: String): Article = articleApi.getArticle(slug).toDomain()
}
