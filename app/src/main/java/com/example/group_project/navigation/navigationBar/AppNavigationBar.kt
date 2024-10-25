package com.example.group_project.navigation.navigationBar

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.group_project.AuthModel
import com.example.group_project.screens.HomePage
import com.example.group_project.screens.MenuPage
import com.example.group_project.screens.MyInvestmentsPage
import com.example.group_project.screens.ProfilePage
import java.lang.reflect.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*

@Composable
fun AppNavigationBar () {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                listOfNavItems.forEach { navItems ->
                    NavigationBarItem(
                        selected =currentDestination?.hierarchy?.any {it.route == navItems.route} == true,
                        onClick = {
                            navController.navigate(navItems.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = navItems.icon,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(text = navItems.label)
                        }
                    )

                }
            }
        }
    ){paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screens.HomePage.name,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(route = Screens.HomePage.name) {
                HomePage(navController, modifier)
            }
            composable(route = Screens.MyInvestmentsPage.name) {
                MyInvestmentsPage()
            }
            composable(route = Screens.MenuPage.name) {
                MenuPage()
            }
            composable(route = Screens.ProfilePage.name) {
                ProfilePage()
            }
        }

    }
}