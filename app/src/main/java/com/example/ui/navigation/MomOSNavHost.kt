package com.example.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.MomOSApplication
import com.example.ui.screens.ExpensesScreen
import com.example.ui.screens.FamilyScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MoreScreen
import com.example.ui.screens.NotesScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ShoppingScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.screens.WidgetsScreen
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.viewmodel.ExpenseViewModel
import com.example.ui.viewmodel.FamilyViewModel
import com.example.ui.viewmodel.HomeViewModel
import com.example.ui.viewmodel.NoteViewModel
import com.example.ui.viewmodel.SettingsViewModel
import com.example.ui.viewmodel.ShoppingViewModel
import com.example.ui.viewmodel.TaskViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun MomOSApp(
    app: MomOSApplication
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val userProfile by app.userRepository.userProfile.collectAsStateWithLifecycle(initialValue = null)

    // Check if onboarding needs to be shown on first open
    LaunchedEffect(userProfile) {
        val profile = userProfile
        if (profile != null && !profile.hasCompletedOnboarding && currentRoute != Screen.Onboarding.route && currentRoute != Screen.Splash.route) {
            navController.navigate(Screen.Onboarding.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // ViewModels with factories
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.provideFactory(
            app.userRepository,
            app.taskRepository,
            app.shoppingRepository,
            app.expenseRepository,
            app.familyRepository,
            app.noteRepository
        )
    )

    val shoppingViewModel: ShoppingViewModel = viewModel(
        factory = ShoppingViewModel.provideFactory(app.shoppingRepository)
    )

    val taskViewModel: TaskViewModel = viewModel(
        factory = TaskViewModel.provideFactory(app.taskRepository)
    )

    val expenseViewModel: ExpenseViewModel = viewModel(
        factory = ExpenseViewModel.provideFactory(app.expenseRepository)
    )

    val familyViewModel: FamilyViewModel = viewModel(
        factory = FamilyViewModel.provideFactory(app.familyRepository)
    )

    val noteViewModel: NoteViewModel = viewModel(
        factory = NoteViewModel.provideFactory(app.noteRepository)
    )

    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.provideFactory(
            app.userRepository,
            app.shoppingRepository,
            app.taskRepository,
            app.expenseRepository,
            app.familyRepository,
            app.noteRepository
        )
    )

    // Listen to feedback events across viewmodels
    LaunchedEffect(Unit) {
        launch {
            homeViewModel.feedbackEvents.collectLatest { msg ->
                snackbarHostState.showSnackbar(msg)
            }
        }
        launch {
            shoppingViewModel.feedbackEvents.collectLatest { msg ->
                snackbarHostState.showSnackbar(msg)
            }
        }
        launch {
            taskViewModel.feedbackEvents.collectLatest { msg ->
                snackbarHostState.showSnackbar(msg)
            }
        }
        launch {
            expenseViewModel.feedbackEvents.collectLatest { msg ->
                snackbarHostState.showSnackbar(msg)
            }
        }
        launch {
            familyViewModel.feedbackEvents.collectLatest { msg ->
                snackbarHostState.showSnackbar(msg)
            }
        }
        launch {
            noteViewModel.feedbackEvents.collectLatest { msg ->
                snackbarHostState.showSnackbar(msg)
            }
        }
        launch {
            settingsViewModel.feedbackEvents.collectLatest { msg ->
                snackbarHostState.showSnackbar(msg)
            }
        }
    }

    val topLevelScreens = remember { Screen.bottomNavItems.filterNotNull() }
    val isTopLevelDestination = topLevelScreens.any { it.route == currentRoute }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (isTopLevelDestination) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                    modifier = Modifier.testTag("momos_bottom_navigation")
                ) {
                    topLevelScreens.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryIndigo,
                                selectedTextColor = PrimaryIndigo,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("nav_item_${screen.route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Splash.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Screen.Splash.route) {
                    SplashScreen(
                        onSplashFinished = {
                            val profile = userProfile
                            if (profile != null && !profile.hasCompletedOnboarding) {
                                navController.navigate(Screen.Onboarding.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            } else {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            }
                        }
                    )
                }

                composable(Screen.Home.route) {
                    HomeScreen(
                        viewModel = homeViewModel,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(Screen.Tasks.route) {
                    TasksScreen(viewModel = taskViewModel)
                }

                composable(Screen.Shopping.route) {
                    ShoppingScreen(viewModel = shoppingViewModel)
                }

                composable(Screen.Family.route) {
                    FamilyScreen(viewModel = familyViewModel)
                }

                composable(Screen.More.route) {
                    MoreScreen(
                        viewModel = settingsViewModel,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(Screen.Expenses.route) {
                    ExpensesScreen(
                        viewModel = expenseViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Notes.route) {
                    NotesScreen(
                        viewModel = noteViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Settings.route) {
                    MoreScreen(
                        viewModel = settingsViewModel,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(Screen.Onboarding.route) {
                    OnboardingScreen(
                        currentName = userProfile?.name ?: "",
                        onComplete = { name, voiceLang, notifications ->
                            scope.launch {
                                val current = userProfile ?: com.example.data.model.User()
                                app.userRepository.updateProfile(
                                    current.copy(
                                        name = name,
                                        voiceLanguage = voiceLang,
                                        notificationsEnabled = notifications,
                                        hasCompletedOnboarding = true
                                    )
                                )
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                                }
                            }
                        }
                    )
                }

                composable(Screen.Widgets.route) {
                    WidgetsScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
