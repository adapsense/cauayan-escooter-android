package com.adapsense.escooter.api.handlers

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface VehiclesHandler {

	@GET("vehicles")
	fun get(): Call<ResponseBody?>?

	@GET("vehicles/search")
	fun search(@Query("code") code: String): Call<ResponseBody?>?

	@PUT("vehicles")
	fun add(@Header("Content-Type") contentType: String = "application/json", @Body body: JsonObject): Call<ResponseBody?>?

	@Multipart
	@PUT("vehicles")
	fun add(
		@Part("vehicle") vehicle: RequestBody,
		@Part image: MultipartBody.Part?
	): Call<ResponseBody?>?

	@PATCH("vehicles")
	fun update(@Header("Content-Type") contentType: String = "application/json", @Body body: JsonObject): Call<ResponseBody?>?

	@Multipart
	@PATCH("vehicles")
	fun update(
		@Part("vehicle") vehicle: RequestBody,
		@Part image: MultipartBody.Part?
	): Call<ResponseBody?>?

	@DELETE("vehicles")
	fun delete(@Query("id") id: String): Call<ResponseBody?>?

	@GET("vehicleTypes")
	fun types(): Call<ResponseBody?>?

	@POST("vehicleLogs/latest")
	fun latestLog(@Header("Content-Type") contentType: String = "application/json", @Body body: JsonObject): Call<ResponseBody?>?

	@PUT("vehicleMessages")
	fun message(@Header("Content-Type") contentType: String = "application/json", @Body body: JsonObject): Call<ResponseBody?>?

	@GET("vehicleStatuses")
	fun statuses(): Call<ResponseBody?>?

}
