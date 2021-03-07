package com.adapsense.escooter.api.handlers

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface FeedbacksHandler {

	@PUT("feedbacks")
	fun sendWithoutImage(@Header("Content-Type") contentType: String = "application/json", @Body body: JsonObject): Call<ResponseBody?>?

	@Multipart
	@PUT("feedbacks")
	fun sendWithImage(
		@Part("feedback") feedback: RequestBody,
		@Part image: MultipartBody.Part?
	): Call<ResponseBody?>?

}
