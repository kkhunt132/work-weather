package com.example.weather

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnClose = findViewById<ImageView>(R.id.btn_close_settings)
        val etSearch = findViewById<EditText>(R.id.et_search_settings)
        
        // Settings Sections
        val tvHeaderWeather = findViewById<TextView>(R.id.tv_header_weather)
        val tvHeaderApp = findViewById<TextView>(R.id.tv_header_app)

        // Setting Items
        val settingTemp = findViewById<RelativeLayout>(R.id.setting_temp)
        val settingWind = findViewById<RelativeLayout>(R.id.setting_wind)
        val settingPrecip = findViewById<RelativeLayout>(R.id.setting_precip)
        val settingTheme = findViewById<RelativeLayout>(R.id.setting_theme)
        val settingNotif = findViewById<RelativeLayout>(R.id.setting_notif)
        val settingAbout = findViewById<RelativeLayout>(R.id.setting_about)

        // Switches
        val switchTemp = findViewById<SwitchCompat>(R.id.switch_temp)
        val switchWind = findViewById<SwitchCompat>(R.id.switch_wind)
        val switchPrecip = findViewById<SwitchCompat>(R.id.switch_precip)
        val switchTheme = findViewById<SwitchCompat>(R.id.switch_theme)
        val switchNotif = findViewById<SwitchCompat>(R.id.switch_notif)

        val sharedPref = getSharedPreferences("WeatherPrefs", Context.MODE_PRIVATE)
        
        // Load saved states
        switchTemp.isChecked = sharedPref.getBoolean("use_celsius", true)
        switchWind.isChecked = sharedPref.getBoolean("use_kmh", true)
        switchPrecip.isChecked = sharedPref.getBoolean("show_precip", true)
        switchTheme.isChecked = sharedPref.getBoolean("dark_mode", true)
        switchNotif.isChecked = sharedPref.getBoolean("notifications", true)

        btnClose.setOnClickListener { finish() }

        // Theme Switch Logic
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("dark_mode", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Toast.makeText(this, "Dark Mode Enabled", Toast.LENGTH_SHORT).show()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Toast.makeText(this, "Light Mode Enabled", Toast.LENGTH_SHORT).show()
            }
        }

        // Other Switches Save Logic with Toasts
        switchTemp.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("use_celsius", isChecked).apply()
            val msg = if (isChecked) "Using Celsius (°C)" else "Using Fahrenheit (°F)"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        switchWind.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("use_kmh", isChecked).apply()
            val msg = if (isChecked) "Wind speed in km/h" else "Wind speed in mph"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        switchPrecip.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("show_precip", isChecked).apply()
            val msg = if (isChecked) "Precipitation Shown" else "Precipitation Hidden"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        switchNotif.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("notifications", isChecked).apply()
            val msg = if (isChecked) "Notifications On" else "Notifications Off"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        // Search Filter Logic
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase()
                
                // Weather group visibility
                val showTemp = "temperature".contains(query) || "celsius".contains(query) || "unit".contains(query)
                val showWind = "wind".contains(query) || "speed".contains(query) || "km/h".contains(query)
                val showPrecip = "precipitation".contains(query) || "rain".contains(query) || "snow".contains(query)
                
                settingTemp.visibility = if (query.isEmpty() || showTemp) View.VISIBLE else View.GONE
                settingWind.visibility = if (query.isEmpty() || showWind) View.VISIBLE else View.GONE
                settingPrecip.visibility = if (query.isEmpty() || showPrecip) View.VISIBLE else View.GONE
                
                tvHeaderWeather.visibility = if (settingTemp.visibility == View.VISIBLE || 
                    settingWind.visibility == View.VISIBLE || 
                    settingPrecip.visibility == View.VISIBLE) View.VISIBLE else View.GONE

                // App group visibility
                val showTheme = "theme".contains(query) || "dark".contains(query) || "mode".contains(query) || "night".contains(query)
                val showNotif = "notification".contains(query) || "alert".contains(query) || "weather".contains(query)
                val showAbout = "about".contains(query) || "version".contains(query) || "app".contains(query)

                settingTheme.visibility = if (query.isEmpty() || showTheme) View.VISIBLE else View.GONE
                settingNotif.visibility = if (query.isEmpty() || showNotif) View.VISIBLE else View.GONE
                settingAbout.visibility = if (query.isEmpty() || showAbout) View.VISIBLE else View.GONE

                tvHeaderApp.visibility = if (settingTheme.visibility == View.VISIBLE || 
                    settingNotif.visibility == View.VISIBLE || 
                    settingAbout.visibility == View.VISIBLE) View.VISIBLE else View.GONE
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Click listeners to toggle switches when the row is clicked
        settingTemp.setOnClickListener { switchTemp.toggle() }
        settingWind.setOnClickListener { switchWind.toggle() }
        settingPrecip.setOnClickListener { switchPrecip.toggle() }
        settingTheme.setOnClickListener { switchTheme.toggle() }
        settingNotif.setOnClickListener { switchNotif.toggle() }
        settingAbout.setOnClickListener { Toast.makeText(this, "Weather App Version 1.0.4", Toast.LENGTH_SHORT).show() }

        val settingLogout = findViewById<RelativeLayout>(R.id.setting_logout)
        settingLogout.setOnClickListener {
            val userPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            userPrefs.edit().putBoolean("isLoggedIn", false).apply()
            
            Toast.makeText(this, "Logged Out Successfully", Toast.LENGTH_SHORT).show()
            
            // Redirect to LoginActivity and clear task stack
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
