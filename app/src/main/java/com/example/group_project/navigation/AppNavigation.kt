package com.example.group_project.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.group_project.AuthModel
import com.example.group_project.screens.HomePage
import com.example.group_project.screens.LoginPage
import com.example.group_project.screens.SignUpPage

@Composable
fun AppNavigation (modifier: Modifier = Modifier, authModel : AuthModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login") {
            LoginPage(modifier, navController, authModel)
        }
        composable("signup") {
            SignUpPage(modifier, navController, authModel)
        }
        composable("home") {
            HomePage(modifier, navController, authModel)
        }
    })

}