package com.example.cvguillermomontenegro.ui.articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cvguillermomontenegro.data.repository.ArticleRepository
import com.example.cvguillermomontenegro.domain.model.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ArticlesUiState(
    val isLoading: Boolean = true,
    val articles: List<Article> = emptyList(),
    val query: String = "",
    val selectedTag: String? = null,
    val error: String? = null
) {
    val availableTags: List<String> = articles.flatMap { it.tags }.distinct().sorted()

    val filteredArticles: List<Article> = articles
        .filter { article ->
            query.isBlank() || listOf(article.title, article.description, article.content)
                .any { text -> text.contains(query, ignoreCase = true) }
        }
        .filter { article ->
            selectedTag == null || article.tags.contains(selectedTag)
        }
        .sortedByDescending { it.date }
}

@HiltViewModel
class ArticlesViewModel @Inject constructor(
    private val articleRepository: ArticleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArticlesUiState())
    val uiState: StateFlow<ArticlesUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { articleRepository.getArticles() }
                .onSuccess { _uiState.value = ArticlesUiState(isLoading = false, articles = it) }
                .onFailure { _uiState.value = ArticlesUiState(isLoading = false, error = it.message) }
        }
    }

    fun updateQuery(value: String) {
        _uiState.update { it.copy(query = value) }
    }

    fun toggleTag(tag: String) {
        _uiState.update {
            it.copy(selectedTag = if (it.selectedTag == tag) null else tag)
        }
    }
}
