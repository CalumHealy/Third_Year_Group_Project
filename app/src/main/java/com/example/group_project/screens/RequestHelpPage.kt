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
fun RequestHelpPage(modifier: Modifier = Modifier, navController: NavController) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            Text(text = "Request Help", fontSize = 24.sp)

            Button(onClick = { navController.navigate("chatbot_ai") }) {
                Text(text = "Talk to Chatbot AI")
            }

            Button(onClick = { navController.navigate("support_form") }) {
                Text(text = "Submit a Support Form")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Frequently Asked Questions", fontSize = 20.sp)

            Text(text = "Q: How do I reset my password?")
            Text(text = "A: Go to the Profile page and click 'Reset Password'.", fontSize = 14.sp)

            Text(text = "Q: How can I contact support?")
            Text(text = "A: Submit a support form or use the chatbot.", fontSize = 14.sp)

            Text(text = "Q: What is this app about?")
            Text(text = "A: This app helps users track stocks, cryptocurrencies, and provides support.", fontSize = 14.sp)

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = { navController.popBackStack() }) {
                Text(text = "Back to Menu")
            }
        }
    }
}
