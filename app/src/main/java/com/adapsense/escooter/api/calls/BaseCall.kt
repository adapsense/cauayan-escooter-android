package com.adapsense.escooter.api.calls

import com.adapsense.escooter.api.ApiRequestListener
import com.adapsense.escooter.util.AppLogger
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

open class BaseCall {

	private var call: Call<ResponseBody?>? = null

	protected fun call(call: Call<ResponseBody?>?, listener: ApiRequestListener?) {
		this.call = call
		this.call?.enqueue(object : Callback<ResponseBody?> {

			override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
				try {
					val serverResponse: String?
					val jsonObject: JSONObject
					if (response.isSuccessful) {
						if (response.body() != null) {
							serverResponse = (response.body() as ResponseBody).string()
							jsonObject = JSONObject(serverResponse)
							AppLogger.printJson(jsonObject)
							listener?.onSuccess(jsonObject)
						} else {
							listener?.onSuccess("")
						}
					} else {
						serverResponse = response.errorBody()?.string()
						jsonObject = JSONObject(serverResponse!!)
						listener?.onError(jsonObject)
					}
				} catch (var5: JSONException) {
					var5.printStackTrace()
					listener?.onError(var5)
				} catch (var5: IOException) {
					var5.printStackTrace()
					listener?.onError(var5)
				}
			}

			override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
				t.printStackTrace()
				listener?.onError(t)
			}
		})
	}

	fun forceStop() {
		if (call != null && call?.isExecuted!!) {
			call!!.cancel()
		}
	}
}
