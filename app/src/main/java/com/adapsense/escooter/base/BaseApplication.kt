package com.adapsense.escooter.base

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.adapsense.escooter.api.ApiConnector
import com.adapsense.escooter.util.CacheUtil

class BaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        ApiConnector.init()
        CacheUtil.init(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}