package com.example.group_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController // Import for NavController
import com.example.group_project.screens.HomePage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create an instance of AuthModel
        val authModel: AuthModel by viewModels()

        setContent {
            // Create NavController instance
            val navController = rememberNavController()

            // Pass both navController and authModel to HomePage
            HomePage(navController = navController, authModel = authModel)
        }
    }
}
