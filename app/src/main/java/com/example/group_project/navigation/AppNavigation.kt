package com.example.group_project.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.group_project.AuthModel
import com.example.group_project.screens.HomePage
import com.example.group_project.screens.LoginPage
import com.example.group_project.screens.SignUpPage
import com.example.group_project.wallet.WalletViewModel
import com.example.group_project.screens.ProfilePage

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    authModel: AuthModel,
    walletViewModel: WalletViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginPage(modifier = modifier.fillMaxSize(), navController = navController, authModel = authModel)
        }
        composable("signup") {
            SignUpPage(modifier = modifier.fillMaxSize(), navController = navController, authModel = authModel)
        }
        composable("home") {
            HomePage(modifier = modifier.fillMaxSize(), navController = navController, authModel = authModel) // Pass the walletViewModel to HomePage
        }
        composable("profile") {
            ProfilePage(modifier = modifier.fillMaxSize(), navController = navController, authModel = authModel)
        }

    }
}
