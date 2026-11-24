package com.example.weather

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

class FifthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fifth)

        val btnNext = findViewById<FrameLayout>(R.id.btn_to_page6)
        btnNext.setOnClickListener {
            startActivity(Intent(this, SixthActivity::class.java))
        }
    }
}
