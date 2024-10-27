package com.example.group_project.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.group_project.AuthModel
import com.example.group_project.AuthState
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfilePage(modifier: Modifier = Modifier, navController: NavController, authModel: AuthModel) {
    val authState = authModel.authState.observeAsState()
    val currentUser = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(authState.value) {
        if (authState.value is AuthState.Unauthenticated) {
            navController.navigate("login") {
                // Clear the back stack to avoid returning to profile after login
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

        // Display user information only if the user is logged in
        currentUser?.let { user ->
            Text(text = "Email: ${user.email ?: "No Email"}", fontSize = 20.sp)
            Text(text = "UID: ${user.uid}", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pay for Premium Button
        Button(onClick = {
            navController.navigate("paypal_checkout")
        }) {
            Text(text = "Pay for Premium")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            authModel.signOut()
        }) {
            Text(text = "Sign out")
        }
    }
}
