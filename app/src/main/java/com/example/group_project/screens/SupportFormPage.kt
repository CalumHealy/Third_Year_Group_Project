package com.example.group_project.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SupportFormPage(modifier: Modifier = Modifier, navController: NavController) {
    // State variables to hold user input
    var supportMessage by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Firebase Firestore instance
    val db = FirebaseFirestore.getInstance()

    // Function to handle form submission
    fun submitSupportForm() {
        if (supportMessage.isNotEmpty()) {
            isSubmitting = true
            val supportData = hashMapOf(
                "message" to supportMessage,
                "timestamp" to System.currentTimeMillis()
            )

            // Add the support message to the Firestore collection
            db.collection("support_forms")
                .add(supportData)
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
