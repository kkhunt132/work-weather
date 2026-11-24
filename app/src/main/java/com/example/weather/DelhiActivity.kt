package com.example.weather

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class DelhiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delhi)

        val btnClose = findViewById<ImageView>(R.id.btn_close_delhi)
        btnClose.setOnClickListener {
            finish()
        }
    }
}
