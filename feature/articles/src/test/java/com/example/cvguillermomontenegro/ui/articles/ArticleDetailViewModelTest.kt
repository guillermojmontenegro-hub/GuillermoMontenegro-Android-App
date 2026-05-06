package com.example.cvguillermomontenegro.ui.articles

import androidx.lifecycle.SavedStateHandle
import com.example.cvguillermomontenegro.data.remote.ArticleApi
import com.example.cvguillermomontenegro.data.remote.ArticleDto
import com.example.cvguillermomontenegro.data.remote.ArticlesResponseDto
import com.example.cvguillermomontenegro.data.repository.ArticleRepository
import com.example.cvguillermomontenegro.domain.usecase.GetArticleBySlugUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArticleDetailViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_loadsArticleBySlug() = runTest(dispatcher) {
        val viewModel = ArticleDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("slug" to "tool-calling")),
            getArticleBySlugUseCase = GetArticleBySlugUseCase(
                ArticleRepository(
                    FakeArticleApi(
                        article = ArticleDto(
                            slug = "tool-calling",
                            title = "Tools for LLMs step by step",
                            date = "2025-09-29",
                            description = "desc",
                            author = "author",
                            previewImageUrl = "asset:///preview.jpg",
                            tags = listOf("Agents"),
                            content = "content"
                        )
                    )
                )
            )
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("tool-calling", state.article?.slug)
        assertNull(state.error)
    }

    @Test
    fun init_whenArticleFails_setsError() = runTest(dispatcher) {
        val viewModel = ArticleDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("slug" to "missing")),
            getArticleBySlugUseCase = GetArticleBySlugUseCase(
                ArticleRepository(FakeArticleApi(shouldFail = true))
            )
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.article)
        assertEquals("article unavailable", state.error)
    }

    private class FakeArticleApi(
        private val article: ArticleDto? = null,
        private val shouldFail: Boolean = false
    ) : ArticleApi {
        override suspend fun getArticles(): ArticlesResponseDto = ArticlesResponseDto(emptyList())

        override suspend fun getArticle(slug: String): ArticleDto {
            if (shouldFail) error("article unavailable")
            return requireNotNull(article)
        }
    }
}
