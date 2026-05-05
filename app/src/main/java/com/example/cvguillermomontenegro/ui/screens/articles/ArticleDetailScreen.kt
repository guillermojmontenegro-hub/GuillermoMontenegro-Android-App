package com.example.cvguillermomontenegro.ui.screens.articles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import com.example.cvguillermomontenegro.R
import com.halilibo.richtext.commonmark.CommonMarkdownParseOptions
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.material3.RichText
import com.example.cvguillermomontenegro.ui.articles.ArticleDetailViewModel
import com.example.cvguillermomontenegro.ui.components.ErrorBox
import com.example.cvguillermomontenegro.ui.components.LoadingBox
import com.example.cvguillermomontenegro.ui.components.TagFlow
import com.example.cvguillermomontenegro.ui.screens.collectAsStateWithLifecycleCompat

@Composable
fun ArticleDetailScreen(
    viewModel: ArticleDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycleCompat()

    when {
        state.isLoading -> LoadingBox(modifier = Modifier.fillMaxSize())
        state.error != null -> ErrorBox(message = state.error ?: stringResource(R.string.article_load_error), modifier = Modifier.padding(16.dp))
        state.article != null -> {
            val article = requireNotNull(state.article)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AsyncImage(
                    model = article.previewImageUrl.replace("asset:///", "file:///android_asset/"),
                    contentDescription = article.title,
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(text = stringResource(R.string.item_date_author, article.date, article.author))
                TagFlow(tags = article.tags)
                RichText {
                    Markdown(
                        content = article.content,
                        markdownParseOptions = CommonMarkdownParseOptions(autolink = true)
                    )
                }
            }
        }
    }
}
