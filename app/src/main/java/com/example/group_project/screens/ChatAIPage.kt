package com.example.group_project.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.group_project.AuthModel
import com.example.group_project.ui.theme.Group_projectTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


@Composable
fun ChatbotAIPage(navController: NavController, authModel: AuthModel) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val firestore = Firebase.firestore
    var accountType by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Fetch user account type from Firestore
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

    if (accountType == "Supporter") {
        // WebView for Supporter users
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    webViewClient = WebViewClient()
                    loadUrl("https://50f1-212-129-83-234.ngrok-free.app")
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    } else {
        // Prompt Regular users to upgrade
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, top = 40.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Access Restricted", fontSize = 32.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Only Supporter accounts can access this page.", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                navController.navigate("profile")
            }) {
                Text(text = "Go to Profile to Upgrade")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ChatbotAIPreview() {
    Group_projectTheme {
        ChatbotAIPage(navController = NavController(LocalContext.current), authModel = AuthModel())
    }
}

