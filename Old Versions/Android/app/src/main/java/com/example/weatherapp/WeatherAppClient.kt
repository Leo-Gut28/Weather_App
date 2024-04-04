package com.example.weatherapp
import android.os.AsyncTask
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class WeatherApiClient : AsyncTask<String, Void, String>() {

    private val API_KEY = "7924bf31d9284f72b10172357231006"
    private val API_BASE_URL = "https://api.weatherapi.com/v1/current.json?key=$API_KEY"

    private var listener: WeatherListener? = null

    fun setWeatherListener(listener: WeatherListener) {
        this.listener = listener
    }

    override fun doInBackground(vararg params: String): String? {
        val city = params[0]
        val apiUrl = "$API_BASE_URL&q=$city"

        try {
            val url = URL(apiUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream: InputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                return response.toString()
            } else {
                Log.e("WeatherApiClient", "API request failed with response code: $responseCode")
            }
        } catch (e: IOException) {
            Log.e("WeatherApiClient", "Error occurred during API request: ${e.message}")
        }

        return null
    }

    override fun onPostExecute(result: String?) {
        listener?.let {
            if (result != null) {
                try {
                    val jsonObject = JSONObject(result)
                    val temperature = jsonObject.getJSONObject("current").getDouble("temp_c")
                    it.onWeatherReceived(temperature)
                } catch (e: JSONException) {
                    Log.e("WeatherApiClient", "Error parsing JSON response: ${e.message}")
                }
            } else {
                it.onWeatherError()
            }
        }
    }

    interface WeatherListener {
        fun onWeatherReceived(temperature: Double)
        fun onWeatherError()
    }
}