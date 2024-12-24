package com.example.group_project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.group_project.navigation.AppNavigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.stripe.android.PaymentConfiguration

class MainActivity : ComponentActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Initialize the launcher for Google Sign-In
        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        }

        val authModel: AuthModel by viewModels()
        setContent {
            AppNavigation(
                authModel = authModel
            )
        }

        // Initialize Stripe
        PaymentConfiguration.init(
            applicationContext,
            "pk_test_4eC39HqLyjWDarjtT1zdp7dc"
        )
    }

    // Google Sign-In
    fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    // Handle Google Sign-In result
    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(Exception::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { authTask ->
                    if (authTask.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        Toast.makeText(this, "Welcome, ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    } else {
                        val error = authTask.exception
                        Toast.makeText(this, "Firebase Auth failed: ${error?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } catch (e: Exception) {
            Toast.makeText(this, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    // Handle payment result and other activities
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 12345) { // Replace with your actual requestCode for Stripe payment
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

