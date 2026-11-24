package com.example.weather

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val etName = findViewById<EditText>(R.id.et_signup_name)
        val etEmail = findViewById<EditText>(R.id.et_signup_email)
        val etPassword = findViewById<EditText>(R.id.et_signup_password)
        val btnSignUp = findViewById<Button>(R.id.btn_signup)
        val tvLogin = findViewById<TextView>(R.id.tv_login)

        btnSignUp.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            } else {
                // Save user credentials in SharedPreferences (for simple demo purposes)
                val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString(email, password)
                editor.putString("name_$email", name)
                
                // Add to user list for Admin to see
                val users = sharedPreferences.getString("user_list", "") ?: ""
                if (!users.contains(email)) {
                    val newUsers = if (users.isEmpty()) email else "$users,$email"
                    editor.putString("user_list", newUsers)
                }

                editor.apply()

                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()
                
                // Go to Login page
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        tvLogin.setOnClickListener {
            finish()
        }
    }
}
