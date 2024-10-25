package com.example.group_project.navigation.navigationBar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

data class NavItems (
    val label: String,
    val icon: ImageVector,
    val route: String
)

val listOfNavItems = listOf(
    NavItems (
        label = "Home",
        icon = Icons.Default.Home,
        route = Screens.HomePage.name
    ),
    NavItems (
        label = "Investments",
        icon = Icons.Default.Star,
        route = Screens.MyInvestmentsPage.name
    ),
    NavItems (
        label = "More",
        icon = Icons.AutoMirrored.Filled.List,
        route = Screens.MenuPage.name
    ),
    NavItems (
        label = "Profile",
        icon = Icons.Default.Person,
        route = Screens.ProfilePage.name
    ),
)