package com.example.weather

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val btnLogout = findViewById<Button>(R.id.btn_admin_logout)
        val containerSubmissions = findViewById<LinearLayout>(R.id.container_submissions)
        val tvNoData = findViewById<TextView>(R.id.tv_no_data)
        val tvTotalStudents = findViewById<TextView>(R.id.tv_total_students)

        btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userListString = sharedPref.getString("user_list", "kkhunt132@weather.com") ?: "kkhunt132@weather.com"
        
        val users = userListString.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()

        tvTotalStudents.text = "Total Active Students: ${users.size}"
        tvTotalStudents.setTextColor(Color.WHITE)

        if (users.isNotEmpty()) {
            tvNoData.visibility = View.GONE
            containerSubmissions.removeAllViews()

            for (userEmail in users) {
                val name = sharedPref.getString("name_$userEmail", "Keval Khunt") ?: "Keval Khunt"
                val rating = sharedPref.getFloat("rating_$userEmail", 0f)
                val feedback = sharedPref.getString("feedback_$userEmail", "No feedback submitted yet") ?: "No feedback submitted yet"

                // Root Container for a single student's record
                val studentProfileWrapper = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { setMargins(0, 20, 0, 80) }
                }

                // Section Label
                val tvSectionLabel = TextView(this).apply {
                    text = "STUDENT PROFILE RECORD"
                    setTextColor(Color.parseColor("#4B5563"))
                    textSize = 12f
                    letterSpacing = 0.2f
                    setTypeface(null, Typeface.BOLD)
                    setPadding(10, 0, 0, 15)
                }
                studentProfileWrapper.addView(tvSectionLabel)

                // 1. Box for Student Name
                studentProfileWrapper.addView(createDetailBox("Student Full Name", "🧑‍🎓 $name", "#FFFFFF"))

                // 2. Box for Email ID
                studentProfileWrapper.addView(createDetailBox("Registered Email ID", "📧 $userEmail", "#94A3B8"))

                // 3. Box for Rating Given
                val ratingStr = if (rating > 0f) "⭐ $rating / 5.0 Stars" else "Not Rated Yet"
                val ratingColor = if (rating > 0f) "#FBBF24" else "#4B5563"
                studentProfileWrapper.addView(createDetailBox("App Rating Given", ratingStr, ratingColor))

                // 4. Box for Feedback Message
                studentProfileWrapper.addView(createDetailBox("Student Feedback Message", "💬 $feedback", "#E5E7EB"))

                containerSubmissions.addView(studentProfileWrapper)
            }
        } else {
            tvNoData.visibility = View.VISIBLE
        }
    }

    private fun createDetailBox(titleLabel: String, valueText: String, textValueColor: String): LinearLayout {
        val boxLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 6, 0, 6) }
            setPadding(40, 32, 40, 32)
            setBackgroundResource(R.drawable.admin_info_box_bg)
        }

        // Title Label inside box
        val tvLabel = TextView(this).apply {
            text = titleLabel.uppercase()
            setTextColor(Color.parseColor("#4B5563"))
            textSize = 10f
            setTypeface(null, Typeface.BOLD)
            letterSpacing = 0.05f
        }

        // Content Text inside box
        val tvValue = TextView(this).apply {
            text = valueText
            setTextColor(Color.parseColor(textValueColor))
            textSize = 15f
            setPadding(0, 8, 0, 0)
            setTypeface(null, Typeface.BOLD)
        }

        boxLayout.addView(tvLabel)
        boxLayout.addView(tvValue)
        return boxLayout
    }
}
