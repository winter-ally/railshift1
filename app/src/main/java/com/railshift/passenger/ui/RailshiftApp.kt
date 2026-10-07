package com.railshift.passenger.ui

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.railshift.passenger.data.models.Language
import com.railshift.passenger.data.models.ThemeMode
import com.railshift.passenger.data.preferences.PreferencesRepository
import com.railshift.passenger.data.repository.FakeRailshiftRepository
import com.railshift.passenger.navigation.RailshiftRoutes
import com.railshift.passenger.ui.components.LanguagePickerBottomSheet
import com.railshift.passenger.ui.components.RailshiftBottomBar
import com.railshift.passenger.ui.screens.bookings.BookingsViewModel
import com.railshift.passenger.ui.screens.bookings.MyBookingsScreen
import com.railshift.passenger.ui.screens.help.HelpScreen
import com.railshift.passenger.ui.screens.help.HelpViewModel
import com.railshift.passenger.ui.screens.home.HomeScreen
import com.railshift.passenger.ui.screens.home.HomeViewModel
import com.railshift.passenger.ui.screens.platform.PlatformTicketScreen
import com.railshift.passenger.ui.screens.platform.PlatformViewModel
import com.railshift.passenger.ui.screens.profile.EditProfileScreen
import com.railshift.passenger.ui.screens.profile.ProfileScreen
import com.railshift.passenger.ui.screens.profile.ProfileViewModel
import com.railshift.passenger.ui.screens.reserved.ReservedSearchResultsScreen
import com.railshift.passenger.ui.screens.reserved.ReservedTicketScreen
import com.railshift.passenger.ui.screens.reserved.ReservedViewModel
import com.railshift.passenger.ui.screens.scanner.QrScannerScreen
import com.railshift.passenger.ui.screens.services.CoachPositionScreen
import com.railshift.passenger.ui.screens.services.PnrStatusScreen
import com.railshift.passenger.ui.screens.services.SearchTrainsScreen
import com.railshift.passenger.ui.screens.services.TrackTrainScreen
import com.railshift.passenger.ui.screens.unreserved.UnreservedTicketScreen
import com.railshift.passenger.ui.screens.unreserved.UnreservedViewModel
import com.railshift.passenger.ui.screens.upgrade.UpgradeTicketScreen
import com.railshift.passenger.ui.screens.upgrade.UpgradeViewModel
import com.railshift.passenger.ui.theme.RailshiftTheme
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun RailshiftApp() {
    val context = LocalContext.current
    val repository = remember { FakeRailshiftRepository() }
    val preferencesRepository = remember { PreferencesRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    val themeMode by preferencesRepository.themeModeFlow.collectAsState(initial = ThemeMode.SYSTEM)
    val currentLanguage by preferencesRepository.languageFlow.collectAsState(initial = Language.ENGLISH)

    val isDark = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    var showLanguagePicker by remember { mutableStateOf(false) }

    // Apply locale to configuration
    val locale = remember(currentLanguage) { Locale(currentLanguage.code) }
    val configuration = LocalConfiguration.current
    val localizedConfiguration = remember(configuration, locale) {
        Configuration(configuration).apply {
            setLocale(locale)
        }
    }

    CompositionLocalProvider(LocalConfiguration provides localizedConfiguration) {
        RailshiftTheme(
            darkTheme = isDark,
            onToggleTheme = {
                coroutineScope.launch {
                    val nextMode = if (isDark) ThemeMode.LIGHT else ThemeMode.DARK
                    preferencesRepository.setThemeMode(nextMode)
                }
            }
        ) {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val snackbarHostState = remember { SnackbarHostState() }

            val homeViewModel = remember { HomeViewModel(repository) }
            val reservedViewModel = remember { ReservedViewModel(repository) }
            val unreservedViewModel = remember { UnreservedViewModel(repository) }
            val upgradeViewModel = remember { UpgradeViewModel(repository) }
            val platformViewModel = remember { PlatformViewModel(repository) }
            val bookingsViewModel = remember { BookingsViewModel(repository) }
            val helpViewModel = remember { HelpViewModel() }
            val profileViewModel = remember { ProfileViewModel(repository, preferencesRepository) }

            val showBottomBar = RailshiftRoutes.shouldShowBottomBar(currentRoute)

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    if (showBottomBar) {
                        RailshiftBottomBar(
                            currentRoute = currentRoute.orEmpty(),
                            onTabSelected = { route ->
                                navController.navigate(route) {
                                    popUpTo(RailshiftRoutes.HOME) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            onScanClick = {
                                navController.navigate(RailshiftRoutes.SCAN)
                            }
                        )
                    }
                },
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    NavHost(
                        navController = navController,
                        startDestination = RailshiftRoutes.HOME
                    ) {
                        // 1. Home
                        composable(RailshiftRoutes.HOME) {
                            HomeScreen(
                                viewModel = homeViewModel,
                                onNavigateToReserved = { navController.navigate(RailshiftRoutes.RESERVED) },
                                onNavigateToUnreserved = { navController.navigate(RailshiftRoutes.UNRESERVED) },
                                onNavigateToPlatform = { navController.navigate(RailshiftRoutes.PLATFORM) },
                                onNavigateToUpgrade = { navController.navigate(RailshiftRoutes.UPGRADE) },
                                onNavigateToSearchTrains = { navController.navigate(RailshiftRoutes.SEARCH_TRAINS) },
                                onNavigateToPnrStatus = { navController.navigate(RailshiftRoutes.PNR_STATUS) },
                                onNavigateToCoachPosition = { navController.navigate(RailshiftRoutes.COACH_POSITION) },
                                onNavigateToTrackTrain = { navController.navigate(RailshiftRoutes.TRACK_TRAIN) },
                                onNavigateToBookings = { navController.navigate(RailshiftRoutes.MY_BOOKINGS) },
                                onOpenLanguagePicker = { showLanguagePicker = true }
                            )
                        }

                        // 2. Reserved
                        composable(RailshiftRoutes.RESERVED) {
                            ReservedTicketScreen(
                                viewModel = reservedViewModel,
                                onBackClick = { navController.popBackStack() },
                                onSearchTrains = { navController.navigate(RailshiftRoutes.RESERVED_RESULTS) },
                                onOpenLanguagePicker = { showLanguagePicker = true }
                            )
                        }

                        composable(RailshiftRoutes.RESERVED_RESULTS) {
                            ReservedSearchResultsScreen(
                                viewModel = reservedViewModel,
                                onBackClick = { navController.popBackStack() },
                                onOpenLanguagePicker = { showLanguagePicker = true },
                                onBookingSuccess = {
                                    navController.navigate(RailshiftRoutes.MY_BOOKINGS) {
                                        popUpTo(RailshiftRoutes.HOME)
                                    }
                                }
                            )
                        }

                        // 3. Unreserved
                        composable(RailshiftRoutes.UNRESERVED) {
                            UnreservedTicketScreen(
                                viewModel = unreservedViewModel,
                                onBackClick = { navController.popBackStack() },
                                onOpenLanguagePicker = { showLanguagePicker = true },
                                onBookingSuccess = {
                                    navController.navigate(RailshiftRoutes.MY_BOOKINGS) {
                                        popUpTo(RailshiftRoutes.HOME)
                                    }
                                }
                            )
                        }

                        // 4. Upgrade
                        composable(RailshiftRoutes.UPGRADE) {
                            UpgradeTicketScreen(
                                viewModel = upgradeViewModel,
                                onBackClick = { navController.popBackStack() },
                                onOpenLanguagePicker = { showLanguagePicker = true },
                                onUpgradeSuccess = {
                                    navController.navigate(RailshiftRoutes.MY_BOOKINGS) {
                                        popUpTo(RailshiftRoutes.HOME)
                                    }
                                }
                            )
                        }

                        // 5. Platform
                        composable(RailshiftRoutes.PLATFORM) {
                            PlatformTicketScreen(
                                viewModel = platformViewModel,
                                onBackClick = { navController.popBackStack() },
                                onOpenLanguagePicker = { showLanguagePicker = true },
                                onBookingSuccess = {
                                    navController.navigate(RailshiftRoutes.HOME)
                                }
                            )
                        }

                        // 6. My Bookings
                        composable(RailshiftRoutes.MY_BOOKINGS) {
                            MyBookingsScreen(
                                viewModel = bookingsViewModel,
                                onOpenLanguagePicker = { showLanguagePicker = true }
                            )
                        }

                        // 7. Help
                        composable(RailshiftRoutes.HELP) {
                            HelpScreen(
                                viewModel = helpViewModel,
                                onOpenLanguagePicker = { showLanguagePicker = true }
                            )
                        }

                        // 8. Profile
                        composable(RailshiftRoutes.PROFILE) {
                            ProfileScreen(
                                viewModel = profileViewModel,
                                onNavigateToEditProfile = { navController.navigate(RailshiftRoutes.EDIT_PROFILE) },
                                onNavigateToBookings = { navController.navigate(RailshiftRoutes.MY_BOOKINGS) },
                                onOpenLanguagePicker = { showLanguagePicker = true }
                            )
                        }

                        // 9. Edit Profile
                        composable(RailshiftRoutes.EDIT_PROFILE) {
                            EditProfileScreen(
                                viewModel = profileViewModel,
                                onBackClick = { navController.popBackStack() },
                                onOpenLanguagePicker = { showLanguagePicker = true }
                            )
                        }

                        // 10. QR Scanner
                        composable(RailshiftRoutes.SCAN) {
                            QrScannerScreen(
                                onBackClick = { navController.popBackStack() },
                                onCodeScanned = { code ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Scanned station/ticket: $code")
                                    }
                                    if (code.length == 8 || code.contains("UTS")) {
                                        upgradeViewModel.onScanSuccess(code)
                                        navController.navigate(RailshiftRoutes.UPGRADE) {
                                            popUpTo(RailshiftRoutes.HOME)
                                        }
                                    } else {
                                        navController.navigate(RailshiftRoutes.PLATFORM) {
                                            popUpTo(RailshiftRoutes.HOME)
                                        }
                                    }
                                }
                            )
                        }

                        // More Services Sub Screens
                        composable(RailshiftRoutes.SEARCH_TRAINS) {
                            SearchTrainsScreen(
                                onBackClick = { navController.popBackStack() },
                                onOpenLanguagePicker = { showLanguagePicker = true }
                            )
                        }

                        composable(RailshiftRoutes.PNR_STATUS) {
                            PnrStatusScreen(
                                repository = repository,
                                onBackClick = { navController.popBackStack() },
                                onOpenLanguagePicker = { showLanguagePicker = true }
                            )
                        }

                        composable(RailshiftRoutes.COACH_POSITION) {
                            CoachPositionScreen(
                                onBackClick = { navController.popBackStack() },
                                onOpenLanguagePicker = { showLanguagePicker = true }
                            )
                        }

                        composable(RailshiftRoutes.TRACK_TRAIN) {
                            TrackTrainScreen(
                                onBackClick = { navController.popBackStack() },
                                onOpenLanguagePicker = { showLanguagePicker = true }
                            )
                        }
                    }
                }
            }

            if (showLanguagePicker) {
                LanguagePickerBottomSheet(
                    currentLanguage = currentLanguage,
                    onLanguageSelected = { selected ->
                        coroutineScope.launch {
                            preferencesRepository.setLanguage(selected)
                        }
                    },
                    onDismissRequest = { showLanguagePicker = false }
                )
            }
        }
    }
}
