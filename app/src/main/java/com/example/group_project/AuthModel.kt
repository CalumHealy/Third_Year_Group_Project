package com.example.group_project

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthModel: ViewModel() {
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState : LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        if(auth.currentUser==null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated
        }
    }

    fun login(email : String, password : String) {

        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }

        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{task->
                if (task.isSuccessful) {
                   _authState.value = AuthState.Authenticated
                }else {
                    _authState.value = AuthState.Error(task.exception?.message?:"Error")
                }
            }
    }

    fun signup(email : String, password : String) {

        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }

        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{task->
                if (task.isSuccessful) {
                    // Get user ID after successful signup
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener

                    // Save user data in Firebase Realtime Database
                    val user = mapOf("email" to email)
                    val userRef = database.reference.child("users").child(userId)

                    userRef.setValue(user)
                        .addOnCompleteListener { dbTask ->
                            if (dbTask.isSuccessful) {
                                _authState.value = AuthState.Authenticated
                            } else {
                                _authState.value =AuthState.Error("Failed to save user data")
                            }
                        }
                }else {
                    _authState.value = AuthState.Error(task.exception?.message?:"Error")
                }
            }
    }

    fun signOut() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }
}


sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message : String) : AuthState()
}