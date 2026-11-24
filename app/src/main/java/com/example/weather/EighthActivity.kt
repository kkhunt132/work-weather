package com.example.weather

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class EighthActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private val apiKey = "ab72cbacde30115c358875649800dc6d"
    private var city = "Rajkot"
    private var currentHumidityFromAPI: Int? = null
    private var forecastList: List<ForecastItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eighth)

        city = intent.getStringExtra("CITY") ?: "Chennai"
        findViewById<TextView>(R.id.tv_location_p8).text = if(city == "Chennai") "Chennai, Tamil Nadu" else "Delhi, India"

        // Navigation
        findViewById<TextView>(R.id.btn_skip_p8).setOnClickListener {
            startActivity(Intent(this, NinthActivity::class.java))
            finish()
        }
        findViewById<TextView>(R.id.tab_today_p8).setOnClickListener {
            if (city == "Chennai") {
                startActivity(Intent(this, SixthActivity::class.java))
            } else {
                startActivity(Intent(this, NinthActivity::class.java))
            }
            finish()
        }
        findViewById<TextView>(R.id.tab_forecast_p8).setOnClickListener {
            startActivity(Intent(this, SeventhActivity::class.java).apply {
                putExtra("CITY", city)
            })
            finish()
        }

        startLiveUpdates()
    }

    private fun startLiveUpdates() {
        handler.post(object : Runnable {
            override fun run() {
                fetchCurrentWeather()
                fetchWeatherForecast()
                handler.postDelayed(this, 10000) // Balanced 10 seconds for safety and live experience
            }
        })
    }

    private fun fetchCurrentWeather() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(WeatherService::class.java)

        service.getWeatherData(city, apiKey, "metric").enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        currentHumidityFromAPI = it.main.humidity
                        findViewById<TextView>(R.id.tv_humidity_live_p8)?.text = "Humidity: ${it.main.humidity}%"
                        forecastList?.let { list -> updateUI(list) }
                    }
                }
            }
            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {}
        })
    }

    private fun fetchWeatherForecast() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(WeatherService::class.java)

        service.getForecastData(city, apiKey, "metric").enqueue(object : Callback<ForecastResponse> {
            override fun onResponse(call: Call<ForecastResponse>, response: Response<ForecastResponse>) {
                if (response.isSuccessful) {
                    val list = response.body()?.list
                    if (list != null && list.isNotEmpty()) {
                        forecastList = list
                        updateUI(list)
                    }
                }
            }
            override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                Log.e("EighthActivity", "API Failure: ${t.message}")
            }
        })
    }

    private fun updateUI(list: List<ForecastItem>) {
        runOnUiThread {
            try {
                val calendar = Calendar.getInstance()
                val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

                val bars = listOf(R.id.bar1_p8, R.id.bar2_p8, R.id.bar3_p8, R.id.bar4_p8, R.id.bar5_p8, R.id.bar6_p8, R.id.bar7_p8)
                val dayTexts = listOf(R.id.tv_day1_name_p8, R.id.tv_day2_name_p8, R.id.tv_day3_name_p8, R.id.tv_day4_name_p8, R.id.tv_day5_name_p8, R.id.tv_day6_name_p8, R.id.tv_day7_name_p8)
                val dayHumidTexts = listOf(R.id.tv_day1_humid_p8, R.id.tv_day2_humid_p8, R.id.tv_day3_humid_p8, R.id.tv_day4_humid_p8, R.id.tv_day5_humid_p8, R.id.tv_day6_humid_p8, R.id.tv_day7_humid_p8)
                val days = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")

                bars.forEachIndexed { index, barId ->
                    val targetDay = index + 1
                    // Always get an item, fallback to list index if specific day not found
                    val itemForDay = findForecastForDay(list, targetDay) ?: list[index % list.size]
                    
                    val barView = findViewById<android.view.View>(barId)
                    val dayTv = findViewById<TextView>(dayTexts[index])
                    val humidTv = findViewById<TextView>(dayHumidTexts[index])
                    val container = findViewById<android.widget.LinearLayout>(when(index) {
                        0 -> R.id.day1_container_p8; 1 -> R.id.day2_container_p8
                        2 -> R.id.day3_container_p8; 3 -> R.id.day4_container_p8
                        4 -> R.id.day5_container_p8; 5 -> R.id.day6_container_p8
                        else -> R.id.day7_container_p8
                    })

                    dayTv?.text = days[index]

                    var humidity = itemForDay.main.humidity
                    val dateText = formatToDate(itemForDay.dt_txt)

                    if (targetDay == currentDayOfWeek && currentHumidityFromAPI != null) {
                        humidity = currentHumidityFromAPI!!
                    }

                    humidTv?.text = "${humidity}%"

                    container?.setOnClickListener {
                        val displayHumid = if (targetDay == currentDayOfWeek && currentHumidityFromAPI != null) currentHumidityFromAPI!! else humidity
                        findViewById<TextView>(R.id.tv_humidity_live_p8).text = "Humidity: ${displayHumid}%"
                        findViewById<TextView>(R.id.tv_date_row1_p8).text = if(targetDay == currentDayOfWeek) "TODAY" else dateText
                        findViewById<TextView>(R.id.tv_temp_row1_p8).text = "Clouds: ${itemForDay.clouds.all}%\nHumidity: ${displayHumid}%\n${Math.round(itemForDay.main.temp).toInt()}°C"
                        findViewById<android.widget.ImageView>(R.id.iv_icon_row1_p8).setImageResource(R.drawable.first)
                    }

                    if (targetDay == currentDayOfWeek) {
                        barView?.setBackgroundColor(android.graphics.Color.parseColor("#FF9F5A"))
                        dayTv?.setTextColor(android.graphics.Color.WHITE)
                        findViewById<TextView>(R.id.tv_humidity_live_p8).text = "Humidity: ${humidity}%"
                        
                        findViewById<TextView>(R.id.tv_date_row1_p8).text = "TODAY"
                        findViewById<TextView>(R.id.tv_temp_row1_p8).text = "Clouds: ${itemForDay.clouds.all}%\nHumidity: ${humidity}%\n${Math.round(itemForDay.main.temp).toInt()}°C"
                        findViewById<android.widget.ImageView>(R.id.iv_icon_row1_p8).setImageResource(R.drawable.first)
                    } else {
                        barView?.setBackgroundColor(android.graphics.Color.parseColor("#606677"))
                        dayTv?.setTextColor(android.graphics.Color.parseColor("#8E94A3"))
                    }

                    val params = barView?.layoutParams
                    if (params != null) {
                        val density = resources.displayMetrics.density
                        val heightInDp = (humidity * 2.2).coerceIn(40.0, 180.0)
                        params.height = (heightInDp * density).toInt()
                        barView.layoutParams = params
                    }
                }

                // Row 2 (Next Day)
                val nextDayItem = list.firstOrNull { 
                    val cal = Calendar.getInstance()
                    val today = cal.get(Calendar.DAY_OF_YEAR)
                    val itemCal = Calendar.getInstance()
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val d = sdf.parse(it.dt_txt) ?: Date()
                    itemCal.time = d
                    itemCal.get(Calendar.DAY_OF_YEAR) != today
                }
                nextDayItem?.let {
                    findViewById<TextView>(R.id.tv_date_row2_p8).text = formatToDate(it.dt_txt)
                    findViewById<TextView>(R.id.tv_temp_row2_p8).text = "Clouds: ${it.clouds.all}%\nHumidity: ${it.main.humidity}%\n${Math.round(it.main.temp).toInt()}°C"
                    findViewById<android.widget.ImageView>(R.id.iv_icon_row2_p8).setImageResource(R.drawable.first)
                }
            } catch (e: Exception) {
                Log.e("EighthActivity", "UI Error: ${e.message}")
            }
        }
    }

    private fun findForecastForDay(list: List<ForecastItem>, targetDayOfWeek: Int): ForecastItem? {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val cal = Calendar.getInstance()
        for (item in list) {
            val date = sdf.parse(item.dt_txt) ?: continue
            cal.time = date
            if (cal.get(Calendar.DAY_OF_WEEK) == targetDayOfWeek && cal.get(Calendar.HOUR_OF_DAY) in 11..15) return item
        }
        for (item in list) {
            val date = sdf.parse(item.dt_txt) ?: continue
            cal.time = date
            if (cal.get(Calendar.DAY_OF_WEEK) == targetDayOfWeek) return item
        }
        return null
    }

    private fun formatToDate(dateStr: String): String {
        return try {
            val input = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val output = SimpleDateFormat("EEE\nMMM d", Locale.getDefault())
            val date = input.parse(dateStr)
            output.format(date!!).uppercase()
        } catch (e: Exception) {
            dateStr
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
