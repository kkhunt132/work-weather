package com.example.weather

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EditLocationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_location)

        val btnClose = findViewById<ImageView>(R.id.btn_close_edit)
        val etSearch = findViewById<EditText>(R.id.et_search_location)

        // Location Containers
        val containerChennai = findViewById<RelativeLayout>(R.id.container_chennai)
        val containerDelhi = findViewById<RelativeLayout>(R.id.container_delhi)

        // Delete Buttons
        val btnDelChennai = findViewById<TextView>(R.id.btn_delete_chennai)
        val btnDelDelhi = findViewById<TextView>(R.id.btn_delete_delhi)

        // Close Activity
        btnClose.setOnClickListener { finish() }

        // Click on Chennai to open SixthActivity
        containerChennai.setOnClickListener {
            startActivity(Intent(this, SixthActivity::class.java))
        }

        // Click on Delhi to open NinthActivity
        containerDelhi.setOnClickListener {
            startActivity(Intent(this, NinthActivity::class.java))
        }

        // Handle Deletions
        btnDelChennai.setOnClickListener { containerChennai.visibility = View.GONE }
        btnDelDelhi.setOnClickListener { containerDelhi.visibility = View.GONE }

        // Handle Search Filtering
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase()
                
                // Filter Chennai
                containerChennai.visibility = if ("chennai".contains(query)) View.VISIBLE else View.GONE
                // Filter Delhi
                containerDelhi.visibility = if ("new delhi".contains(query) || "delhi".contains(query)) View.VISIBLE else View.GONE
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}
