package com.example.group_project.screens

import android.content.Context
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.compose.ui.platform.LocalContext
import com.example.group_project.AuthState

@Composable
fun ProfilePage(modifier: Modifier = Modifier, navController: NavController, authModel: AuthModel) {
    val authState = authModel.authState.observeAsState()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val firestore = Firebase.firestore
    val context = LocalContext.current // Get the current context

    // States for managing user details and edit mode
    var fullName by remember { mutableStateOf(currentUser?.displayName ?: "") }
    var email by remember { mutableStateOf(currentUser?.email ?: "") }
    var address1 by remember { mutableStateOf("") }
    var address2 by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf("100.00") } // Default balance to $100
    var accountType by remember { mutableStateOf("Regular") } // Default account type
    var isEditing by remember { mutableStateOf(false) }

    LaunchedEffect(authState.value) {
        if (authState.value is AuthState.Unauthenticated) {
            navController.navigate("login") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            val docRef = firestore.collection("users").document(user.uid)
            docRef.addSnapshotListener { document, _ ->
                if (document != null && document.exists()) {
                    address1 = document.getString("address1") ?: ""
                    address2 = document.getString("address2") ?: ""
                    phoneNumber = document.getString("phoneNumber") ?: ""
                    balance = document.getDouble("balance")?.toString() ?: "100.00"
                    accountType = document.getString("accountType") ?: "Regular"
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

        // Display Balance and Account Type
        Text(text = "Balance: $balance", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Account Type: $accountType", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Edit Profile Button
        Button(onClick = {
            if (isEditing) {
                // Save changes to Firestore when editing is complete
                saveProfileChangesToFirestore(fullName, email, address1, address2, phoneNumber, context)
            }
            isEditing = !isEditing // Toggle edit mode
        }) {
            Text(text = if (isEditing) "Save Changes" else "Edit Profile")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Upgrade Account Button
        Button(onClick = {
            upgradeAccount(balance.toDouble(), context) // Pass context here
        }) {
            Text(text = "Upgrade Account")
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

// Function to save profile changes to Firestore with better error logging
private fun saveProfileChangesToFirestore(
    fullName: String,
    email: String,
    address1: String,
    address2: String,
    phoneNumber: String,
    context: Context  // Add context parameter
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val firestore = Firebase.firestore
    currentUser?.let { user ->
        // Update user profile details
        user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(fullName).build())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userData = hashMapOf(
                        "fullName" to fullName,
                        "email" to email,
                        "address1" to address1,
                        "address2" to address2,
                        "phoneNumber" to phoneNumber,
                        "balance" to 100.00, // Default balance or fetch from UI
                        "accountType" to "Regular" // Default account type
                    )

                    // Create or update the user document in Firestore
                    firestore.collection("users").document(user.uid)
                        .set(userData)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Profile updated!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Error updating profile: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context, "Failed to update profile in Firestore.", Toast.LENGTH_SHORT).show()
                }
            }
    } ?: run {
        Toast.makeText(context, "User is not authenticated.", Toast.LENGTH_SHORT).show()
    }
}

// Function to upgrade account if the user has enough balance
private fun upgradeAccount(currentBalance: Double, context: Context) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val firestore = Firebase.firestore

    if (currentUser != null) {
        val userRef = firestore.collection("users").document(currentUser.uid)

        // Check if the balance is enough to upgrade
        if (currentBalance >= 20.00) {
            val newBalance = currentBalance - 20.00
            userRef.update(
                "balance", newBalance,
                "accountType", "Supporter"
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Successfully updated balance and account type
                    Toast.makeText(context, "Account upgraded to Supporter!", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle failure
                    Toast.makeText(context, "Failed to upgrade account: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // Insufficient balance
            Toast.makeText(context, "Insufficient balance to upgrade", Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
    }
}
