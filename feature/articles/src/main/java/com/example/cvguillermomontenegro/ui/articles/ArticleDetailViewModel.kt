package com.example.cvguillermomontenegro.ui.articles

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cvguillermomontenegro.domain.model.Article
import com.example.cvguillermomontenegro.domain.usecase.GetArticleBySlugUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ArticleDetailUiState(
    val isLoading: Boolean = true,
    val article: Article? = null,
    val error: String? = null
)

@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getArticleBySlugUseCase: GetArticleBySlugUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArticleDetailUiState())
    val uiState: StateFlow<ArticleDetailUiState> = _uiState.asStateFlow()

    init {
        val slug = savedStateHandle.get<String>("slug").orEmpty()
        viewModelScope.launch {
            runCatching { getArticleBySlugUseCase(slug) }
                .onSuccess { _uiState.value = ArticleDetailUiState(isLoading = false, article = it) }
                .onFailure { _uiState.value = ArticleDetailUiState(isLoading = false, error = it.message) }
        }
    }
}
