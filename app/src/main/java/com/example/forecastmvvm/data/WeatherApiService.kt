package com.example.forecastmvvm.data

import com.example.forecastmvvm.data.response.CurrentWeatherEntry
import com.example.forecastmvvm.data.response.CurrentWeatherResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY = "bd2246deb64c4fd2b58185121181712"

//http://api.apixu.com/v1/current.json?key=bd2246deb64c4fd2b58185121181712&q=noida
interface WeatherApiService {

    @GET("current.json")
    fun getCurrentWeather(
        @Query("q") location: String,
        @Query("lang") language: String = "en"
    ): Deferred<CurrentWeatherResponse>
    // Deferred = part of coroutines , we can await the call to getCurrentWeather()
    companion object {
        operator fun invoke(): WeatherApiService {
            // add this requestInterceptor to an OKHttp client
            val requestInterceptor = Interceptor {chain ->
                // adding API_KEY by intercepting every request
                val url = chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("key", API_KEY)
                    .build()
                // Building new request with API_KEY
                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()
                return@Interceptor chain.proceed(request)
            }
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .build()
            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://api.apixu.com/v1/")
                .addCallAdapterFactory(CoroutineCallAdapterFactory())   // because deferred courutines were used
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherApiService::class.java)
        }
    }
}