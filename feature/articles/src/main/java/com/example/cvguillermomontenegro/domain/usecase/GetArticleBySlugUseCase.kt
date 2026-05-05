package com.example.cvguillermomontenegro.domain.usecase

import com.example.cvguillermomontenegro.data.repository.ArticleRepository
import com.example.cvguillermomontenegro.domain.model.Article
import javax.inject.Inject

class GetArticleBySlugUseCase @Inject constructor(
    private val articleRepository: ArticleRepository
) {
    suspend operator fun invoke(slug: String): Article = articleRepository.getArticle(slug)
}
