package com.example.weather

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ShareActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        val btnClose = findViewById<ImageView>(R.id.btn_close_share)
        val btnShareNow = findViewById<Button>(R.id.btn_share_now)

        btnClose.setOnClickListener {
            finish()
        }

        btnShareNow.setOnClickListener {
            val appPackageName = packageName
            val shareMessage = """
                Check out the live weather at Ahmedabad!
                Temperature: 37°C
                Condition: Clear Sky
                
                Download this amazing Weather App now:
                https://play.google.com/store/apps/details?id=$appPackageName
            """.trimIndent()
            
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            
            startActivity(Intent.createChooser(shareIntent, "Share Weather via"))
        }
    }
}
