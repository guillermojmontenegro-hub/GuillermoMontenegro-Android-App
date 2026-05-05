package com.example.cvguillermomontenegro.domain.usecase

import com.example.cvguillermomontenegro.data.repository.ArticleRepository
import com.example.cvguillermomontenegro.domain.model.Article
import javax.inject.Inject

class GetArticlesUseCase @Inject constructor(
    private val articleRepository: ArticleRepository
) {
    suspend operator fun invoke(): List<Article> = articleRepository.getArticles()
}
