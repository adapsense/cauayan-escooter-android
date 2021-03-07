package com.adapsense.escooter.util

import android.content.Context
import android.content.SharedPreferences
import com.adapsense.escooter.api.models.objects.AnalyticsType
import com.adapsense.escooter.api.models.objects.Thread
import com.adapsense.escooter.api.models.objects.Trip
import com.adapsense.escooter.api.models.objects.User
import com.adapsense.escooter.api.models.objects.Vehicle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CacheUtil private constructor(context: Context) {

    companion object {
        @get:Synchronized
        var instance: CacheUtil? = null
            private set
        private const val CACHE = "Cache"
        private const val TOKEN = "Token"
        private const val USER = "User"
        private const val VEHICLE = "Vehicle"
        private const val ANALYTICS_TYPE = "AnalyticsType"
        private const val ANALYTICS_USER = "AnalyticsUser"
        private const val ANALYTICS_VEHICLE = "AnalyticsVehicle"
        private const val ANALYTICS_TRIP = "AnalyticsTrip"
        private const val ANALYTICS_DATE = "AnalyticsDate"
        private const val THREAD = "Thread"
        fun init(context: Context) {
            if (instance == null) {
                instance = CacheUtil(context)
            }
        }

    }

    private val cacheSharedPreferences: SharedPreferences

    init {
        cacheSharedPreferences = context.getSharedPreferences(CACHE, Context.MODE_PRIVATE)
    }

    var token: String?
        get() = cacheSharedPreferences.getString(TOKEN, "")
        set(token) {
            val editor = cacheSharedPreferences.edit()
            editor.putString(TOKEN, token)
            editor.apply()
        }

    var user: User?
        get() {
            val userValue = cacheSharedPreferences.getString(USER, null)
            return if (userValue != null) {
                Gson().fromJson(userValue, object : TypeToken<User?>() {}.type)
            } else null
        }
        set(user) {
            val userValue: String = Gson().toJson(user)
            val editor = cacheSharedPreferences.edit()
            editor.putString(USER, userValue)
            editor.apply()
        }

    var vehicle: Vehicle?
        get() {
            val vehicleValue = cacheSharedPreferences.getString(VEHICLE, null)
            return if (vehicleValue != null) {
                Gson().fromJson(vehicleValue, object : TypeToken<Vehicle?>() {}.type)
            } else null
        }
        set(vehicle) {
            val vehicleValue: String = Gson().toJson(vehicle)
            val editor = cacheSharedPreferences.edit()
            editor.putString(VEHICLE, vehicleValue)
            editor.apply()
        }

    var analyticsType: AnalyticsType?
        get() {
            val analyticsTypeValue = cacheSharedPreferences.getString(ANALYTICS_TYPE, null)
            return if (analyticsTypeValue != null) {
                Gson().fromJson(analyticsTypeValue, object : TypeToken<AnalyticsType?>() {}.type)
            } else null
        }
        set(analyticsType) {
            val analyticsTypeValue: String = Gson().toJson(analyticsType)
            val editor = cacheSharedPreferences.edit()
            editor.putString(ANALYTICS_TYPE, analyticsTypeValue)
            editor.apply()
        }

    var analyticsUser: User?
        get() {
            val analyticsUserValue = cacheSharedPreferences.getString(ANALYTICS_USER, null)
            return if (analyticsUserValue != null) {
                Gson().fromJson(analyticsUserValue, object : TypeToken<User?>() {}.type)
            } else null
        }
        set(analyticsUser) {
            val analyticsUserValue: String = Gson().toJson(analyticsUser)
            val editor = cacheSharedPreferences.edit()
            editor.putString(ANALYTICS_USER, analyticsUserValue)
            editor.apply()
        }

    var analyticsVehicle: Vehicle?
        get() {
            val analyticsVehicleValue = cacheSharedPreferences.getString(ANALYTICS_VEHICLE, null)
            return if (analyticsVehicleValue != null) {
                Gson().fromJson(analyticsVehicleValue, object : TypeToken<Vehicle?>() {}.type)
            } else null
        }
        set(analyticsVehicle) {
            val analyticsVehicleValue: String = Gson().toJson(analyticsVehicle)
            val editor = cacheSharedPreferences.edit()
            editor.putString(ANALYTICS_VEHICLE, analyticsVehicleValue)
            editor.apply()
        }


    var analyticsTrip: Trip?
        get() {
            val analyticsTripValue = cacheSharedPreferences.getString(ANALYTICS_TRIP, null)
            return if (analyticsTripValue != null) {
                Gson().fromJson(analyticsTripValue, object : TypeToken<Trip?>() {}.type)
            } else null
        }
        set(analyticsTrip) {
            val analyticsTripValue: String = Gson().toJson(analyticsTrip)
            val editor = cacheSharedPreferences.edit()
            editor.putString(ANALYTICS_TRIP, analyticsTripValue)
            editor.apply()
        }

    var analyticsDate: String?
        get() = cacheSharedPreferences.getString(ANALYTICS_DATE, null)
        set(analyticsDate) {
            val editor = cacheSharedPreferences.edit()
            editor.putString(ANALYTICS_DATE, analyticsDate)
            editor.apply()
        }

    var thread: Thread?
        get() {
            val threadValue = cacheSharedPreferences.getString(THREAD, null)
            return if (threadValue != null) {
                Gson().fromJson(threadValue, object : TypeToken<Thread?>() {}.type)
            } else null
        }
        set(thread) {
            val threadValue: String = Gson().toJson(thread)
            val editor = cacheSharedPreferences.edit()
            editor.putString(THREAD, threadValue)
            editor.apply()
        }

}