package com.adapsense.escooter.api.handlers

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface UsersHandler {

	@GET("users")
	fun get(): Call<ResponseBody?>?

	@POST("users/search")
	fun search(@Header("Content-Type") contentType: String = "application/json", @Body body: JsonObject): Call<ResponseBody?>?

	@PUT("users/signup")
	fun signup(@Header("Content-Type") contentType: String = "application/json", @Query("isAdmin") isAdmin: Boolean, @Body body: JsonObject): Call<ResponseBody?>?

	@POST("users/signin")
	fun signin(@Header("Content-Type") contentType: String = "application/json", @Query("isAdmin") isAdmin: Boolean, @Query("isMobile") isMobile: Boolean, @Body body: JsonObject): Call<ResponseBody?>?

	@PATCH("users")
	fun update(@Header("Content-Type") contentType: String = "application/json", @Body body: JsonObject): Call<ResponseBody?>?

	@PATCH("users/changePassword")
	fun changePassword(@Header("Content-Type") contentType: String = "application/json", @Body body: JsonObject): Call<ResponseBody?>?

	@DELETE("users")
	fun delete(@Query("id") id: String): Call<ResponseBody?>?

	@GET("userStatuses")
	fun statuses(): Call<ResponseBody?>?

}
