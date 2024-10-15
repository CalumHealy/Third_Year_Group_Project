package com.example.group_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class splashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // find buttons by their IDs
        val loginButton: Button = findViewById(R.id.splashLoginButton)
        val signupButton: Button = findViewById(R.id.splashSignupButton)

        // set onClickListener for Login button
        loginButton.setOnClickListener {
            // Navigate to LoginActivity when Login button is clicked
            val intent = Intent(this, loginActivity::class.java)
            startActivity(intent)
        }

        // set onClickListener for register button
        signupButton.setOnClickListener {
            // Navigate to registerActivity when Sign Up button is clicked
            val intent = Intent(this, registerActivity::class.java)
            startActivity(intent)
        }
    }
}

