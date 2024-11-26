package com.example.group_project.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.group_project.AuthModel
import com.example.group_project.AuthState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfilePage(modifier: Modifier = Modifier, navController: NavController, authModel: AuthModel) {
    val authState = authModel.authState.observeAsState()
    val currentUser = FirebaseAuth.getInstance().currentUser

    // States for managing name and edit mode
    var fullName by remember { mutableStateOf(currentUser?.displayName ?: "") }
    var isEditingName by remember { mutableStateOf(false) }
    val creationDate = currentUser?.metadata?.creationTimestamp?.let {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it))
    }

    LaunchedEffect(authState.value) {
        if (authState.value is AuthState.Unauthenticated) {
            navController.navigate("login") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Profile Page", fontSize = 32.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Full Name Display or Edit
        if (isEditingName) {
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                // Save the updated name
                currentUser?.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setDisplayName(fullName)
                        .build()
                )?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        isEditingName = false // Exit edit mode
                    } else {
                        println("Failed to update profile: ${task.exception?.message}")
                    }
                }
            }) {
                Text("Save Changes")
            }
        } else {
            Text(text = "Full Name: ${fullName.ifBlank { "No Name" }}", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { isEditingName = true }) {
                Text("Edit Name")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Email and UID Information
        currentUser?.let { user ->
            Text(text = "Email: ${user.email ?: "No Email"}", fontSize = 20.sp)
            Text(text = "UID: ${user.uid}", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Account Creation Date
        creationDate?.let {
            Text(text = "Account Created: $it", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sign Out Button
        Button(onClick = {
            authModel.signOut()
        }) {
            Text(text = "Sign out")
        }
    }
}
