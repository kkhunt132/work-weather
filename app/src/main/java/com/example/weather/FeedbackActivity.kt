package com.example.weather

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class FeedbackActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        val btnClose = findViewById<ImageView>(R.id.btn_close_feedback)
        val etMessage = findViewById<EditText>(R.id.et_feedback_message)
        val btnSubmit = findViewById<Button>(R.id.btn_submit_feedback)

        btnClose.setOnClickListener {
            finish()
        }

        btnSubmit.setOnClickListener {
            val message = etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                // Store feedback locally
                val sharedPref = getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putString("last_feedback", message)
                
                val currentUser = sharedPref.getString("currentUserEmail", "Student") ?: "Student"
                editor.putString("feedback_$currentUser", message)
                editor.apply()

                Toast.makeText(this, "Message submitted successfully", Toast.LENGTH_SHORT).show()
                etMessage.text.clear()
                finish()
            } else {
                Toast.makeText(this, "Please enter your feedback", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
