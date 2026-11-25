package com.indianrailways.railone.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.indianrailways.railone.ui.navigation.Screen
import com.indianrailways.railone.ui.theme.RailOneBlue

data class NavItem(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun RailOneBottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        NavItem(
            title = "Home",
            route = Screen.Home.route,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        NavItem(
            title = "My Bookings",
            route = Screen.MyBookings.route,
            selectedIcon = Icons.Filled.ConfirmationNumber,
            unselectedIcon = Icons.Outlined.ConfirmationNumber
        ),
        NavItem(
            title = "You",
            route = Screen.YouProfile.route,
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person
        ),
        NavItem(
            title = "Menu",
            route = Screen.Menu.route,
            selectedIcon = Icons.Filled.Menu,
            unselectedIcon = Icons.Outlined.Menu
        )
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(RailOneBlue)
            .navigationBarsPadding()
            .height(62.dp)
            .padding(horizontal = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            val contentColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.72f)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (currentRoute != item.route) {
                            onNavigate(item.route)
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                    contentDescription = item.title,
                    tint = contentColor,
                    modifier = Modifier.size(23.dp)
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = item.title,
                    color = contentColor,
                    fontSize = 11.5.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}
