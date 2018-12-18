package com.example.forecastmvvm

import android.app.Application
import com.example.forecastmvvm.data.db.ForecastDatabase
import com.example.forecastmvvm.data.network.*
import com.example.forecastmvvm.data.repository.ForecastRepository
import com.example.forecastmvvm.data.repository.ForecastRepositoryImpl
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

class ForecastApplication: Application(), KodeinAware {
    override val kodein: Kodein = Kodein.lazy {
        // This will provide us instances of context, various services related to Android
        import(androidXModule(this@ForecastApplication))
        // Do not want multiple instances of ForecastDatabase at same time
        // To call invoke context must be passed which is passed by instance returned by AndroidX module
        bind() from singleton { ForecastDatabase(instance()) }
        // CurrentWeatherDao
        bind() from singleton { instance<ForecastDatabase>().currentWeatherDao() }
        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance()) }
        // WeatherApiService
        bind() from singleton { WeatherApiService(instance()) }
        // WeatherNetworkDataSourceImpl
        bind<WeatherNetworkDataSource>() with singleton { WeatherNetworkDataSourceImpl(instance()) }
        // ForecastRepositoryImpl
        bind<ForecastRepository>() with singleton { ForecastRepositoryImpl(instance(), instance( )) }


    }

}