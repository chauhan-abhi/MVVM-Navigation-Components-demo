package com.example.forecastmvvm.data.repository

import androidx.lifecycle.LiveData
import com.example.forecastmvvm.data.db.unitlocalised.UnitSpecificCurrentWeatherEntry

interface ForecastRepository {
    // why suspend : 1 --> we want the function to be async
    // 2--> call this from a coroutine
    suspend fun getCurrentWeather(metric: Boolean): LiveData<out UnitSpecificCurrentWeatherEntry>
}