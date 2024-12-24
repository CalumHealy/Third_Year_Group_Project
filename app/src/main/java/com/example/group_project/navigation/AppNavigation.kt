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
import com.example.group_project.screens.*

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
        composable("menu") {
            MenuPage(modifier = modifier.fillMaxSize(), navController = navController)
        }
        composable("add_funds") {
            AddFundsPage(
                onPaymentSuccess = { /* Handle payment success */ }
            )
        }
        composable("ratings") {
            RatingsReviewPage(modifier = modifier.fillMaxSize())
        }
        composable("support_form") {
            SupportFormPage(modifier = modifier.fillMaxSize(), navController = navController)
        }
        composable("request_help") {
            RequestHelpPage(modifier = modifier.fillMaxSize(), navController = navController)
        }
        composable("chatbot_ai") {
            ChatbotAIPage(modifier = modifier.fillMaxSize(), navController = navController)
        }
        composable(
            route = "investment_details/{name}/{symbol}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("symbol") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val symbol = backStackEntry.arguments?.getString("symbol") ?: ""
            InvestmentDetailsPage(name = name, symbol = symbol)
        }
        composable("invest") {
            InvestPage()
        }
        composable("investments") {
            MyInvestmentsPage(navController = navController)
        }
    }
}
