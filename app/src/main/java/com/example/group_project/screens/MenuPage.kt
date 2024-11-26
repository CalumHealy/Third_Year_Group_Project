package com.example.group_project.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun MenuPage(modifier: Modifier = Modifier,navController: NavController ) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Menu Screen",
                fontSize = 22.sp
            )
            // Button to navigate to Request Help Page
            Button(onClick = { navController.navigate("request_help") }) {
                Text(text = "Request Help")
            }
            // Button to navigate to Support Form Page
            Button(onClick = { navController.navigate("support_form") }) {
                Text(text = "Support Form")
            }
            // Button to navigate to Ratings Page
            Button(onClick = { navController.navigate("ratings") }) {
                Text(text = "Ratings/Review")
            }
        }
    }
}
