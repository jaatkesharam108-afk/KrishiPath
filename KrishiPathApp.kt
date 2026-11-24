package com.example.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.domain.model.SubjectCategory
import com.example.ui.components.tr
import com.example.ui.screens.AgricultureSubjectsScreen
import com.example.ui.screens.BioChemScreen
import com.example.ui.screens.BookmarksScreen
import com.example.ui.screens.CuetIcarScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.McqPracticeScreen
import com.example.ui.screens.MockTestScreen
import com.example.ui.screens.NoteDetailScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.PyqScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.StudyNotesScreen
import com.example.ui.state.KrishiPathViewModel

private data class TopLevelDestination(
    val route: String,
    val baseRouteMatch: String,
    val labelHi: String,
    val labelEn: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun KrishiPathApp(
    viewModel: KrishiPathViewModel = viewModel()
) {
    val navController = rememberNavController()
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val questions by viewModel.questions.collectAsStateWithLifecycle()
    val agricultureSubjects by viewModel.agricultureSubjects.collectAsStateWithLifecycle()
    val mockAttempts by viewModel.mockAttempts.collectAsStateWithLifecycle()
    val progress by viewModel.progressSummary.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val mockSession by viewModel.mockSession.collectAsStateWithLifecycle()

    val lang = profile.language

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Routes.HOME
    val isActiveMockTest = mockSession.blueprint != null && !mockSession.isSubmitted

    val topDestinations = listOf(
        TopLevelDestination(
            route = Routes.HOME,
            baseRouteMatch = Routes.HOME,
            labelHi = "होम",
            labelEn = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            testTag = "nav_home"
        ),
        TopLevelDestination(
            route = Routes.studyNotes(),
            baseRouteMatch = Routes.STUDY_NOTES,
            labelHi = "नोट्स",
            labelEn = "Notes",
            selectedIcon = Icons.AutoMirrored.Filled.MenuBook,
            unselectedIcon = Icons.AutoMirrored.Outlined.MenuBook,
            testTag = "nav_notes"
        ),
        TopLevelDestination(
            route = Routes.mcqPractice(),
            baseRouteMatch = Routes.MCQ_PRACTICE,
            labelHi = "अभ्यास",
            labelEn = "Practice",
            selectedIcon = Icons.Filled.Quiz,
            unselectedIcon = Icons.Outlined.Quiz,
            testTag = "nav_practice"
        ),
        TopLevelDestination(
            route = Routes.BOOKMARKS,
            baseRouteMatch = Routes.BOOKMARKS,
            labelHi = "बुकमार्क",
            labelEn = "Saved",
            selectedIcon = Icons.Filled.Bookmark,
            unselectedIcon = Icons.Outlined.BookmarkBorder,
            testTag = "nav_bookmarks"
        ),
        TopLevelDestination(
            route = Routes.PROFILE,
            baseRouteMatch = Routes.PROFILE,
            labelHi = "प्रोफ़ाइल",
            labelEn = "Profile",
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            testTag = "nav_profile"
        )
    )

    val navigateToTopLevel: (String) -> Unit = { target ->
        navController.navigate(target) {
            popUpTo(Routes.HOME) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useRail = maxWidth >= 600.dp && !isActiveMockTest

        Row(modifier = Modifier.fillMaxSize()) {
            if (useRail) {
                NavigationRail {
                    topDestinations.forEach { dest ->
                        val selected = currentRoute.startsWith(dest.baseRouteMatch)
                        NavigationRailItem(
                            selected = selected,
                            onClick = { navigateToTopLevel(dest.route) },
                            icon = {
                                Icon(
                                    imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                    contentDescription = tr(lang, dest.labelHi, dest.labelEn)
                                )
                            },
                            label = {
                                Text(
                                    text = tr(lang, dest.labelHi, dest.labelEn),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            modifier = Modifier.testTag(dest.testTag)
                        )
                    }
                }
            }

            Scaffold(
                bottomBar = {
                    if (!useRail && !isActiveMockTest) {
                        NavigationBar {
                            topDestinations.forEach { dest ->
                                val selected = currentRoute.startsWith(dest.baseRouteMatch)
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { navigateToTopLevel(dest.route) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                            contentDescription = tr(lang, dest.labelHi, dest.labelEn)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = tr(lang, dest.labelHi, dest.labelEn),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    modifier = Modifier.testTag(dest.testTag)
                                )
                            }
                        }
                    }
                }
            ) { outerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(outerPadding)
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Routes.HOME
                    ) {
                        composable(Routes.HOME) {
                            HomeScreen(
                                profile = profile,
                                progress = progress,
                                featuredNotes = notes,
                                onToggleLanguage = viewModel::toggleLanguage,
                                onNavigate = { route -> navController.navigate(route) },
                                onOpenNote = { noteId -> navController.navigate(Routes.noteDetail(noteId)) }
                            )
                        }

                        composable(Routes.AGRICULTURE_SUBJECTS) {
                            AgricultureSubjectsScreen(
                                subjects = agricultureSubjects,
                                language = lang,
                                onToggleLanguage = viewModel::toggleLanguage,
                                onBackClick = { navController.popBackStack() },
                                onSearchClick = { navController.navigate(Routes.SEARCH) },
                                onOpenSubjectNotes = { subjectId ->
                                    navController.navigate(
                                        Routes.studyNotes(
                                            category = SubjectCategory.AGRICULTURE.id,
                                            subjectId = subjectId
                                        )
                                    )
                                },
                                onOpenSubjectMcqs = { subjectId ->
                                    navController.navigate(
                                        Routes.mcqPractice(
                                            category = SubjectCategory.AGRICULTURE.id,
                                            subjectId = subjectId
                                        )
                                    )
                                }
                            )
                        }

                        composable(Routes.CUET_ICAR) {
                            CuetIcarScreen(
                                cuetNotes = notes.filter { it.category == SubjectCategory.CUET_ICAR },
                                language = lang,
                                onToggleLanguage = viewModel::toggleLanguage,
                                onBackClick = { navController.popBackStack() },
                                onSearchClick = { navController.navigate(Routes.SEARCH) },
                                onOpenNote = { noteId -> navController.navigate(Routes.noteDetail(noteId)) },
                                onOpenMockTests = { navController.navigate(Routes.MOCK_TESTS) },
                                onOpenPyqs = { navController.navigate(Routes.PYQ) }
                            )
                        }

                        composable(Routes.BIO_CHEM) {
                            BioChemScreen(
                                allNotes = notes,
                                language = lang,
                                onToggleLanguage = viewModel::toggleLanguage,
                                onBackClick = { navController.popBackStack() },
                                onSearchClick = { navController.navigate(Routes.SEARCH) },
                                onOpenNote = { noteId -> navController.navigate(Routes.noteDetail(noteId)) },
                                onOpenMcqCategory = { cat ->
                                    navController.navigate(Routes.mcqPractice(category = cat.id))
                                }
                            )
                        }

                        composable(
                            route = Routes.STUDY_NOTES_WITH_FILTER,
                            arguments = listOf(
                                navArgument("category") {
                                    type = NavType.StringType
                                    defaultValue = "all"
                                },
                                navArgument("subjectId") {
                                    type = NavType.StringType
                                    defaultValue = "all"
                                }
                            )
                        ) { backStackEntry ->
                            val category = backStackEntry.arguments?.getString("category") ?: "all"
                            val subjectId = backStackEntry.arguments?.getString("subjectId") ?: "all"
                            StudyNotesScreen(
                                notes = notes,
                                initialCategory = category,
                                initialSubjectId = subjectId,
                                language = lang,
                                onToggleLanguage = viewModel::toggleLanguage,
                                onBackClick = { navController.popBackStack() },
                                onOpenNote = { noteId -> navController.navigate(Routes.noteDetail(noteId)) },
                                onToggleBookmark = viewModel::toggleNoteBookmark,
                                onToggleOffline = viewModel::toggleNoteOffline
                            )
                        }

                        composable(
                            route = Routes.NOTE_DETAIL,
                            arguments = listOf(navArgument("noteId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
                            val note = notes.firstOrNull { it.id == noteId }
                            NoteDetailScreen(
                                note = note,
                                language = lang,
                                onToggleLanguage = viewModel::toggleLanguage,
                                onBackClick = { navController.popBackStack() },
                                onToggleBookmark = viewModel::toggleNoteBookmark,
                                onToggleOffline = viewModel::toggleNoteOffline,
                                onToggleCompleted = viewModel::toggleNoteCompleted
                            )
                        }

                        composable(
                            route = Routes.MCQ_PRACTICE_WITH_FILTER,
                            arguments = listOf(
                                navArgument("category") {
                                    type = NavType.StringType
                                    defaultValue = "all"
                                },
                                navArgument("subjectId") {
                                    type = NavType.StringType
                                    defaultValue = "all"
                                }
                            )
                        ) { backStackEntry ->
                            val category = backStackEntry.arguments?.getString("category") ?: "all"
                            val subjectId = backStackEntry.arguments?.getString("subjectId") ?: "all"
                            McqPracticeScreen(
                                questions = questions,
                                initialCategory = category,
                                initialSubjectId = subjectId,
                                language = lang,
                                onToggleLanguage = viewModel::toggleLanguage,
                                onBackClick = { navController.popBackStack() },
                                onSelectOption = viewModel::recordMcqAnswer,
                                onToggleBookmark = viewModel::toggleQuestionBookmark,
                                onToggleOffline = viewModel::toggleQuestionOffline
                            )
                        }

                        composable(Routes.MOCK_TESTS) {
                            MockTestScreen(
                                blueprints = viewModel.mockBlueprints,
                                attempts = mockAttempts,
                                session = mockSession,
                                language = lang,
                                onToggleLanguage = viewModel::toggleLanguage,
                                onBackClick = { navController.popBackStack() },
                                onStartTest = viewModel::startMockTest,
                                onTogglePause = viewModel::togglePauseMockTest,
                                onSelectAnswer = viewModel::selectMockAnswer,
                                onClearAnswer = viewModel::clearMockAnswer,
                                onToggleReview = viewModel::toggleMarkForReview,
                                onGoToQuestion = viewModel::goToMockQuestion,
                                onSubmitTest = viewModel::submitMockTest,
                                onExitSession = viewModel::exitMockSession
                            )
                        }

                        composable(Routes.PYQ) {
                            PyqScreen(
                                questions = questions,
                                language = lang,
                                onToggleLanguage = viewModel::toggleLanguage,
                                onBackClick = { navController.popBackStack() },
                                onSelectOption = viewModel::recordMcqAnswer,
                                onToggleBookmark = viewModel::toggleQuestionBookmark,
                                onToggleOffline = viewModel::toggleQuestionOffline
                            )
                        }

                        composable(Routes.BOOKMARKS) {
                            BookmarksScreen(
                                notes = notes,
                                questions = questions,
                                language = lang,
                                onToggleLanguage = viewModel::toggleLanguage,
                                onBackClick = { navController.popBackStack() },
                                onOpenNote = { noteId -> navController.navigate(Routes.noteDetail(noteId)) },
                                onToggleNoteBookmark = viewModel::toggleNoteBookmark,
                                onToggleNoteOffline = viewModel::toggleNoteOffline,
                                onSelectQuestionOption = viewModel::recordMcqAnswer,
                                onToggleQuestionBookmark = viewModel::toggleQuestionBookmark,
                                onToggleQuestionOffline = viewModel::toggleQuestionOffline
                            )
                        }

                        composable(Routes.PROFILE) {
                            ProfileScreen(
                                profile = profile,
                                progress = progress,
                                subjects = agricultureSubjects,
                                onToggleLanguage = viewModel::toggleLanguage,
                                onSetLanguage = viewModel::setLanguage,
                                onUpdateProfile = viewModel::updateProfile,
                                onResetProgress = viewModel::resetAllProgress,
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        composable(Routes.SEARCH) {
                            SearchScreen(
                                searchResults = searchResults,
                                language = lang,
                                onQueryChange = viewModel::setSearchQuery,
                                onToggleLanguage = viewModel::toggleLanguage,
                                onBackClick = { navController.popBackStack() },
                                onOpenNote = { noteId -> navController.navigate(Routes.noteDetail(noteId)) },
                                onSelectQuestionOption = viewModel::recordMcqAnswer,
                                onToggleQuestionBookmark = viewModel::toggleQuestionBookmark,
                                onToggleQuestionOffline = viewModel::toggleQuestionOffline
                            )
                        }
                    }
                }
            }
        }
    }
}
