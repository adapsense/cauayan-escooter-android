package com.adapsense.escooter.api.handlers

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ThreadsHandler {

	@GET("threads")
	fun get(@Header("Content-Type") contentType: String = "application/json"): Call<ResponseBody?>?

	@POST("threads")
	fun get(@Header("Content-Type") contentType: String = "application/json", @Body body: JsonObject): Call<ResponseBody?>?

	@PUT("threads")
	fun create(@Header("Content-Type") contentType: String = "application/json", @Body body: JsonObject): Call<ResponseBody?>?

}
