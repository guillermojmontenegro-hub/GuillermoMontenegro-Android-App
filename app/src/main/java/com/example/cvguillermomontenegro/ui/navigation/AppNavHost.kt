package com.example.cvguillermomontenegro.ui.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cvguillermomontenegro.domain.model.User
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
    val drawerEnabledRoutes = remember { setOf(Routes.ONBOARDING, Routes.HOME, Routes.ARTICLES) }

    CVScaffold(
        navController = navController,
        activeUser = activeUser,
        onOpenDrawer = {
            scope.launch {
                if (drawerState.isClosed) {
                    drawerState.open()
                } else {
                    drawerState.close()
                }
            }
        },
        onNavigate = { route ->
            scope.launch { drawerState.close() }
            navController.navigate(route)
        }
    ) { innerModifier, currentRoute ->
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = currentRoute in drawerEnabledRoutes || currentRoute.startsWith("articleDetail/"),
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
