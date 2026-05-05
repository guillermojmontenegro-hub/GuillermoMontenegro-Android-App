package com.example.cvguillermomontenegro.ui.screens.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.cvguillermomontenegro.R
import com.example.cvguillermomontenegro.ui.components.ErrorBox
import com.example.cvguillermomontenegro.ui.components.InfoRow
import com.example.cvguillermomontenegro.ui.components.LoadingBox
import com.example.cvguillermomontenegro.ui.components.SectionCard
import com.example.cvguillermomontenegro.ui.components.TagFlow
import com.example.cvguillermomontenegro.ui.profile.ProfileViewModel
import com.example.cvguillermomontenegro.ui.screens.collectAsStateWithLifecycleCompat

@Composable
fun HomeScreen(
    onOpenArticles: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycleCompat()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            if (state.isLoading) {
                LoadingBox(modifier = Modifier.padding(top = 32.dp))
                return@item
            }
            state.error?.let {
                ErrorBox(message = it, modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp))
                return@item
            }
            val profile = state.profile ?: return@item

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
                    .padding(16.dp)
                    .clip(MaterialTheme.shapes.large)
            ) {
                AsyncImage(
                    model = "file:///android_asset/profile/images/fotoCV.jpg",
                    contentDescription = profile.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.82f)
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = profile.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onTertiary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = profile.headline,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                    Text(
                        text = profile.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                    Button(
                        onClick = onOpenArticles,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text(stringResource(R.string.home_explore_articles))
                    }
                }
            }
        }

        state.profile?.let { profile ->
            item {
                SectionCard(
                    title = stringResource(R.string.home_specialties),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    TagFlow(tags = profile.skills)
                }
            }

            item {
                SectionCard(
                    title = stringResource(R.string.home_experience),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                        profile.experience.forEach { item ->
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = item.role,
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = stringResource(R.string.item_company_period, item.company, item.period),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                item.highlights.forEach { highlight ->
                                    Text(text = stringResource(R.string.item_bullet, "• ", highlight), style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SectionCard(
                        title = stringResource(R.string.home_projects),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            profile.projects.take(4).forEach { project ->
                                Text(text = stringResource(R.string.item_bullet, "• ", project), style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                    SectionCard(
                        title = stringResource(R.string.home_languages),
                        modifier = Modifier.width(148.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            profile.languages.forEach { language ->
                                Text(text = language.name, fontWeight = FontWeight.SemiBold)
                                Text(
                                    text = language.level,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            item {
                SectionCard(
                    title = stringResource(R.string.home_education),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    profile.education.forEach { education ->
                        Column(modifier = Modifier.padding(bottom = 10.dp)) {
                            Text(text = education.title, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = stringResource(R.string.item_company_period, education.institution, education.period),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                SectionCard(
                    title = stringResource(R.string.home_contact),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    InfoRow(label = stringResource(R.string.contact_location), value = profile.contact.location)
                    InfoRow(label = stringResource(R.string.contact_email), value = profile.contact.email) {
                        context.startActivity(
                            Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${profile.contact.email}"))
                        )
                    }
                    InfoRow(label = stringResource(R.string.contact_linkedin), value = profile.contact.linkedin) {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(profile.contact.linkedin)))
                    }
                    InfoRow(label = stringResource(R.string.contact_github), value = profile.contact.github) {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(profile.contact.github)))
                    }
                }
            }
        }
    }
}
