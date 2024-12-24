package com.example.group_project.screens


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Review(
    val rating: Int,
    val reviewText: String,
    val timestamp: Long
)

class RatingsReviewViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    // StateFlows to hold UI state
    private val _reviewsList = MutableStateFlow<List<Review>>(emptyList())
    val reviewsList: StateFlow<List<Review>> = _reviewsList

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    // Fetch reviews from Firestore
    fun fetchReviews() {
        db.collection("reviews")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { snapshot ->
                _reviewsList.value = snapshot.documents.map { document ->
                    Review(
                        rating = document.getLong("rating")?.toInt() ?: 1,
                        reviewText = document.getString("reviewText") ?: "",
                        timestamp = document.getLong("timestamp") ?: 0L
                    )
                }
            }
            .addOnFailureListener {
                _errorMessage.value = "Error fetching reviews: ${it.localizedMessage}"
            }
    }

    // Submit a review to Firestore
    fun submitReview(rating: Int, reviewText: String) {
        if (reviewText.isEmpty()) {
            _errorMessage.value = "Please enter a review before submitting."
            return
        }

        _isSubmitting.value = true
        val reviewData = hashMapOf(
            "rating" to rating,
            "reviewText" to reviewText,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("reviews")
            .add(reviewData)
            .addOnSuccessListener {
                _isSubmitting.value = false
                _errorMessage.value = "Review submitted successfully!"
                fetchReviews() // Refresh the list after submission
            }
            .addOnFailureListener {
                _isSubmitting.value = false
                _errorMessage.value = "Error submitting review: ${it.localizedMessage}"
            }
    }
}