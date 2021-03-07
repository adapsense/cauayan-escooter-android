package com.adapsense.escooter.api.handlers

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface TripsHandler {

	@GET("trips")
	fun get(): Call<ResponseBody?>?

	@POST("trips")
	fun get(@Header("Content-Type") contentType: String = "application/json", @Body body: JsonObject): Call<ResponseBody?>?

	@PUT("trips")
	fun start(@Header("Content-Type") contentType: String = "application/json", @Body body: JsonObject): Call<ResponseBody?>?

	@PATCH("trips")
	fun end(@Header("Content-Type") contentType: String = "application/json", @Body body: JsonObject): Call<ResponseBody?>?

}
