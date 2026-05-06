package com.example.cvguillermomontenegro.ui.articles

import com.example.cvguillermomontenegro.data.remote.ArticleApi
import com.example.cvguillermomontenegro.data.remote.ArticleDto
import com.example.cvguillermomontenegro.data.remote.ArticlesResponseDto
import com.example.cvguillermomontenegro.data.repository.ArticleRepository
import com.example.cvguillermomontenegro.domain.model.Article
import com.example.cvguillermomontenegro.domain.usecase.GetArticlesUseCase
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArticlesViewModelTest {

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
    fun init_loadsArticles_andExposesSortedTags() = runTest(dispatcher) {
        val viewModel = createViewModel(
            FakeArticleApi(
                articles = listOf(
                    articleDto(
                        slug = "b",
                        title = "Second",
                        date = "2025-01-01",
                        tags = listOf("Kotlin", "Android")
                    ),
                    articleDto(
                        slug = "a",
                        title = "First",
                        date = "2025-02-10",
                        tags = listOf("AI", "Android")
                    )
                )
            )
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.articles.size)
        assertEquals(listOf("AI", "Android", "Kotlin"), state.availableTags)
        assertNull(state.error)
    }

    @Test
    fun updateQuery_andToggleTag_filterArticles() = runTest(dispatcher) {
        val viewModel = createViewModel(
            FakeArticleApi(
                articles = listOf(
                    articleDto(
                        slug = "compose",
                        title = "Jetpack Compose Patterns",
                        description = "Declarative UI",
                        content = "Compose article",
                        date = "2025-03-01",
                        tags = listOf("Android")
                    ),
                    articleDto(
                        slug = "agents",
                        title = "Agents at Work",
                        description = "Multi-agent systems",
                        content = "Tool calling and planning",
                        date = "2025-04-01",
                        tags = listOf("AI")
                    )
                )
            )
        )
        advanceUntilIdle()

        viewModel.updateQuery("tool calling")
        viewModel.toggleTag("AI")

        val state = viewModel.uiState.value
        assertEquals("tool calling", state.query)
        assertEquals("AI", state.selectedTag)
        assertEquals(listOf("agents"), state.filteredArticles.map(Article::slug))

        viewModel.toggleTag("AI")

        assertNull(viewModel.uiState.value.selectedTag)
    }

    @Test
    fun refresh_whenUseCaseFails_setsError() = runTest(dispatcher) {
        val viewModel = createViewModel(
            FakeArticleApi(
                shouldFailArticles = true
            )
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.articles.isEmpty())
        assertEquals("articles unavailable", state.error)
    }

    private fun createViewModel(api: FakeArticleApi): ArticlesViewModel {
        return ArticlesViewModel(
            getArticlesUseCase = GetArticlesUseCase(
                ArticleRepository(api)
            )
        )
    }

    private fun articleDto(
        slug: String,
        title: String,
        description: String = "Description",
        content: String = "Content",
        date: String,
        tags: List<String>
    ) = ArticleDto(
        slug = slug,
        title = title,
        date = date,
        description = description,
        author = "author",
        previewImageUrl = "asset:///preview.png",
        tags = tags,
        content = content
    )

    private class FakeArticleApi(
        private val articles: List<ArticleDto> = emptyList(),
        private val shouldFailArticles: Boolean = false
    ) : ArticleApi {
        override suspend fun getArticles(): ArticlesResponseDto {
            if (shouldFailArticles) error("articles unavailable")
            return ArticlesResponseDto(articles)
        }

        override suspend fun getArticle(slug: String): ArticleDto {
            return articles.first { it.slug == slug }
        }
    }
}
