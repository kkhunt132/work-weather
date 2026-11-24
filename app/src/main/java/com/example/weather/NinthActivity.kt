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

class NinthActivity : AppCompatActivity() {

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
    private val city = "Delhi"
    private val apiKey = "ab72cbacde30115c358875649800dc6d"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ninth)

        initializeViews()

        findViewById<TextView>(R.id.tv_skip_p9).setOnClickListener {
            startActivity(Intent(this, TenthActivity::class.java).apply {
                putExtra("CITY", city)
            })
        }

        findViewById<TextView>(R.id.tab_forecast_p9).setOnClickListener {
            startActivity(Intent(this, SeventhActivity::class.java).apply {
                putExtra("CITY", city)
            })
        }

        findViewById<TextView>(R.id.tab_precipitation_p9).setOnClickListener {
            startActivity(Intent(this, EighthActivity::class.java).apply {
                putExtra("CITY", city)
            })
        }

        startLiveUpdates()
    }

    private fun initializeViews() {
        tvTemp = findViewById(R.id.tv_temp_p9)
        tvStatus = findViewById(R.id.tv_status_p9)
        tvHumidity = findViewById(R.id.tv_humidity_p9)
        tvWind = findViewById(R.id.tv_wind_grid_p9)
        tvDate = findViewById(R.id.tv_date_p9)
        tvDetails = findViewById(R.id.tv_details_p9)
        tvPrecip = findViewById(R.id.tv_precip_p9)
        tvSunset = findViewById(R.id.tv_sunset_p9)

        tvH1Time = findViewById(R.id.tv_h1_time_p9); tvH1Temp = findViewById(R.id.tv_h1_temp_p9); ivH1Icon = findViewById(R.id.iv_h1_icon_p9)
        tvH2Time = findViewById(R.id.tv_h2_time_p9); tvH2Temp = findViewById(R.id.tv_h2_temp_p9); ivH2Icon = findViewById(R.id.iv_h2_icon_p9)
        tvH3Time = findViewById(R.id.tv_h3_time_p9); tvH3Temp = findViewById(R.id.tv_h3_temp_p9); ivH3Icon = findViewById(R.id.iv_h3_icon_p9)
        tvH4Time = findViewById(R.id.tv_h4_time_p9); tvH4Temp = findViewById(R.id.tv_h4_temp_p9); ivH4Icon = findViewById(R.id.iv_h4_icon_p9)

        tvDay1 = findViewById(R.id.tv_day1_p9); tvDay1Temp = findViewById(R.id.tv_day1_temp_p9)
        tvDay2 = findViewById(R.id.tv_day2_p9); tvDay2Temp = findViewById(R.id.tv_day2_temp_p9)
        tvDay3 = findViewById(R.id.tv_day3_p9); tvDay3Temp = findViewById(R.id.tv_day3_temp_p9)
    }

    private fun startLiveUpdates() {
        handler.post(object : Runnable {
            var updateCount = 0
            override fun run() {
                updateDateTime()
                // Fetch data every 10 seconds (if updateCount % 10 == 0)
                // But let's make sure it fetches immediately on first run
                if (updateCount % 10 == 0) {
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
                    response.body()?.let { weatherData ->
                        val currentTempInt = Math.round(weatherData.main.temp).toInt() // Rounding for accuracy
                        tvTemp.text = "$currentTempInt°"
                        tvStatus.text = weatherData.weather[0].description.replaceFirstChar { it.uppercase() }
                        tvHumidity.text = "Humidity: ${weatherData.main.humidity}%"
                        tvWind.text = "Wind: ${weatherData.wind.speed} km/h"
                        tvPrecip.text = "Cloudiness: ${weatherData.clouds.all}%"
                        tvDetails.text = "Humidity ${weatherData.main.humidity}%    |    Wind ${weatherData.wind.speed} KM/H"
                        
                        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
                        tvSunset.text = "Sunset: ${sdf.format(Date(weatherData.sys.sunset * 1000))}"

                        tvH2Temp.text = "$currentTempInt°"
                        updateWeatherIcon("", ivH2Icon)
                        updateWeatherIcon("", findViewById(R.id.img_main_weather_p9))
                        
                        tvDay1Temp.text = "${Math.round(weatherData.main.temp_max).toInt()}°     ${Math.round(weatherData.main.temp_min).toInt()}°"
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

                    // 1st Card: Past 1 Hour
                    cal.time = Date()
                    cal.add(Calendar.HOUR_OF_DAY, -1)
                    tvH1Time.text = sdfTime.format(cal.time)
                    tvH1Temp.text = "${Math.round(list[0].main.temp - 1).toInt()}°"
                    ivH1Icon.setImageResource(R.drawable.fifth)

                    // 2nd Card: NOW
                    tvH2Time.text = "NOW"
                    
                    // 3rd Card: Past 2 Hours
                    cal.time = Date()
                    cal.add(Calendar.HOUR_OF_DAY, -2)
                    tvH3Time.text = sdfTime.format(cal.time)
                    tvH3Temp.text = "${Math.round(list[0].main.temp - 2).toInt()}°"
                    ivH3Icon.setImageResource(R.drawable.first)

                    // 4th Card: Past 3 Hours
                    cal.time = Date()
                    cal.add(Calendar.HOUR_OF_DAY, -3)
                    tvH4Time.text = sdfTime.format(cal.time)
                    tvH4Temp.text = "${Math.round(list[0].main.temp - 3).toInt()}°"
                    ivH4Icon.setImageResource(R.drawable.sunset)

                    updateDaily(list[8], tvDay2, tvDay2Temp, "Tomorrow")
                    updateDaily(list[16], tvDay3, tvDay3Temp, "")
                } else {
                    android.widget.Toast.makeText(this@NinthActivity, "Forecast Unreachable", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                android.widget.Toast.makeText(this@NinthActivity, "Check Connection", android.widget.Toast.LENGTH_SHORT).show()
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
            R.id.tv_day1_p9 -> R.id.img_day1_p9
            R.id.tv_day2_p9 -> R.id.img_day2_p9
            R.id.tv_day3_p9 -> R.id.img_day3_p9
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
