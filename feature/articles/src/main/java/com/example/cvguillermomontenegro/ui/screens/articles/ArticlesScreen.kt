package com.example.cvguillermomontenegro.ui.screens.articles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.cvguillermomontenegro.feature.articles.R
import com.example.cvguillermomontenegro.ui.articles.ArticlesViewModel
import com.example.cvguillermomontenegro.ui.components.ErrorBox
import com.example.cvguillermomontenegro.ui.components.LoadingBox
import com.example.cvguillermomontenegro.ui.components.SectionCard
import com.example.cvguillermomontenegro.ui.components.TagFlow
import com.example.cvguillermomontenegro.ui.screens.collectAsStateWithLifecycleCompat

@Composable
fun ArticlesScreen(
    onArticleClick: (String) -> Unit,
    viewModel: ArticlesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycleCompat()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionCard(title = stringResource(R.string.articles_library_title)) {
                OutlinedTextField(
                    value = state.query,
                    onValueChange = viewModel::updateQuery,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.articles_search_label)) },
                    singleLine = true
                )
                if (state.availableTags.isNotEmpty()) {
                    TagFlow(
                        tags = state.availableTags,
                        selectedTag = state.selectedTag,
                        onTagClick = viewModel::toggleTag
                    )
                }
            }
        }

        when {
            state.isLoading -> item { LoadingBox() }
            state.error != null -> item { ErrorBox(state.error ?: stringResource(R.string.articles_unknown_error)) }
            state.filteredArticles.isEmpty() -> item {
                ErrorBox(stringResource(R.string.articles_empty_filters))
            }
            else -> items(state.filteredArticles) { article ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onArticleClick(article.slug) }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AsyncImage(
                            model = article.previewImageUrl.replace("asset:///", "file:///android_asset/"),
                            contentDescription = article.title,
                            modifier = Modifier.fillMaxWidth().height(180.dp),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = article.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(text = article.date, style = MaterialTheme.typography.bodySmall)
                        Text(text = article.description)
                        TagFlow(tags = article.tags)
                    }
                }
            }
        }
    }
}
