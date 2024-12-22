package com.example.group_project.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.group_project.AuthModel
import com.example.group_project.navigation.navigationBar.NavItems

@Composable
fun HomePage(modifier: Modifier = Modifier, navController: NavController, authModel: AuthModel) {
    val navItemsList = listOf(
        NavItems("Home", Icons.Default.Home),
        NavItems("Invest", Icons.Default.ShoppingCart),
        NavItems("Investments", Icons.Default.Star),
        NavItems("More", Icons.Default.Menu),
        NavItems("Profile", Icons.Default.Person)
    )

    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navItemsList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = {
                            Icon(imageVector = navItem.icon, contentDescription = "Icon")
                        },
                        label = {
                            Text(text = navItem.label)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        ContentScreen(
            modifier = Modifier.padding(innerPadding),
            selectedIndex = selectedIndex,
            navController = navController,
            authModel = authModel
        )
    }
}

@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    navController: NavController,
    authModel: AuthModel,
) {
    when (selectedIndex) {
        0 -> HomePageContent(navController = navController)
        1 -> InvestPage()
        2 -> MyInvestmentsPage(navController = navController)
        3 -> MenuPage(navController = navController)
        4 -> ProfilePage(navController = navController, authModel = authModel)
    }
}

