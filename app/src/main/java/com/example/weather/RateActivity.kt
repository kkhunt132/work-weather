package com.example.weather

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rate)

        val btnClose = findViewById<ImageView>(R.id.btn_close_rate)
        val ratingBar = findViewById<RatingBar>(R.id.rating_bar)
        val btnRateNow = findViewById<Button>(R.id.btn_rate_now)

        btnClose.setOnClickListener {
            finish()
        }

        btnRateNow.setOnClickListener {
            val rating = ratingBar.rating
            if (rating > 0) {
                // Store rating locally
                val sharedPref = getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putFloat("last_rating", rating)

                val currentUser = sharedPref.getString("currentUserEmail", "Student") ?: "Student"
                editor.putFloat("rating_$currentUser", rating)
                
                // Add to user list just in case they haven't signed up via normal way but are logged in
                val users = sharedPref.getString("user_list", "") ?: ""
                if (!users.contains(currentUser)) {
                    val newUsers = if (users.isEmpty()) currentUser else "$users,$currentUser"
                    editor.putString("user_list", newUsers)
                }
                
                editor.apply()

                Toast.makeText(this, "Rated $rating stars! Thank you.", Toast.LENGTH_SHORT).show()
                finish() // Close activity after rating
            } else {
                Toast.makeText(this, "Please select at least one star", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
