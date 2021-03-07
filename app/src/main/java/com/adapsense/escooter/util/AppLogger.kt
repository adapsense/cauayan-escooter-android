package com.adapsense.escooter.util

import android.util.Log
import com.adapsense.escooter.BuildConfig
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object AppLogger {

    private const val TAG = "eScooter"

    fun info(tag: String?, string: String?) {
        if (BuildConfig.DEBUG) {
            if (!string.isNullOrEmpty()) {
                if (string.length > 4000) {
                    Log.i(tag, string.substring(0, 4000))
                    info(tag, string.substring(4000))
                } else {
                    Log.i(tag, string)
                }
            } else {
                Log.i(TAG, "Nothing to log")
            }
        }
    }

    fun info(string: String?) {
        if (BuildConfig.DEBUG) {
            if (!string.isNullOrEmpty()) {
                Log.i(TAG, string)
            } else {
                Log.i(TAG, "Nothing to log")
            }
        }
    }

    fun error(tag: String?,string: String?) {
        if (BuildConfig.DEBUG) {
            if (!string.isNullOrEmpty()) {
                Log.e(tag, string)
            } else {
                Log.e(tag, "Nothing to log")
            }
        }
    }

    fun error(string: String?) {
        if (BuildConfig.DEBUG) {
            if (!string.isNullOrEmpty()) {
                Log.e(TAG, string)
            } else {
                Log.e(TAG, "Nothing to log")
            }
        }
    }

    fun warn(string: String?) {
        if (BuildConfig.DEBUG) {
            if (!string.isNullOrEmpty()) {
                Log.w(TAG, string)
            } else {
                Log.w(TAG, "Nothing to log")
            }
        }
    }

    fun longInfo(tag: String?, msg: String?) {
        if (!msg.isNullOrEmpty()) {
            if (msg.length > 4000) {
                Log.i(tag, msg.substring(0, 4000))
                longInfo(tag, msg.substring(4000))
            } else {
                Log.i(tag, msg)
            }
        } else {
            Log.i(TAG,"LongInfo msg is empty")
        }
    }

    fun longInfo(sb: String) {
        if (sb.length > 4000) {
            Log.v(TAG, "sb.length = " + sb.length)
            val chunkCount: Int = sb.length / 4000 // integer division
            for (i in 0..chunkCount) {
                val max = 4000 * (i + 1)
                if (max >= sb.length) {
                    Log.v(TAG, sb.substring(4000 * i))
                } else {
                    Log.v(TAG, sb.substring(4000 * i, max))
                }
            }
        }else{
            Log.v(TAG,sb)
        }
    }

    fun longError(sb: String) {
        if (sb.length > 4000) {
           longError(TAG, "sb.length = " + sb.length)
            val chunkCount: Int = sb.length / 4000 // integer division
            for (i in 0..chunkCount) {
                val max = 4000 * (i + 1)
                if (max >= sb.length) {
                   longError(TAG, sb.substring(4000 * i))
                } else {
                    longError(TAG, sb.substring(4000 * i, max))
                }
            }
        }else{
            error(sb)
        }
    }

    fun longError(tag: String, sb: String) {
        if (sb.length > 4000) {
           longError(tag, "sb.length = " + sb.length)
            val chunkCount: Int = sb.length / 4000 // integer division
            for (i in 0..chunkCount) {
                val max = 4000 * (i + 1)
                if (max >= sb.length) {
                   longError(tag, sb.substring(4000 * i))
                } else {
                   longError(tag, sb.substring(4000 * i, max))
                }
            }
        }else{
            error(tag,sb)
        }
    }

    fun longWarn(msg: String?) {
        if (!msg.isNullOrEmpty()) {
            if (msg.length > 4000) {
                warn(msg.substring(0, 4000))
                longWarn(msg.substring(4000))
            } else {
                warn(msg)
            }
        } else {
            error("LongWarn msg is empty")
        }
    }

    fun prettyPrint(`object`: JSONObject) {
        try {
            val text = `object`.toString(2)
            val temp = text.split("\n").toTypedArray()
            for (s in temp) {
                info(s)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun printJSONObject(tag: String?, `object`: JSONObject) {
        if (BuildConfig.DEBUG) {
            try {
                val text = `object`.toString(2)
                val temp = text.split("\n").toTypedArray()
                for (s in temp) {
                    Log.i(tag, s)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                printJson(`object`)
            }
        }
    }

    fun printJson(`object`: Any?) {
        if (`object` is JSONObject) {
            try {
                longInfo(`object`.toString(2))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else if (`object` is JSONArray) {
            try {
                longInfo(`object`.toString(2))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else if (`object` != null) {
            longInfo(`object`.toString())
        } else {
            error("Nothing to Log")
        }
    }

    fun printRetrofitError(error: Any?) {
        if (error is JSONObject) {
            try {
                longError(error.toString(2))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else if (error is Exception) {
            if (BuildConfig.DEBUG) {
                error("Exception Stacktrace")
                error.printStackTrace()
            }
        } else if (error is Throwable) {
            if (BuildConfig.DEBUG) {
                error("Throwable Stacktrace")
                error.printStackTrace()
            }
        } else if (error != null) {
            longError(error.toString())
        } else {
            error("Nothing to log")
        }
    }

    fun printMap(map: Map<String, String>) {
        for (key in map.keys) {
            longInfo(key + ": " + map[key])
        }
    }
}
