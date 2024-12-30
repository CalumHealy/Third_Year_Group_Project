package com.example.group_project.screens

import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun RatingsReviewPage(
    modifier: Modifier = Modifier,
    viewModel: RatingsReviewViewModel = viewModel()
) {
    // State variables for user input
    var ratingInput by remember { mutableStateOf("1") }
    var reviewText by remember { mutableStateOf("") }

    // Collecting state from ViewModel
    val reviewsList by viewModel.reviewsList.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Fetch reviews on first render
    LaunchedEffect(Unit) {
        viewModel.fetchReviews()
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            Text(text = "Ratings and Reviews", fontSize = 24.sp, color = Color.Black)

            // Rating input field
            TextField(
                value = ratingInput,
                onValueChange = { input ->
                    if (input.toIntOrNull() != null) {
                        ratingInput = input.toInt().coerceIn(1, 5).toString()
                    }
                },
                label = { Text("Rate the app (1-5)") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { /* Hide keyboard */ }),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )

            // Review text input field
            TextField(
                value = reviewText,
                onValueChange = { reviewText = it },
                label = { Text("Write your review here") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                maxLines = 5
            )

            // Error message display
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red)
            }

            // Submit button
            Button(
                onClick = {
                    viewModel.submitReview(ratingInput.toInt(), reviewText)
                    reviewText = ""
                },
                enabled = !isSubmitting
            ) {
                Text(text = if (isSubmitting) "Submitting..." else "Submit Review")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Display list of reviews
            if (reviewsList.isNotEmpty()) {
                Text(text = "Previous Reviews:", fontSize = 18.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                reviewsList.forEach { review ->
                    Text(text = "Rating: ${review.rating}/5", fontSize = 16.sp, color = Color.Black)
                    Text(text = review.reviewText, fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            } else {
                Text(text = "No reviews yet.", fontSize = 16.sp, color = Color.Gray)
            }
        }
    }
}