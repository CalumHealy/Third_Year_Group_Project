package com.example.group_project.screens

import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class Review(
    val rating: Int = 0,
    val reviewText: String = "",
    val timestamp: Long = 0L
) {
    // Secondary constructor to handle string ratings
    constructor(rating: String?, reviewText: String, timestamp: Long) : this(
        rating = rating?.toIntOrNull() ?: 0,
        reviewText = reviewText,
        timestamp = timestamp
    )
}

class RatingsReviewViewModel : ViewModel() {

    private val db: DatabaseReference = FirebaseDatabase.getInstance().getReference("reviews")

    private val _reviewsList = MutableStateFlow<List<Review>>(emptyList())
    val reviewsList: StateFlow<List<Review>> = _reviewsList

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    // Fetch reviews from Realtime Database
    fun fetchReviews() {
        db.get().addOnSuccessListener { snapshot ->
            val reviews = snapshot.children.mapNotNull { dataSnapshot ->
                try {
                    // Manually map the fields to handle potential string rating
                    val rating = dataSnapshot.child("rating").value?.toString()?.toIntOrNull() ?: 0
                    val reviewText = dataSnapshot.child("review").value?.toString() ?: ""
                    val timestamp = dataSnapshot.child("timestamp").value as? Long ?: 0L
                    Review(rating, reviewText, timestamp)
                } catch (e: Exception) {
                    null // Skip problematic entries
                }
            }
            _reviewsList.value = reviews.sortedByDescending { it.timestamp }
        }.addOnFailureListener {
            _errorMessage.value = "Error fetching reviews: ${it.localizedMessage}"
        }
    }

    // Fetch reviews in real-time
    fun fetchReviewsRealtime() {
        db.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val reviews = snapshot.children.mapNotNull { dataSnapshot ->
                    try {
                        // Manually map the fields to handle potential string rating
                        val rating = dataSnapshot.child("rating").value?.toString()?.toIntOrNull() ?: 0
                        val reviewText = dataSnapshot.child("review").value?.toString() ?: ""
                        val timestamp = dataSnapshot.child("timestamp").value as? Long ?: 0L
                        Review(rating, reviewText, timestamp)
                    } catch (e: Exception) {
                        null // Skip problematic entries
                    }
                }
                _reviewsList.value = reviews.sortedByDescending { it.timestamp }
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                _errorMessage.value = "Error fetching reviews: ${error.message}"
            }
        })
    }

    // Submit a review to Realtime Database
    fun submitReview(rating: Int, reviewText: String) {
        if (reviewText.isEmpty()) {
            _errorMessage.value = "Please enter a review before submitting."
            return
        }

        _isSubmitting.value = true
        _errorMessage.value = ""

        val review = mapOf(
            "rating" to rating,
            "review" to reviewText,
            "timestamp" to System.currentTimeMillis()
        )

        db.push().setValue(review).addOnCompleteListener { task ->
            _isSubmitting.value = false
            if (task.isSuccessful) {
                _errorMessage.value = "Review submitted successfully!"
                fetchReviews()
            } else {
                _errorMessage.value = "Error submitting review: ${task.exception?.localizedMessage}"
            }
        }
    }
}
