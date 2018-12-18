package com.example.forecastmvvm.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.forecastmvvm.data.db.entity.CurrentWeatherEntry

@Database(
    entities = [CurrentWeatherEntry::class],
    version = 1
)
abstract class ForecastDatabase : RoomDatabase() {
    abstract fun currentWeatherDao() : CurrentWeatherDao

    // Database needs to be singleton,
    // no sense in having multiple instances of database at same time
    companion object {
        @Volatile private var instance: ForecastDatabase? = null
        // LOCK is a dummy object to make sure no two threads are
        // currently doing the same thing working in parallel
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            // also executes after successful completion of buildDatabase function
            // and initializing instance with the
            // it{ return value from the function }
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext, // Application context
                    ForecastDatabase::class.java,                // Database class
                    "forecast.db")                         // Database name
                    .build()
    }

}