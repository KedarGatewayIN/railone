package com.indianrailways.railone.ui

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.indianrailways.railone.data.repository.TicketRepository
import com.indianrailways.railone.ui.components.RailOneBottomNavBar
import com.indianrailways.railone.ui.components.RailOneLogo
import com.indianrailways.railone.ui.components.RailOneTrainOutlineLogo
import com.indianrailways.railone.ui.navigation.Screen
import com.indianrailways.railone.ui.screens.CoachPositionScreen
import com.indianrailways.railone.ui.screens.DigitalTicketScreen
import com.indianrailways.railone.ui.screens.FileRefundScreen
import com.indianrailways.railone.ui.screens.HomeScreen
import com.indianrailways.railone.ui.screens.LiveTrainStatusScreen
import com.indianrailways.railone.ui.screens.MenuScreen
import com.indianrailways.railone.ui.screens.MyBookingsScreen
import com.indianrailways.railone.ui.screens.NotificationScreen
import com.indianrailways.railone.ui.screens.OrderFoodScreen
import com.indianrailways.railone.ui.screens.PlatformBookingScreen
import com.indianrailways.railone.ui.screens.PnrStatusScreen
import com.indianrailways.railone.ui.screens.RailMadadScreen
import com.indianrailways.railone.ui.screens.ReservedBookingScreen
import com.indianrailways.railone.ui.screens.UnreservedBookingScreen
import com.indianrailways.railone.ui.screens.UnreservedJourneyScreen
import com.indianrailways.railone.ui.screens.YouProfileScreen
import com.indianrailways.railone.ui.theme.RailOneSurface
import kotlinx.coroutines.delay

@Composable
fun RailOneApp(ticketRepository: TicketRepository) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var showSplash by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(650)
        showSplash = false
    }

    val useDarkStatusIcons = showSplash ||
        currentRoute == Screen.Home.route ||
        currentRoute == Screen.UnreservedBooking.route

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = useDarkStatusIcons
            }
        }
    }

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.MyBookings.route,
        Screen.YouProfile.route,
        Screen.Menu.route
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    RailOneBottomNavBar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(Screen.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(RailOneSurface)
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            ticketRepository = ticketRepository,
                            onNavigate = { route -> navController.navigate(route) }
                        )
                    }

                    // Step 1: Unreserved E-Ticket
                    composable(Screen.UnreservedBooking.route) {
                        UnreservedBookingScreen(
                            ticketRepository = ticketRepository,
                            onBack = { navController.popBackStack() },
                            onProceedToJourney = { fromCode, toCode, bookingMode, locationMode ->
                                navController.navigate(
                                    Screen.UnreservedJourney.createRoute(
                                        fromCode = fromCode,
                                        toCode = toCode,
                                        bookingMode = bookingMode,
                                        locationMode = locationMode
                                    )
                                )
                            }
                        )
                    }

                    // Step 2: Unreserved Journey (Train Type, Pax, Fare & Book Now)
                    composable(
                        route = Screen.UnreservedJourney.route,
                        arguments = listOf(
                            navArgument("fromCode") { type = NavType.StringType },
                            navArgument("toCode") { type = NavType.StringType },
                            navArgument("bookingMode") { type = NavType.StringType },
                            navArgument("locationMode") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val fromCode = backStackEntry.arguments?.getString("fromCode") ?: "BH"
                        val toCode = backStackEntry.arguments?.getString("toCode") ?: "BRC"
                        val bookingMode = backStackEntry.arguments?.getString("bookingMode") ?: "Normal"
                        val locationMode = backStackEntry.arguments?.getString("locationMode") ?: "Outside Station"

                        UnreservedJourneyScreen(
                            fromCode = fromCode,
                            toCode = toCode,
                            bookingMode = bookingMode,
                            locationMode = locationMode,
                            ticketRepository = ticketRepository,
                            onBack = { navController.popBackStack() },
                            onTicketBooked = { utsNumber ->
                                navController.navigate(Screen.DigitalTicket.createRoute(utsNumber)) {
                                    popUpTo(Screen.Home.route)
                                }
                            }
                        )
                    }

                    // Digital Ticket Booking Details (Portrait)
                    composable(
                        route = Screen.DigitalTicket.route,
                        arguments = listOf(navArgument("utsNumber") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val uts = backStackEntry.arguments?.getString("utsNumber") ?: ""
                        DigitalTicketScreen(
                            utsNumber = uts,
                            ticketRepository = ticketRepository,
                            onBack = { navController.popBackStack() },
                            onBookConnectingJourney = {
                                navController.navigate(Screen.UnreservedBooking.route)
                            }
                        )
                    }

                    // My Bookings
                    composable(Screen.MyBookings.route) {
                        MyBookingsScreen(
                            ticketRepository = ticketRepository,
                            onBack = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                }
                            },
                            onViewUnreservedTicket = { utsNumber ->
                                navController.navigate(Screen.DigitalTicket.createRoute(utsNumber))
                            },
                            onBookAgainUnreserved = {
                                navController.navigate(Screen.UnreservedBooking.route)
                            },
                            onBookAgainReserved = {
                                navController.navigate(Screen.ReservedBooking.route)
                            }
                        )
                    }

                    // Reserved Train Search & Booking
                    composable(Screen.ReservedBooking.route) {
                        ReservedBookingScreen(
                            ticketRepository = ticketRepository,
                            onBack = { navController.popBackStack() },
                            onBooked = {
                                navController.navigate(Screen.MyBookings.route) {
                                    popUpTo(Screen.Home.route)
                                }
                            }
                        )
                    }

                    // Platform Ticket Booking
                    composable(Screen.PlatformBooking.route) {
                        PlatformBookingScreen(
                            ticketRepository = ticketRepository,
                            onBack = { navController.popBackStack() },
                            onSuccess = {
                                navController.navigate(Screen.MyBookings.route) {
                                    popUpTo(Screen.Home.route)
                                }
                            }
                        )
                    }

                    // Notifications Screen
                    composable(Screen.Notifications.route) {
                        NotificationScreen(
                            ticketRepository = ticketRepository,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.PnrStatus.route) {
                        PnrStatusScreen(onBack = { navController.popBackStack() })
                    }

                    composable(Screen.LiveTrainStatus.route) {
                        LiveTrainStatusScreen(onBack = { navController.popBackStack() })
                    }

                    composable(Screen.CoachPosition.route) {
                        CoachPositionScreen(onBack = { navController.popBackStack() })
                    }

                    composable(Screen.OrderFood.route) {
                        OrderFoodScreen(onBack = { navController.popBackStack() })
                    }

                    composable(Screen.RailMadad.route) {
                        RailMadadScreen(onBack = { navController.popBackStack() })
                    }

                    composable(Screen.FileRefund.route) {
                        FileRefundScreen(onBack = { navController.popBackStack() })
                    }

                    composable(Screen.YouProfile.route) {
                        YouProfileScreen(onBack = { navController.popBackStack() })
                    }

                    composable(Screen.Menu.route) {
                        MenuScreen(onNavigate = { route -> navController.navigate(route) })
                    }
                }
            }
        }

        // Authentic RailOne Launch Splash Screen matching Video (00:04 - 00:08)
        AnimatedVisibility(
            visible = showSplash,
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFAF9F7)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                RailOneTrainOutlineLogo(modifier = Modifier.size(150.dp))
                Spacer(modifier = Modifier.height(6.dp))
                RailOneLogo(fontSizeSp = 34f)
            }
        }
    }
}
