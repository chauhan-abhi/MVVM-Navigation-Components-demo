package com.example.forecastmvvm.data.repository

import androidx.lifecycle.LiveData
import com.example.forecastmvvm.data.db.CurrentWeatherDao
import com.example.forecastmvvm.data.db.unitlocalised.UnitSpecificCurrentWeatherEntry
import com.example.forecastmvvm.data.network.WeatherNetworkDataSource
import com.example.forecastmvvm.data.network.response.CurrentWeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.ZonedDateTime
import java.util.*

class ForecastRepositoryImpl(
    private val currentWeatherDao: CurrentWeatherDao,
    private val weatherNetworkDataSource: WeatherNetworkDataSource
) : ForecastRepository {

    init {
        // Repositories do not have a lifecycle so we don't worry of the lifecycle being destroyed
        // This is to persist the new value of downloadedCurrentWeather in local DB
        weatherNetworkDataSource.downloadedCurrentWeather.observeForever { newCurrentWeather ->
            //persist
            persistFetchedCurrentWeather(newCurrentWeather)
        }
    }

    // THis out keyword basically tells the generics that we can also return the classes which only
    // implement UnitSpecificCurrentWeatherEntry and not only direct UnitSpecificCurrentWeatherEntry
    override suspend fun getCurrentWeather(metric: Boolean): LiveData<out UnitSpecificCurrentWeatherEntry> {
        // withContext returns a value , launch returns a Job
        // here we are returning whats already in local DB
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext if (metric) currentWeatherDao.getWeatherMetric()
            else currentWeatherDao.getWeatherImperial()
        }
    }

    // actually persist the weather
    private fun persistFetchedCurrentWeather(fetchedWeather: CurrentWeatherResponse) {
        // use Global Scope to launch a coroutine and is perfext here and not in fragment because
        // repositories has not lifecycle whereas a Fragment has and an exception can occur if it goes
        // out of lifecycle and GlobalScope launch result comes after it
        GlobalScope.launch(Dispatchers.IO) {
            currentWeatherDao.upsert(fetchedWeather.currentWeatherEntry)
        }
    }

    private suspend fun initWeatherData() {
        if (isFetchedCurrentNeeded(ZonedDateTime.now().minusHours(1))) {
            fetchCurrentWeather()
        }
    }

    private suspend fun fetchCurrentWeather(){
        weatherNetworkDataSource.fetchCurrentWeather(
            "Lucknow",
            Locale.getDefault().language
        )
        // no return here because the LiveData is being observed in init block
    }

    private fun isFetchedCurrentNeeded(lastFetchItem:ZonedDateTime): Boolean {
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchItem.isBefore(thirtyMinutesAgo)
    }
}