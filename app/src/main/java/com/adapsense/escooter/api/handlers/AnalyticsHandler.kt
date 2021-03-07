package com.adapsense.escooter.api.handlers

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface AnalyticsHandler {

	@POST("analytics/{url}")
	fun get(@Header("Content-Type") contentType: String = "application/json", @Path(value = "url", encoded = true) url: String, @Body body: JsonObject): Call<ResponseBody?>?

	@GET("analyticsTypes")
	fun types(): Call<ResponseBody?>?

}
