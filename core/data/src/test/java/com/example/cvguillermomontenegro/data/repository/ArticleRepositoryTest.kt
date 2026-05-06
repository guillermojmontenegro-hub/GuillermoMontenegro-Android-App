package com.example.cvguillermomontenegro.data.repository

import com.example.cvguillermomontenegro.data.remote.ArticleApi
import com.example.cvguillermomontenegro.data.remote.ArticleDto
import com.example.cvguillermomontenegro.data.remote.ArticlesResponseDto
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ArticleRepositoryTest {

    @Test
    fun getArticles_mapsDtosToDomain() = runTest {
        val repository = ArticleRepository(
            FakeArticleApi(
                articles = listOf(
                    ArticleDto(
                        slug = "srow",
                        title = "SROW",
                        date = "2025-09-13",
                        description = "desc",
                        author = "author",
                        previewImageUrl = "asset:///preview.jpg",
                        tags = listOf("AI"),
                        content = "content"
                    )
                )
            )
        )

        val articles = repository.getArticles()

        assertEquals(1, articles.size)
        assertEquals("srow", articles.single().slug)
        assertEquals("SROW", articles.single().title)
        assertEquals(listOf("AI"), articles.single().tags)
    }

    @Test
    fun getArticle_returnsMappedDomainItem() = runTest {
        val repository = ArticleRepository(
            FakeArticleApi(
                article = ArticleDto(
                    slug = "deterministic",
                    title = "Determinism in LLMs",
                    date = "2025-09-19",
                    description = "desc",
                    author = "author",
                    previewImageUrl = "asset:///preview.jpg",
                    tags = listOf("Engineering"),
                    content = "content"
                )
            )
        )

        val article = repository.getArticle("deterministic")

        assertEquals("deterministic", article.slug)
        assertEquals("Determinism in LLMs", article.title)
    }

    private class FakeArticleApi(
        private val articles: List<ArticleDto> = emptyList(),
        private val article: ArticleDto? = articles.firstOrNull()
    ) : ArticleApi {
        override suspend fun getArticles(): ArticlesResponseDto = ArticlesResponseDto(articles)

        override suspend fun getArticle(slug: String): ArticleDto = requireNotNull(article)
    }
}
