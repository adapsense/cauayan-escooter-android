package com.adapsense.escooter.api.handlers

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface MessagesHandler {

	@POST("messages")
	fun get(@Header("Content-Type") contentType: String = "application/json", @Body body: JsonObject): Call<ResponseBody?>?

	@PUT("messages")
	fun send(@Header("Content-Type") contentType: String = "application/json", @Body body: JsonObject): Call<ResponseBody?>?

	@PATCH("messages/seen")
	fun seen(@Header("Content-Type") contentType: String = "application/json", @Body body: JsonObject): Call<ResponseBody?>?

}
