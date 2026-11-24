package com.example.weather

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChennaiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chennai)

        val tvTemp = findViewById<TextView>(R.id.tv_temp_chennai)
        val tvStatus = findViewById<TextView>(R.id.tv_status_chennai)
        val tvHumidity = findViewById<TextView>(R.id.tv_humidity_chennai)
        val tvWind = findViewById<TextView>(R.id.tv_wind_chennai)
        val tvClouds = findViewById<TextView>(R.id.tv_clouds_chennai)
        val tvPressure = findViewById<TextView>(R.id.tv_pressure_chennai)
        val btnClose = findViewById<ImageView>(R.id.btn_close_chennai)

        btnClose.setOnClickListener { finish() }

        val apiKey = "ab72cbacde30115c358875649800dc6d"
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(WeatherService::class.java)

        service.getWeatherData("Chennai", apiKey).enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weather = response.body()
                    runOnUiThread {
                        tvTemp.text = "${weather?.main?.temp?.toInt()}°C"
                        tvStatus.text = weather?.weather?.get(0)?.description?.replaceFirstChar { it.uppercase() }
                        tvHumidity.text = "${weather?.main?.humidity}%"
                        tvWind.text = "${weather?.wind?.speed} km/h"
                        tvClouds.text = "${weather?.clouds?.all}%"
                        tvPressure.text = "${weather?.main?.pressure} hPa"
                    }
                }
            }
            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Toast.makeText(this@ChennaiActivity, "Error loading data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
