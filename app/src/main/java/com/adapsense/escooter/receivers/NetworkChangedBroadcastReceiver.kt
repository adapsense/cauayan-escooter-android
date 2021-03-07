package com.adapsense.escooter.receivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.adapsense.escooter.util.AppLogger

class NetworkChangedBroadcastReceiver: BroadcastReceiver() {

    interface OnNetworkChangedCallback {
        fun onNetworkChanged()
    }

    var callback: OnNetworkChangedCallback ? = null

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {

        if(callback != null) {
            callback!!.onNetworkChanged()
        }
    }

}