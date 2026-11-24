package com.example.weather

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContentView(R.layout.activity_login)

        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val tvSignup = findViewById<TextView>(R.id.tv_signup)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show()
            } else {
                val registeredPassword = sharedPreferences.getString(email, null)
                
                if (email == "admin@weather.com" && password == "admin123") {
                    Toast.makeText(this, "Admin Login Successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AdminActivity::class.java))
                    finish()
                } else if (registeredPassword != null && registeredPassword == password) {
                    sharedPreferences.edit()
                        .putBoolean("isLoggedIn", true)
                        .putString("currentUserEmail", email)
                        .apply()
                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else if (email == "kkhunt132@weather.com" && password == "keval123") {
                    sharedPreferences.edit()
                        .putBoolean("isLoggedIn", true)
                        .putString("currentUserEmail", email)
                        .apply()
                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Invalid Email or Password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        tvSignup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}
