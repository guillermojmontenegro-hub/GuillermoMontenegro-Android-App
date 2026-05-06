package com.example.cvguillermomontenegro.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import com.example.cvguillermomontenegro.ui.i18n.localizedStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cvguillermomontenegro.R
import com.example.cvguillermomontenegro.domain.model.User
import com.example.cvguillermomontenegro.ui.components.UserAvatar
import com.example.cvguillermomontenegro.ui.screens.articles.ArticleDetailScreen
import com.example.cvguillermomontenegro.ui.screens.articles.ArticlesScreen
import com.example.cvguillermomontenegro.ui.screens.home.HomeScreen
import com.example.cvguillermomontenegro.ui.screens.onboarding.OnboardingScreen
import com.example.cvguillermomontenegro.ui.screens.users.UserFormScreen
import com.example.cvguillermomontenegro.ui.screens.users.UsersScreen
import com.example.cvguillermomontenegro.ui.users.UserViewModel
import kotlinx.coroutines.launch
import androidx.compose.material3.rememberDrawerState

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    userViewModel: UserViewModel = hiltViewModel(),
    activeUser: User? = null,
    languageTag: String = "es",
    darkModeEnabled: Boolean = false
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("cv_app_prefs", android.content.Context.MODE_PRIVATE) }
    val shouldShowOnboarding = remember { !prefs.getBoolean("onboarding_seen", false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    CVScaffold(
        navController = navController,
        activeUser = activeUser,
        onOpenDrawer = { scope.launch { drawerState.open() } },
        onNavigate = { route ->
            scope.launch { drawerState.close() }
            navController.navigate(route)
        }
    ) { innerModifier, currentRoute ->
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = currentRoute in setOf(Routes.HOME, Routes.USERS),
            drawerContent = {
                AppDrawerContent(
                    activeUser = activeUser,
                    currentRoute = currentRoute,
                    darkModeEnabled = darkModeEnabled,
                    onClose = { scope.launch { drawerState.close() } },
                    onNavigateHome = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Routes.HOME)
                    },
                    onNavigateUsers = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Routes.USERS)
                    },
                    onNavigateAbout = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Routes.ONBOARDING)
                    },
                    onToggleDarkMode = { user ->
                        userViewModel.setDarkMode(user, !user.darkModeEnabled)
                    }
                )
            }
        ) {
            NavHost(
                navController = navController,
                startDestination = if (shouldShowOnboarding) Routes.ONBOARDING else Routes.HOME,
                modifier = modifier.then(innerModifier)
            ) {
                composable(Routes.ONBOARDING) {
                    OnboardingScreen(
                        onFinish = {
                            prefs.edit().putBoolean("onboarding_seen", true).apply()
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.ONBOARDING) { inclusive = true }
                            }
                        }
                    )
                }
                composable(Routes.HOME) {
                    HomeScreen(
                        languageTag = languageTag,
                        onOpenArticles = { navController.navigate(Routes.ARTICLES) }
                    )
                }
                composable(Routes.ARTICLES) {
                    ArticlesScreen(
                        onArticleClick = { slug -> navController.navigate(Routes.articleDetail(slug)) }
                    )
                }
                composable(
                    route = Routes.ARTICLE_DETAIL,
                    arguments = listOf(navArgument("slug") { type = NavType.StringType })
                ) {
                    ArticleDetailScreen()
                }
                composable(Routes.USERS) {
                    UsersScreen(
                        onCreateUser = { navController.navigate(Routes.USER_FORM) },
                        onEditUser = { id -> navController.navigate(Routes.userForm(id)) }
                    )
                }
                composable(Routes.USER_FORM) {
                    UserFormScreen(userId = null, onSaved = { navController.popBackStack() })
                }
                composable(
                    route = Routes.USER_FORM_WITH_ID,
                    arguments = listOf(navArgument("userId") { type = NavType.LongType })
                ) { backStackEntry ->
                    UserFormScreen(
                        userId = backStackEntry.arguments?.getLong("userId"),
                        onSaved = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

@Composable
private fun AppDrawerContent(
    activeUser: User?,
    currentRoute: String,
    darkModeEnabled: Boolean,
    onClose: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateUsers: () -> Unit,
    onNavigateAbout: () -> Unit,
    onToggleDarkMode: (User) -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.fillMaxHeight(),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                UserAvatar(
                    label = activeUser?.name?.take(1) ?: localizedStringResource(R.string.drawer_default_avatar),
                    modifier = Modifier.size(52.dp)
                )
                Text(
                    text = activeUser?.name ?: localizedStringResource(R.string.drawer_no_active_user),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = activeUser?.email ?: localizedStringResource(R.string.drawer_no_active_user_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                activeUser?.role?.takeIf { it.isNotBlank() }?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            NavigationDrawerItem(
                label = { Text(localizedStringResource(R.string.drawer_home)) },
                selected = currentRoute == Routes.HOME,
                onClick = onNavigateHome,
                icon = { Icon(Icons.Default.Home, contentDescription = null) }
            )
            NavigationDrawerItem(
                label = { Text(localizedStringResource(R.string.drawer_users_list)) },
                selected = currentRoute == Routes.USERS,
                onClick = onNavigateUsers,
                icon = { Icon(Icons.Default.Group, contentDescription = null) }
            )

            val darkModeLabel = if (darkModeEnabled) {
                localizedStringResource(R.string.drawer_light_mode)
            } else {
                localizedStringResource(R.string.drawer_dark_mode)
            }
            NavigationDrawerItem(
                label = { Text(darkModeLabel) },
                selected = darkModeEnabled,
                onClick = { activeUser?.let(onToggleDarkMode) },
                icon = { Icon(Icons.Default.DarkMode, contentDescription = null) }
            )

            NavigationDrawerItem(
                label = { Text(localizedStringResource(R.string.drawer_about_app)) },
                selected = currentRoute == Routes.ONBOARDING,
                onClick = onNavigateAbout,
                icon = { Icon(Icons.Default.Info, contentDescription = null) }
            )

            Spacer(modifier = Modifier.weight(1f))
            NavigationDrawerItem(
                label = { Text(localizedStringResource(R.string.drawer_close)) },
                selected = false,
                onClick = onClose,
                icon = { Icon(Icons.Default.ArrowBack, contentDescription = null) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CVScaffold(
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
                                imageVector = if (isTopLevel) Icons.Default.Menu else Icons.Default.ArrowBack,
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
                            Icons.Default.Article,
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
