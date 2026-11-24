package com.example.weather

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationsActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private val apiKey = "ab72cbacde30115c358875649800dc6d"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        findViewById<ImageView>(R.id.btn_close_notifications).setOnClickListener { finish() }

        setupExpandableCards()
        startLiveUpdates()
    }

    private fun setupExpandableCards() {
        val cardIds = listOf(R.id.notif_card_1, R.id.notif_card_2, R.id.notif_card_3, R.id.notif_card_4, R.id.notif_card_5)
        val detailIds = listOf(R.id.tv_detail_1, R.id.tv_detail_2, R.id.tv_detail_3, R.id.tv_detail_4, R.id.tv_detail_5)

        cardIds.forEachIndexed { index, cardId ->
            findViewById<LinearLayout>(cardId).setOnClickListener {
                val detailView = findViewById<TextView>(detailIds[index])
                detailView.visibility = if (detailView.visibility == View.GONE) View.VISIBLE else View.GONE
            }
        }
    }

    private fun startLiveUpdates() {
        handler.post(object : Runnable {
            override fun run() {
                // Fetching for all cities defined in XML with correct IDs
                fetchWeather("Delhi", R.id.tv_notif1_temp, R.id.tv_detail_1, R.id.img_notif1_icon, R.id.tv_notif1_time)
                fetchWeather("Bangalore", R.id.tv_notif2_temp, R.id.tv_detail_2, R.id.img_notif2_icon, R.id.tv_notif2_time)
                fetchWeather("Chennai", R.id.tv_notif3_temp, R.id.tv_detail_3, R.id.img_notif3_icon, R.id.tv_notif3_time)
                fetchWeather("Mumbai", R.id.tv_notif4_temp, R.id.tv_detail_4, R.id.img_notif4_icon, R.id.tv_notif4_time)
                fetchWeather("Kolkata", R.id.tv_notif5_temp, R.id.tv_detail_5, R.id.img_notif5_icon, R.id.tv_notif5_time)
                
                // Refresh every 10 seconds for "live" feel
                handler.postDelayed(this, 10000)
            }
        })
    }

    private fun fetchWeather(city: String, tvTempId: Int, tvDetailId: Int, imgIconId: Int, tvTimeId: Int) {
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
                        val desc = it.weather[0].description.replaceFirstChar { it.uppercase() }
                        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                        
                        findViewById<TextView>(tvTempId).text = "$temp°C"
                        findViewById<TextView>(tvTimeId).text = "Updated: $currentTime"
                        findViewById<TextView>(tvDetailId).text = "Condition: $desc in $city. Humidity: ${it.main.humidity}%. Wind: ${it.wind.speed} km/h."
                        
                        // Force icon to 'first' as per previous rules
                        findViewById<ImageView>(imgIconId).setImageResource(R.drawable.first)
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
