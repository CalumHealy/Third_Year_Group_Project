package com.example.group_project.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun SupportFormPage(modifier: Modifier = Modifier, navController: NavController) {
    // State variables to hold user input
    var supportMessage by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Firebase instances
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseDatabase.getInstance().getReference("queries")

    // Function to handle form submission
    fun submitSupportForm() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userEmail = currentUser.email ?: "Unknown"

            if (supportMessage.isNotEmpty()) {
                isSubmitting = true
                val supportData = mapOf(
                    "userId" to userId,
                    "email" to userEmail,
                    "message" to supportMessage,
                    "timestamp" to System.currentTimeMillis()
                )

                // Add the support message to the Realtime Database
                db.push()
                    .setValue(supportData)
                    .addOnSuccessListener {
                        supportMessage = ""
                        isSubmitting = false
                        errorMessage = "Support request submitted successfully!"
                    }
                    .addOnFailureListener {
                        isSubmitting = false
                        errorMessage = "Error submitting your request. Please try again."
                    }
            } else {
                errorMessage = "Please enter a message before submitting."
            }
        } else {
            errorMessage = "You must be logged in to submit a support request."
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Main content of the Support Form
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            Text(text = "Support Form", fontSize = 24.sp, color = Color.Black)

            // Support message input field
            TextField(
                value = supportMessage,
                onValueChange = { supportMessage = it },
                label = { Text(text = "Enter your message") },
                modifier = Modifier.fillMaxWidth().height(200.dp),
                maxLines = 5
            )

            // Error message if any
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red)
            }

            // Submit button
            Button(
                onClick = { submitSupportForm() },
                enabled = !isSubmitting
            ) {
                Text(text = if (isSubmitting) "Submitting..." else "Submit Request")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Display confirmation message after successful submission
            if (errorMessage == "Support request submitted successfully!") {
                Text(text = "Your request has been submitted successfully!", color = Color.Green)
            }

            // Back Button
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { navController.navigate("menu") { popUpTo("menu") { inclusive = true } } }
            ) {
                Text(text = "Back to Menu")
            }
        }
    }
}
