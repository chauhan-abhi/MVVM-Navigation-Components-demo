package com.example.forecastmvvm.data.network

import android.content.Context
import android.net.ConnectivityManager
import com.example.forecastmvvm.internal.NoConnectivityException
import okhttp3.Interceptor
import okhttp3.Response

// Context will help in getting system Service
class ConnectivityInterceptorImpl(context: Context) : ConnectivityInterceptor {

    // we need to safely have only appContext(referencing whole app) even after the activity or
    // fragment gets destroyed(component of app)
    private val appContext = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isOnline()) {
            throw NoConnectivityException()
        }
        return chain.proceed(chain.request())
    }

    private fun isOnline(): Boolean {
        val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE)
        as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}