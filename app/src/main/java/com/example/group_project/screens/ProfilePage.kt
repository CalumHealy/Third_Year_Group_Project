package com.example.group_project.screens

import android.widget.Toast
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfilePage(modifier: Modifier = Modifier, navController: NavController, authModel: AuthModel) {
    val authState = authModel.authState.observeAsState()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val firestore = Firebase.firestore

    // States for managing user details and edit mode
    var fullName by remember { mutableStateOf(currentUser?.displayName ?: "") }
    var email by remember { mutableStateOf(currentUser?.email ?: "") }
    var address1 by remember { mutableStateOf("") }
    var address2 by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf("0.00") } // Assuming balance is fetched from Firestore or your server

    var isEditing by remember { mutableStateOf(false) }

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

    // Fetch address, phone number, and balance from Firestore if they exist
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            val docRef = firestore.collection("users").document(user.uid)
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    address1 = document.getString("address1") ?: ""
                    address2 = document.getString("address2") ?: ""
                    phoneNumber = document.getString("phoneNumber") ?: ""
                    balance = document.getString("balance") ?: "0.00" // Assuming balance is stored here
                }
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
        if (isEditing) {
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        } else {
            Text(text = "Full Name: ${fullName.ifBlank { "No Name" }}", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Email Display or Edit
        if (isEditing) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        } else {
            Text(text = "Email: ${email.ifBlank { "No Email" }}", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Address 1 Display or Edit
        if (isEditing) {
            OutlinedTextField(
                value = address1,
                onValueChange = { address1 = it },
                label = { Text("Address 1") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        } else {
            Text(text = "Address 1: ${address1.ifBlank { "Not Provided" }}", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Address 2 Display or Edit
        if (isEditing) {
            OutlinedTextField(
                value = address2,
                onValueChange = { address2 = it },
                label = { Text("Address 2") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        } else {
            Text(text = "Address 2: ${address2.ifBlank { "Not Provided" }}", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Phone Number Display or Edit
        if (isEditing) {
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        } else {
            Text(text = "Phone Number: ${phoneNumber.ifBlank { "Not Provided" }}", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Display UID and Balance
        currentUser?.let { user ->
            Text(text = "UID: ${user.uid}", fontSize = 20.sp)
        }
        Text(text = "Balance: $balance", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Edit Profile Button
        Button(onClick = {
            if (isEditing) {
                // Save changes to Firestore when editing is complete
                saveProfileChangesToFirestore(fullName, email, address1, address2, phoneNumber)
            }
            isEditing = !isEditing // Toggle edit mode
        }) {
            Text(text = if (isEditing) "Save Changes" else "Edit Profile")
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

private fun saveProfileChangesToFirestore(
    fullName: String,
    email: String,
    address1: String,
    address2: String,
    phoneNumber: String
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val firestore = Firebase.firestore
    currentUser?.let { user ->
        // Update user profile details
        user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(fullName).build())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firestore.collection("users").document(user.uid).apply {
                        update("address1", address1)
                        update("address2", address2)
                        update("phoneNumber", phoneNumber)
                    }
                }
            }
    }
}
