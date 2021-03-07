package com.adapsense.escooter.api.calls

import com.adapsense.escooter.api.ApiConnector
import com.adapsense.escooter.api.ApiRequestListener
import com.adapsense.escooter.api.handlers.AnalyticsHandler
import com.adapsense.escooter.api.models.objects.AnalyticsType
import com.adapsense.escooter.api.models.objects.Trip
import com.adapsense.escooter.api.models.objects.User
import com.adapsense.escooter.api.models.objects.Vehicle
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.ResponseBody
import retrofit2.Call

object AnalyticsApiCall : BaseCall() {
	private val handler: AnalyticsHandler = ApiConnector.instance!!.createService(AnalyticsHandler::class.java)
	
	fun types(listener: ApiRequestListener?) {
		val call: Call<ResponseBody?> = handler.types()!!
		call(call, listener)
	}

	fun vehicleLog(analyticsType: AnalyticsType, vehicle: Vehicle, date: String, listener: ApiRequestListener?) {
		val body = JsonObject()
		body.add("vehicle", JsonParser.parseString(Gson().toJson(vehicle)))
		body.addProperty("date", date)
		val call: Call<ResponseBody?> = handler.get(url = analyticsType.url, body = body)!!
		call(call, listener)
	}

	fun trip(analyticsType: AnalyticsType, trip: Trip, listener: ApiRequestListener?) {
		val body = JsonObject()
		body.add("trip", JsonParser.parseString(Gson().toJson(trip)))
		val call: Call<ResponseBody?> = handler.get(url = analyticsType.url, body = body)!!
		call(call, listener)
	}

}
