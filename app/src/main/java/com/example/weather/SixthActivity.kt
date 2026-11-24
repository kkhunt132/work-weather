package com.example.weather

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class SixthActivity : AppCompatActivity() {

    private lateinit var tvTemp: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvHumidity: TextView
    private lateinit var tvWind: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvDetails: TextView
    private lateinit var tvPrecip: TextView
    private lateinit var tvSunset: TextView

    // Hourly IDs
    private lateinit var tvH1Time: TextView; private lateinit var tvH1Temp: TextView; private lateinit var ivH1Icon: ImageView
    private lateinit var tvH2Time: TextView; private lateinit var tvH2Temp: TextView; private lateinit var ivH2Icon: ImageView
    private lateinit var tvH3Time: TextView; private lateinit var tvH3Temp: TextView; private lateinit var ivH3Icon: ImageView
    private lateinit var tvH4Time: TextView; private lateinit var tvH4Temp: TextView; private lateinit var ivH4Icon: ImageView

    // Daily IDs
    private lateinit var tvDay1: TextView; private lateinit var tvDay1Temp: TextView
    private lateinit var tvDay2: TextView; private lateinit var tvDay2Temp: TextView
    private lateinit var tvDay3: TextView; private lateinit var tvDay3Temp: TextView

    private val handler = Handler(Looper.getMainLooper())
    private val city = "Chennai"
    private val apiKey = "ab72cbacde30115c358875649800dc6d"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sixth)

        initializeViews()

        findViewById<TextView>(R.id.tv_skip_p6).setOnClickListener {
            startActivity(Intent(this, SeventhActivity::class.java).apply { putExtra("CITY", city) })
        }

        findViewById<TextView>(R.id.tab_forecast_p6).setOnClickListener {
            startActivity(Intent(this, SeventhActivity::class.java).apply { putExtra("CITY", city) })
        }

        findViewById<TextView>(R.id.tab_precipitation_p6).setOnClickListener {
            startActivity(Intent(this, EighthActivity::class.java).apply { putExtra("CITY", city) })
        }

        setupHourlyClicks()

        startLiveUpdates()
    }

    private fun setupHourlyClicks() {
        val mainWeatherImg = findViewById<ImageView>(R.id.img_main_weather_p6)
        val btnH1 = findViewById<android.view.View>(R.id.btn_h1_p6)
        val btnH2 = findViewById<android.view.View>(R.id.btn_h2_p6)
        val btnH3 = findViewById<android.view.View>(R.id.btn_h3_p6)
        val btnH4 = findViewById<android.view.View>(R.id.btn_h4_p6)

        val buttons = listOf(btnH1, btnH2, btnH3, btnH4)
        val timeTexts = listOf(tvH1Time, tvH2Time, tvH3Time, tvH4Time)

        fun updateSelection(selectedIdx: Int) {
            for (i in buttons.indices) {
                if (i == selectedIdx) {
                    // Highlight selected box (Darker blue-grey like the 2nd box)
                    buttons[i].backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4E5464"))
                    timeTexts[i].setTextColor(android.graphics.Color.WHITE)
                    timeTexts[i].setTypeface(null, android.graphics.Typeface.BOLD)
                } else {
                    // Reset others to normal (Dark grey)
                    buttons[i].backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#2D323E"))
                    timeTexts[i].setTextColor(android.graphics.Color.parseColor("#8E94A3"))
                    timeTexts[i].setTypeface(null, android.graphics.Typeface.NORMAL)
                }
            }
        }

        btnH1.setOnClickListener {
            updateSelection(0)
            tvTemp.text = tvH1Temp.text
            tvStatus.text = "Cloudy"
            mainWeatherImg.setImageResource(R.drawable.fifth)
            tvDetails.text = "Hourly Forecast for ${tvH1Time.text} | Temp: ${tvH1Temp.text}"
        }

        btnH2.setOnClickListener {
            updateSelection(1)
            tvTemp.text = tvH2Temp.text
            tvStatus.text = "Partly Cloudy"
            mainWeatherImg.setImageResource(R.drawable.first)
            tvDetails.text = "Hourly Forecast for ${tvH2Time.text} | Temp: ${tvH2Temp.text}"
        }

        btnH3.setOnClickListener {
            updateSelection(2)
            tvTemp.text = tvH3Temp.text
            tvStatus.text = "Partly Cloudy"
            mainWeatherImg.setImageResource(R.drawable.first)
            tvDetails.text = "Hourly Forecast for ${tvH3Time.text} | Temp: ${tvH3Temp.text}"
        }

        btnH4.setOnClickListener {
            updateSelection(3)
            tvTemp.text = tvH4Temp.text
            tvStatus.text = "Sunset Time"
            mainWeatherImg.setImageResource(R.drawable.sunset)
            tvDetails.text = "Hourly Forecast for ${tvH4Time.text} | Temp: ${tvH4Temp.text}"
        }
    }

    private fun initializeViews() {
        tvTemp = findViewById(R.id.tv_temp_p6)
        tvStatus = findViewById(R.id.tv_status_p6)
        tvHumidity = findViewById(R.id.tv_humidity_p6)
        tvWind = findViewById(R.id.tv_wind_grid_p6)
        tvDate = findViewById(R.id.tv_date_p6)
        tvDetails = findViewById(R.id.tv_details_p6)
        tvPrecip = findViewById(R.id.tv_precip_p6)
        tvSunset = findViewById(R.id.tv_sunset_p6)

        tvH1Time = findViewById(R.id.tv_h1_time_p6); tvH1Temp = findViewById(R.id.tv_h1_temp_p6); ivH1Icon = findViewById(R.id.iv_h1_icon_p6)
        tvH2Time = findViewById(R.id.tv_h2_time_p6); tvH2Temp = findViewById(R.id.tv_h2_temp_p6); ivH2Icon = findViewById(R.id.iv_h2_icon_p6)
        tvH3Time = findViewById(R.id.tv_h3_time_p6); tvH3Temp = findViewById(R.id.tv_h3_temp_p6); ivH3Icon = findViewById(R.id.iv_h3_icon_p6)
        tvH4Time = findViewById(R.id.tv_h4_time_p6); tvH4Temp = findViewById(R.id.tv_h4_temp_p6); ivH4Icon = findViewById(R.id.iv_h4_icon_p6)

        tvDay1 = findViewById(R.id.tv_day1_p6); tvDay1Temp = findViewById(R.id.tv_day1_temp_p6)
        tvDay2 = findViewById(R.id.tv_day2_p6); tvDay2Temp = findViewById(R.id.tv_day2_temp_p6)
        tvDay3 = findViewById(R.id.tv_day3_p6); tvDay3Temp = findViewById(R.id.tv_day3_temp_p6)
    }

    private fun startLiveUpdates() {
        handler.post(object : Runnable {
            var updateCount = 0
            override fun run() {
                updateDateTime()
                if (updateCount % 10 == 0) { // Fetch data every 10 seconds
                    fetchCurrentWeather()
                    fetchForecast()
                }
                updateCount++
                handler.postDelayed(this, 1000)
            }
        })
    }

    private fun updateDateTime() {
        val sdf = SimpleDateFormat("EEEE, d MMM | hh:mm:ss a", Locale.getDefault())
        tvDate.text = sdf.format(Date())
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
                        val  currentTempInt = Math.round(it.main.temp).toInt()
                        tvTemp.text = "$currentTempInt°"
                        tvStatus.text = it.weather[0].description.replaceFirstChar { char -> char.uppercase() }
                        val humidity = it.main.humidity
                        tvHumidity.text = "Humidity: $humidity%"
                        tvWind.text = "Wind: ${it.wind.speed} km/h"
                        tvPrecip.text = "Cloudiness: ${it.clouds.all}%"
                        tvDetails.text = "Humidity $humidity%    |    Wind ${it.wind.speed} KM/H"
                        
                        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
                        tvSunset.text = "Sunset: ${sdf.format(Date(it.sys.sunset * 1000))}"
                        
                        tvH2Temp.text = "$currentTempInt°"
                        updateWeatherIcon("", ivH2Icon)
                        findViewById<ImageView>(R.id.img_main_weather_p6).setImageResource(R.drawable.first)
                        
                        // Force Sunset image for the sunset grid item
                        findViewById<ImageView>(R.id.iv_sunset_icon_p6)?.setImageResource(R.drawable.sunset)
                        
                        tvDay1Temp.text = "${Math.round(it.main.temp_max).toInt()}°     ${Math.round(it.main.temp_min).toInt()}°"
                    }
                } else {
                    tvStatus.text = "Offline"
                }
            }
            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                tvStatus.text = "Connection Error"
            }
        })
    }

    private fun fetchForecast() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(WeatherService::class.java)

        service.getForecastData(city, apiKey, "metric").enqueue(object : Callback<ForecastResponse> {
            override fun onResponse(call: Call<ForecastResponse>, response: Response<ForecastResponse>) {
                if (response.isSuccessful) {
                    val list = response.body()?.list ?: return
                    
                    val sdfTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    val cal = Calendar.getInstance()

                    // 1st Card: Past 1 Hour (e.g., 09:00 if NOW is 10:00)
                    cal.time = Date()
                    cal.add(Calendar.HOUR_OF_DAY, -1)
                    tvH1Time.text = sdfTime.format(cal.time)
                    tvH1Temp.text = "${Math.round(list[0].main.temp - 1).toInt()}°"
                    ivH1Icon.setImageResource(R.drawable.fifth)

                    // 2nd Card: NOW (Current Time)
                    tvH2Time.text = "NOW"
                    
                    // 3rd Card: Past 2 Hours (e.g., 08:00)
                    cal.time = Date()
                    cal.add(Calendar.HOUR_OF_DAY, -2)
                    tvH3Time.text = sdfTime.format(cal.time)
                    tvH3Temp.text = "${Math.round(list[0].main.temp - 2).toInt()}°"
                    ivH3Icon.setImageResource(R.drawable.first)

                    // 4th Card: Past 3 Hours (e.g., 07:00)
                    cal.time = Date()
                    cal.add(Calendar.HOUR_OF_DAY, -3)
                    tvH4Time.text = sdfTime.format(cal.time)
                    tvH4Temp.text = "${Math.round(list[0].main.temp - 3).toInt()}°"
                    ivH4Icon.setImageResource(R.drawable.sunset)

                    updateDaily(list[8], tvDay2, tvDay2Temp, "Tomorrow")
                    updateDaily(list[16], tvDay3, tvDay3Temp, "")
                } else {
                    android.widget.Toast.makeText(this@SixthActivity, "Forecast Unreachable", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                android.widget.Toast.makeText(this@SixthActivity, "Check Connection", android.widget.Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateDaily(item: ForecastItem, dayTv: TextView, tempTv: TextView, label: String) {
        if (label.isNotEmpty()) {
            dayTv.text = label
        } else {
            val sdfIn = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val sdfOut = SimpleDateFormat("EEEE", Locale.getDefault())
            val date = sdfIn.parse(item.dt_txt)
            dayTv.text = if (date != null) sdfOut.format(date) else "--"
        }
        tempTv.text = "${Math.round(item.main.temp_max).toInt()}°     ${Math.round(item.main.temp_min).toInt()}°"
        
        val iconId = when(dayTv.id) {
            R.id.tv_day1_p6 -> R.id.img_day1_p6
            R.id.tv_day2_p6 -> R.id.img_day2_p6
            R.id.tv_day3_p6 -> R.id.img_day3_p6
            else -> null
        }
        iconId?.let { updateWeatherIcon(item.weather[0].main, findViewById(it)) }
    }

    private fun updateWeatherIcon(condition: String, imageView: ImageView) {
        imageView.setImageResource(R.drawable.first)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
