package com.example.weather

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        val btnClose = findViewById<ImageView>(R.id.btn_close_notif)
        btnClose.setOnClickListener {
            finish()
        }
    }
}
