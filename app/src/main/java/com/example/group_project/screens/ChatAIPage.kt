package com.example.group_project.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.group_project.AuthModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun ChatbotAIPage(navController: NavController, authModel: AuthModel) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val firestore = Firebase.firestore
    var accountType by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Fetch user account type from Firestore when the page is composed
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            val docRef = firestore.collection("users").document(currentUser.uid)
            docRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    accountType = document.getString("accountType") ?: "Regular"
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Error fetching user data.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "User not authenticated.", Toast.LENGTH_SHORT).show()
        }
    }

    // Conditionally display content based on account type
    if (accountType == "Supporter") {
        // Show AI page content for Supporter
        var messageText by remember { mutableStateOf("") }
        val messages = remember { mutableStateListOf<String>() } // List to hold chat messages

        // Add initial chatbot greeting message when the page is opened
        LaunchedEffect(Unit) {
            messages.add("AI: Hello, how can I assist you?")
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 40.dp),  // Added padding to the top of the screen
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header at the top of the page
            Text(
                text = "AI Chatbot",
                fontSize = 32.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Chat display area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 8.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                // Displaying chat messages
                LazyColumn {
                    items(messages) { message ->
                        Text(
                            text = message,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            // Message input area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Input field for typing a message
                BasicTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                        .padding(16.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            // When the user presses send, add the message to the list
                            if (messageText.isNotEmpty()) {
                                messages.add("You: $messageText") // Add user message to list
                                messageText = "" // Clear the input field
                                // TODO: You can add AI response here later
                            }
                        }
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Send button
                Button(onClick = {
                    if (messageText.isNotEmpty()) {
                        messages.add("You: $messageText") // Add user message to list
                        messageText = "" // Clear the input field
                        // TODO: You can add AI response here later
                    }
                }) {
                    Text(text = "Send")
                }
            }
        }
    } else {
        // Show upgrade prompt for Regular user
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 40.dp),  
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Access Restricted", fontSize = 32.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Only Supporter accounts can access this page.", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                // Navigate to the Profile Page
                navController.navigate("profile")
            }) {
                Text(text = "Go to Profile to Upgrade")
            }
        }
    }
}
