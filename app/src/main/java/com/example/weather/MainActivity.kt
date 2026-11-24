package com.example.weather

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find the FrameLayout which is our new button container
        val btnToPage2 = findViewById<View>(R.id.btn_to_page2)
        btnToPage2.setOnClickListener {
            startActivity(Intent(this, SecondActivity::class.java))
        }
    }
}
