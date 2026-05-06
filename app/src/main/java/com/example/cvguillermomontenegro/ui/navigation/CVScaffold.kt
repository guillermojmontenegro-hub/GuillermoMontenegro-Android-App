package com.example.cvguillermomontenegro.ui.navigation

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.cvguillermomontenegro.R
import com.example.cvguillermomontenegro.domain.model.User
import com.example.cvguillermomontenegro.ui.i18n.localizedStringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CVScaffold(
    navController: NavHostController,
    activeUser: User?,
    onOpenDrawer: () -> Unit,
    onNavigate: (String) -> Unit,
    content: @Composable (Modifier, String) -> Unit
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val destination = navBackStackEntry?.destination
    val route = destination?.route.orEmpty()
    val isTopLevel = route == Routes.HOME || route == Routes.USERS
    val showChrome = route != Routes.ONBOARDING
    val articleFabTransition = rememberInfiniteTransition(label = "articleFab")
    val articleFabScale by articleFabTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "articleFabScale"
    )

    Scaffold(
        topBar = {
            if (showChrome) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    title = {
                        Column {
                            Text(localizedStringResource(routeTitleRes(route)))
                            if (isTopLevel) {
                                Text(
                                    text = activeUser?.name ?: localizedStringResource(R.string.nav_no_active_user),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (isTopLevel) onOpenDrawer() else navController.popBackStack()
                            }
                        ) {
                            Icon(
                                imageVector = if (isTopLevel) Icons.Default.Menu else Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = localizedStringResource(
                                    if (isTopLevel) R.string.nav_open_menu else R.string.nav_go_back
                                )
                            )
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (route == Routes.HOME) {
                ExtendedFloatingActionButton(
                    onClick = { onNavigate(Routes.ARTICLES) },
                    modifier = Modifier.scale(articleFabScale),
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.Article,
                            contentDescription = localizedStringResource(R.string.fab_view_articles_content_description)
                        )
                    },
                    text = { Text(localizedStringResource(R.string.fab_view_articles)) }
                )
            } else if (route == Routes.USERS) {
                FloatingActionButton(onClick = { onNavigate(Routes.USER_FORM) }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            content(Modifier.fillMaxSize(), route)
        }
    }
}

private fun routeTitleRes(route: String): Int = when {
    route == Routes.ONBOARDING -> R.string.nav_title_onboarding
    route == Routes.ARTICLES -> R.string.nav_title_library
    route.startsWith("articleDetail/") || route == Routes.ARTICLE_DETAIL -> R.string.nav_title_article
    route.startsWith("userForm") -> R.string.nav_title_user
    route == Routes.USERS -> R.string.nav_title_admin
    else -> R.string.nav_title_home
}
