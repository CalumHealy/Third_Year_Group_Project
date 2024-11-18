package com.example.group_project.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.group_project.AuthModel
import com.example.group_project.screens.HomePage
import com.example.group_project.screens.LoginPage
import com.example.group_project.screens.SignUpPage
import com.example.group_project.screens.ProfilePage
import com.example.group_project.screens.CryptoDetailPage
import com.example.group_project.screens.StockDetailPage

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    authModel: AuthModel
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
            HomePage(modifier = modifier.fillMaxSize(), navController = navController, authModel = authModel)
        }
        composable("profile") {
            ProfilePage(modifier = modifier.fillMaxSize(), navController = navController, authModel = authModel)
        }

        // Add the Crypto Detail destination and pass navController
        composable(
            "cryptoDetail/{cryptoId}",
            arguments = listOf(navArgument("cryptoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val cryptoId = backStackEntry.arguments?.getString("cryptoId")
            cryptoId?.let {
                CryptoDetailPage(cryptoId = it, navController = navController) // Pass navController here
            }
        }

        // Add the Stock Detail destination and pass the correct ticker argument
        composable(
            "stockDetail/{ticker}",
            arguments = listOf(navArgument("ticker") { type = NavType.StringType })
        ) { backStackEntry ->
            val ticker = backStackEntry.arguments?.getString("ticker")
            ticker?.let {
                StockDetailPage(stockSymbol = it, navController = navController) // Pass correct argument here
            }
        }
    }
}

