package com.indianrailways.railone.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.indianrailways.railone.data.repository.TicketRepository
import com.indianrailways.railone.ui.components.RailOneBottomNavBar
import com.indianrailways.railone.ui.navigation.Screen
import com.indianrailways.railone.ui.screens.CoachPositionScreen
import com.indianrailways.railone.ui.screens.DigitalTicketScreen
import com.indianrailways.railone.ui.screens.FileRefundScreen
import com.indianrailways.railone.ui.screens.HomeScreen
import com.indianrailways.railone.ui.screens.LiveTrainStatusScreen
import com.indianrailways.railone.ui.screens.MenuScreen
import com.indianrailways.railone.ui.screens.MyBookingsScreen
import com.indianrailways.railone.ui.screens.OrderFoodScreen
import com.indianrailways.railone.ui.screens.PlatformBookingScreen
import com.indianrailways.railone.ui.screens.PnrStatusScreen
import com.indianrailways.railone.ui.screens.RailMadadScreen
import com.indianrailways.railone.ui.screens.UnreservedBookingScreen
import com.indianrailways.railone.ui.screens.YouProfileScreen
import com.indianrailways.railone.ui.theme.RailOneSurface

@Composable
fun RailOneApp(ticketRepository: TicketRepository) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Top-level destinations that show the bottom navigation bar
    // Hide bottom bar on digital ticket screen (it goes landscape)
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.MyBookings.route,
        Screen.YouProfile.route,
        Screen.Menu.route
    ) && currentRoute != Screen.DigitalTicket.route

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
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.fillMaxSize()
            ) {
                // 1. Home Screen
                composable(Screen.Home.route) {
                    HomeScreen(
                        ticketRepository = ticketRepository,
                        onNavigate = { route ->
                            navController.navigate(route)
                        }
                    )
                }

                // 2. Unreserved Ticket Booking Flow
                composable(Screen.UnreservedBooking.route) {
                    UnreservedBookingScreen(
                        ticketRepository = ticketRepository,
                        onBack = { navController.popBackStack() },
                        onTicketBooked = { utsNumber ->
                            navController.navigate(Screen.DigitalTicket.createRoute(utsNumber)) {
                                popUpTo(Screen.Home.route)
                            }
                        }
                    )
                }

                // 3. Digital Ticket View
                composable(
                    route = Screen.DigitalTicket.route,
                    arguments = listOf(navArgument("utsNumber") { type = NavType.StringType })
                ) { backStackEntry ->
                    val uts = backStackEntry.arguments?.getString("utsNumber") ?: ""
                    DigitalTicketScreen(
                        utsNumber = uts,
                        ticketRepository = ticketRepository,
                        onBack = { navController.popBackStack() },
                        onGoToMyBookings = {
                            navController.navigate(Screen.MyBookings.route) {
                                popUpTo(Screen.Home.route)
                            }
                        }
                    )
                }

                // 4. My Bookings Screen
                composable(Screen.MyBookings.route) {
                    MyBookingsScreen(
                        ticketRepository = ticketRepository,
                        onBack = { navController.navigate(Screen.Home.route) },
                        onViewUnreservedTicket = { utsNumber ->
                            navController.navigate(Screen.DigitalTicket.createRoute(utsNumber))
                        },
                        onBookAgain = {
                            navController.navigate(Screen.UnreservedBooking.route)
                        }
                    )
                }

                // 5. Platform Ticket Booking
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

                // 6. PNR Status
                composable(Screen.PnrStatus.route) {
                    PnrStatusScreen(onBack = { navController.popBackStack() })
                }

                // 7. Live Train Status
                composable(Screen.LiveTrainStatus.route) {
                    LiveTrainStatusScreen(onBack = { navController.popBackStack() })
                }

                // 8. Coach Position
                composable(Screen.CoachPosition.route) {
                    CoachPositionScreen(onBack = { navController.popBackStack() })
                }

                // 9. Order Food
                composable(Screen.OrderFood.route) {
                    OrderFoodScreen(onBack = { navController.popBackStack() })
                }

                // 10. Rail Madad
                composable(Screen.RailMadad.route) {
                    RailMadadScreen(onBack = { navController.popBackStack() })
                }

                // 11. File Refund
                composable(Screen.FileRefund.route) {
                    FileRefundScreen(onBack = { navController.popBackStack() })
                }

                // 12. You Profile
                composable(Screen.YouProfile.route) {
                    YouProfileScreen(onBack = { navController.popBackStack() })
                }

                // 13. Menu
                composable(Screen.Menu.route) {
                    MenuScreen(onNavigate = { route -> navController.navigate(route) })
                }
            }
        }
    }
}
