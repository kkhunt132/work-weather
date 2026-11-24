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

class SeventhActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private val apiKey = "ab72cbacde30115c358875649800dc6d"
    private var city = "Chennai"
    private var currentTempFromAPI: Int? = null
    private var forecastList: List<ForecastItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seventh)

        city = intent.getStringExtra("CITY") ?: "Chennai"
        findViewById<TextView>(R.id.tv_location_p7).text = if(city == "Chennai") "Chennai, Tamil Nadu" else "Delhi, India"

        // Navigation Setup
        findViewById<TextView>(R.id.tab_today_p7).setOnClickListener {
            if (city == "Chennai") {
                startActivity(Intent(this, SixthActivity::class.java).apply { putExtra("CITY", "Chennai") })
            } else {
                startActivity(Intent(this, NinthActivity::class.java).apply { putExtra("CITY", "Delhi") })
            }
            finish()
        }
        findViewById<TextView>(R.id.tab_precipitation_p7).setOnClickListener {
            startActivity(Intent(this, EighthActivity::class.java).apply {
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
                handler.postDelayed(this, 10000) // Balanced 10 seconds for live feel & battery safety
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
                        val  currentTemp = Math.round(it.main.temp).toInt()
                        currentTempFromAPI = currentTemp
                        findViewById<TextView>(R.id.tv_average_p7)?.text = "Humidity: ${it.main.humidity}% | Temp: ${currentTemp}°C"
                        // Refresh graph with new current temp if forecast is already loaded
                        forecastList?.let { list -> updateForecastUI(list) }
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
                        updateForecastUI(list)
                    }
                }
            }
            override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                Log.e("LiveUpdate", "API Failure: ${t.message}")
            }
        })
    }

    private fun updateForecastUI(list: List<ForecastItem>) {
        runOnUiThread {
            try {
                val calendar = Calendar.getInstance()
                val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

                val bars = listOf(R.id.bar1_p7, R.id.bar2_p7, R.id.bar3_p7, R.id.bar4_p7, R.id.bar5_p7, R.id.bar6_p7, R.id.bar7_p7)
                val dayTexts = listOf(R.id.tv_day1_name_p7, R.id.tv_day2_name_p7, R.id.tv_day3_name_p7, R.id.tv_day4_name_p7, R.id.tv_day5_name_p7, R.id.tv_day6_name_p7, R.id.tv_day7_name_p7)
                val dayTempTexts = listOf(R.id.tv_day1_temp_p7, R.id.tv_day2_temp_p7, R.id.tv_day3_temp_p7, R.id.tv_day4_temp_p7, R.id.tv_day5_temp_p7, R.id.tv_day6_temp_p7, R.id.tv_day7_temp_p7)
                val days = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")

                bars.forEachIndexed { index, barId ->
                    val targetDay = index + 1 // SUN=1, MON=2...
                    // Always get an item, fallback to list index if specific day not found
                    val itemForDay = findForecastForDay(list, targetDay) ?: list[index % list.size]
                    
                    val barView = findViewById<android.view.View>(barId)
                    val dayTv = findViewById<TextView>(dayTexts[index])
                    val tempTv = findViewById<TextView>(dayTempTexts[index])
                    val container = findViewById<android.widget.LinearLayout>(when(index) {
                        0 -> R.id.day1_container_p7; 1 -> R.id.day2_container_p7
                        2 -> R.id.day3_container_p7; 3 -> R.id.day4_container_p7
                        4 -> R.id.day5_container_p7; 5 -> R.id.day6_container_p7
                        else -> R.id.day7_container_p7
                    })

                    // Always set Day Name
                    dayTv?.text = days[index]

                    var temp = Math.round(itemForDay.main.temp).toInt()
                    val maxTemp = Math.round(itemForDay.main.temp_max).toInt()
                    val minTemp = Math.round(itemForDay.main.temp_min).toInt()
                    val desc = itemForDay.weather[0].description
                    val condition = itemForDay.weather[0].main
                    val dateText = formatToDate(itemForDay.dt_txt)

                    if (targetDay == currentDayOfWeek && currentTempFromAPI != null) {
                        temp = currentTempFromAPI!!
                    }

                    tempTv?.text = "$temp°"
                    
                    container?.setOnClickListener {
                        var displayMax = maxTemp
                        if (targetDay == currentDayOfWeek && currentTempFromAPI != null) displayMax = currentTempFromAPI!!
                        
                        findViewById<TextView>(R.id.tv_date_row1)?.text = if(targetDay == currentDayOfWeek) "TODAY" else dateText
                        findViewById<TextView>(R.id.tv_temp_row1)?.text = "${desc.replaceFirstChar { it.uppercase() }}\n${displayMax}° / ${minTemp}°"
                        updateWeatherIcon(condition, findViewById(R.id.iv_icon_row1))
                    }

                    if (targetDay == currentDayOfWeek) {
                        barView?.setBackgroundColor(android.graphics.Color.parseColor("#FF9F5A"))
                        dayTv?.setTextColor(android.graphics.Color.WHITE)
                        
                        var displayMax = maxTemp
                        if (currentTempFromAPI != null) displayMax = currentTempFromAPI!!
                        findViewById<TextView>(R.id.tv_date_row1)?.text = "TODAY"
                        findViewById<TextView>(R.id.tv_temp_row1)?.text = "${desc.replaceFirstChar { it.uppercase() }}\n${displayMax}° / ${minTemp}°"
                        updateWeatherIcon(condition, findViewById(R.id.iv_icon_row1))
                    } else {
                        barView?.setBackgroundColor(android.graphics.Color.parseColor("#8E94A3"))
                        dayTv?.setTextColor(android.graphics.Color.parseColor("#8E94A3"))
                    }

                    val params = barView?.layoutParams
                    if (params != null) {
                        val density = resources.displayMetrics.density
                        val heightInDp = (temp * 4).coerceIn(40, 180)
                        params.height = (heightInDp * density).toInt()
                        barView.layoutParams = params
                    }
                }

                // Initial Row 2 (Next Day)
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
                    findViewById<TextView>(R.id.tv_date_row2)?.text = formatToDate(it.dt_txt)
                    findViewById<TextView>(R.id.tv_temp_row2)?.text = "${it.weather[0].description.replaceFirstChar { it.uppercase() }}\n${Math.round(it.main.temp_max).toInt()}° / ${Math.round(it.main.temp_min).toInt()}°"
                    updateWeatherIcon(it.weather[0].main, findViewById(R.id.iv_icon_row2))
                }
            } catch (e: Exception) {
                Log.e("LiveUpdate", "UI Error: ${e.message}")
            }
        }
    }

    private fun findForecastForDay(list: List<ForecastItem>, targetDayOfWeek: Int): ForecastItem? {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val cal = Calendar.getInstance()
        // Try to find mid-day forecast (12 PM)
        for (item in list) {
            val date = sdf.parse(item.dt_txt) ?: continue
            cal.time = date
            if (cal.get(Calendar.DAY_OF_WEEK) == targetDayOfWeek && cal.get(Calendar.HOUR_OF_DAY) in 11..15) return item
        }
        // Fallback to any forecast for that day
        for (item in list) {
            val date = sdf.parse(item.dt_txt) ?: continue
            cal.time = date
            if (cal.get(Calendar.DAY_OF_WEEK) == targetDayOfWeek) return item
        }
        return null
    }

    private fun updateWeatherIcon(condition: String, imageView: android.widget.ImageView) {
        imageView.setImageResource(R.drawable.first)
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
