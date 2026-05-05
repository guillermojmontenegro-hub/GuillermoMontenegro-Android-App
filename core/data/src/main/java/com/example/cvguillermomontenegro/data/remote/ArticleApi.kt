package com.example.cvguillermomontenegro.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface ArticleApi {
    @GET("articles")
    suspend fun getArticles(): ArticlesResponseDto

    @GET("articles/{slug}")
    suspend fun getArticle(@Path("slug") slug: String): ArticleDto
}
