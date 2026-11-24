package com.example.weather

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TenthActivity : AppCompatActivity() {

    private lateinit var tvChennaiData: TextView
    private lateinit var tvDelhiData: TextView
    private val handler = Handler(Looper.getMainLooper())
    private val apiKey = "ab72cbacde30115c358875649800dc6d"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tenth)

        tvChennaiData = findViewById(R.id.tv_chennai_data)
        tvDelhiData = findViewById(R.id.tv_delhi_data)

        // Set logged in user's email into the Profile Name TextView
        val sharedPref = getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE)
        val currentUserEmail = sharedPref.getString("currentUserEmail", "kkhunt132@weather.com")
        findViewById<TextView>(R.id.tv_profile_name)?.text = currentUserEmail

        setupClickListeners()
        startLiveUpdates()
    }

    private fun setupClickListeners() {
        findViewById<androidx.cardview.widget.CardView>(R.id.card_profile).setOnClickListener {
            // Summary dialog removed as per user request
            Toast.makeText(this, "Profile Details", Toast.LENGTH_SHORT).show()
        }

        val btnMenu = findViewById<View>(R.id.btn_menu_p10)
        val btnLogoutOption = findViewById<TextView>(R.id.btn_logout_option)
        btnMenu.setOnClickListener {
            btnLogoutOption.visibility = if (btnLogoutOption.visibility == View.GONE) View.VISIBLE else View.GONE
        }
        btnLogoutOption.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        findViewById<TextView>(R.id.btn_edit_location).setOnClickListener {
            startActivity(Intent(this, EditLocationActivity::class.java))
        }

        findViewById<RelativeLayout>(R.id.btn_chennai).setOnClickListener {
            startActivity(Intent(this, SixthActivity::class.java).apply {
                putExtra("CITY", "Chennai")
            })
        }

        findViewById<RelativeLayout>(R.id.btn_delhi).setOnClickListener {
            startActivity(Intent(this, NinthActivity::class.java).apply {
                putExtra("CITY", "Delhi")
            })
        }

        findViewById<RelativeLayout>(R.id.btn_notifications).setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        findViewById<RelativeLayout>(R.id.btn_settings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        findViewById<RelativeLayout>(R.id.btn_feedback).setOnClickListener {
            startActivity(Intent(this, FeedbackActivity::class.java))
        }

        findViewById<RelativeLayout>(R.id.btn_rate).setOnClickListener {
            startActivity(Intent(this, RateActivity::class.java))
        }

        findViewById<RelativeLayout>(R.id.btn_share).setOnClickListener {
            startActivity(Intent(this, ShareActivity::class.java))
        }
    }

    private fun startLiveUpdates() {
        handler.post(object : Runnable {
            override fun run() {
                fetchWeather("Chennai", tvChennaiData)
                fetchWeather("Delhi", tvDelhiData)
                handler.postDelayed(this, 10000)
            }
        })
    }

    private fun fetchWeather(city: String, textView: TextView) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(WeatherService::class.java)

        service.getWeatherData(city, apiKey, "metric").enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val temp = Math.round(it.main.temp).toInt()
                        val status = it.weather[0].description.replaceFirstChar { it.uppercase() }
                        textView.text = "$temp°, $status"
                    }
                }
            }
            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {}
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
