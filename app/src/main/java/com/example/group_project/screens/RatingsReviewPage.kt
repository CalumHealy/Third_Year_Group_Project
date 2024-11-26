package com.example.group_project.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RatingsReviewPage(modifier: Modifier = Modifier, navController: NavController) {
    // State variables for input fields
    var rating by remember { mutableStateOf(1) } // Default to 1 rating
    var reviewText by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var reviewsList by remember { mutableStateOf<List<Review>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }

    // Firebase Firestore instance
    val db = FirebaseFirestore.getInstance()

    // Function to fetch reviews from Firestore
    fun fetchReviews() {
        db.collection("reviews")
            .orderBy("timestamp") // Ordering reviews by timestamp
            .get()
            .addOnSuccessListener { snapshot ->
                reviewsList = snapshot.documents.map { document ->
                    Review(
                        rating = document.getLong("rating")?.toInt() ?: 1,
                        reviewText = document.getString("reviewText") ?: "",
                        timestamp = document.getLong("timestamp") ?: 0
                    )
                }
            }
            .addOnFailureListener {
                errorMessage = "Error fetching reviews."
            }
    }

    // Fetch reviews when the page is first loaded
    LaunchedEffect(Unit) {
        fetchReviews()
    }

    // Function to submit a new review to Firestore
    fun submitReview() {
        if (reviewText.isNotEmpty()) {
            isSubmitting = true
            val reviewData = hashMapOf(
                "rating" to rating,
                "reviewText" to reviewText,
                "timestamp" to System.currentTimeMillis()
            )

            // Add the review to Firestore
            db.collection("reviews")
                .add(reviewData)
                .addOnSuccessListener {
                    reviewText = ""  // Clear input after successful submission
                    isSubmitting = false
                    errorMessage = "Review submitted successfully!"
                    // Refresh reviews after submission
                    fetchReviews()
                }
                .addOnFailureListener {
                    isSubmitting = false
                    errorMessage = "Error submitting your review. Please try again."
                }
        } else {
            errorMessage = "Please enter a review before submitting."
        }
    }

    // UI for displaying the page
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            Text(text = "Ratings and Reviews", fontSize = 24.sp, color = Color.Black)

            // Rating input (just an integer input)
            Text("Rate the app (1-5):", fontSize = 16.sp)
            TextField(
                value = rating.toString(),
                onValueChange = {
                    if (it.toIntOrNull() != null) rating = it.toInt().coerceIn(1, 5)
                },
                label = { Text(text = "Rating (1-5)") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { /* Handle input done */ }),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )

            // Review input field
            TextField(
                value = reviewText,
                onValueChange = { reviewText = it },
                label = { Text(text = "Write your review here") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                maxLines = 5
            )

            // Error message
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red)
            }

            // Submit button
            Button(
                onClick = { submitReview() },
                enabled = !isSubmitting
            ) {
                Text(text = if (isSubmitting) "Submitting..." else "Submit Review")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Display previously submitted reviews
            if (reviewsList.isNotEmpty()) {
                Text(text = "Previous Reviews:", fontSize = 18.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                reviewsList.forEach { review ->
                    Text("Rating: ${review.rating}/5", fontSize = 16.sp, color = Color.Black)
                    Text(review.reviewText, fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            } else {
                Text(text = "No reviews yet.", fontSize = 16.sp, color = Color.Gray)
            }

            // Back button below the Submit button
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.popBackStack() } // Navigate back to MenuPage
            ) {
                Text(text = "Back")
            }
        }
    }
}

// Data class for Reviews
data class Review(
    val rating: Int,
    val reviewText: String,
    val timestamp: Long
)

