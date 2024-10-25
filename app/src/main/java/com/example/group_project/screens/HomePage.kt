package com.example.group_project.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.group_project.AuthModel
import com.example.group_project.AuthState

@Composable
fun HomePage (modifier: Modifier = Modifier, navController: NavController, authModel: AuthModel){
    val authState = authModel.authState.observeAsState()
    when(authState.value) {
        is AuthState.Unauthenticated -> navController.navigate("login")
        else -> Unit
    }

    Box (modifier = Modifier
        .fillMaxSize(),
        contentAlignment = Alignment.Center) {
        Text(text = "Home Screen",
            fontSize = 22.sp
        )
    }
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(text = "Home Page", fontSize = 32.sp)

        TextButton(onClick = {
            authModel.signOut()
        }) {
            Text(text = "Sign out")
        }
    }
}
