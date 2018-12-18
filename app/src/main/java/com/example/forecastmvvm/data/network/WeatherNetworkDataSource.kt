package com.example.forecastmvvm.data.network

import androidx.lifecycle.LiveData
import com.example.forecastmvvm.data.network.response.CurrentWeatherResponse

interface WeatherNetworkDataSource {
    val downloadedCurrentWeather: LiveData<CurrentWeatherResponse>

    // This fun does not return currentweatherresponse but only it updates the LiveData
    // which in turn is observed by the repository
    suspend fun fetchCurrentWeather(
        location: String,
        languageCode: String
    )
}