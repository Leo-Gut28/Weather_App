package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    private lateinit var editTextCity: EditText
    private lateinit var buttonSearch: Button
    private lateinit var textViewTemperature: TextView
    private lateinit var layout: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextCity = findViewById(R.id.editTextCity)
        buttonSearch = findViewById(R.id.buttonSearch)
        textViewTemperature = findViewById(R.id.textViewTemperature)
        layout = findViewById(R.id.layout)

        buttonSearch.setOnClickListener {
            val city = editTextCity.text.toString()
            if (city.isNotEmpty()) {
                val weatherApiClient = WeatherApiClient()
                weatherApiClient.setWeatherListener(object : WeatherApiClient.WeatherListener {
                    override fun onWeatherReceived(temperature: Double) {
                        textViewTemperature.text = temperature.toString()
                        updateBackground(temperature)
                    }

                    override fun onWeatherError() {
                        textViewTemperature.text = "Enter valid city"
                    }
                })
                weatherApiClient.execute(city)
            }
        }
    }

    private fun updateBackground(temperature: Double) {
        val backgroundResId = when {
            temperature < 10 -> R.color.cold
            temperature < 15 -> R.color.moderate
            temperature < 20 -> R.color.warm
            temperature < 25 -> R.color.hot
            temperature < 35 -> R.color.extreme
            else -> android.R.color.white
        }
        val backgroundColor = ContextCompat.getColor(this, backgroundResId)
        layout.setBackgroundColor(backgroundColor)
    }
}