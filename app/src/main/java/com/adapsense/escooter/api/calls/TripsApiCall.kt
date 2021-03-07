package com.adapsense.escooter.api.calls

import com.adapsense.escooter.api.ApiConnector
import com.adapsense.escooter.api.ApiRequestListener
import com.adapsense.escooter.api.handlers.TripsHandler
import com.adapsense.escooter.api.models.objects.Trip
import com.adapsense.escooter.api.models.objects.User
import com.adapsense.escooter.api.models.objects.Vehicle
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.ResponseBody
import retrofit2.Call

object TripsApiCall : BaseCall() {
	private val handler: TripsHandler = ApiConnector.instance!!.createService(TripsHandler::class.java)

	fun get(user: User?, listener: ApiRequestListener?) {
		val call: Call<ResponseBody?> = if(user != null) {
			val body = JsonObject()
			body.addProperty("user", user.id)
			handler.get(body = body)!!
		} else {
			handler.get()!!
		}
		call(call, listener)
	}

	fun get(vehicle: Vehicle, listener: ApiRequestListener?) {
		val tripObject = JsonObject()
		tripObject.addProperty("vehicle", vehicle.id)
		val body = JsonObject()
		body.add("trip", tripObject)
		val call: Call<ResponseBody?> = handler.get(body = body)!!
		call(call, listener)
	}

	fun start(trip: Trip, listener: ApiRequestListener?) {

		val tripObject = JsonObject()
		tripObject.addProperty("vehicle", trip.vehicle.id)
		tripObject.add("startLocation", JsonParser.parseString(Gson().toJson(trip.startLocation)))

		val body = JsonObject()
		body.add("trip", tripObject)
		val call: Call<ResponseBody?> = handler.start(body = body)!!
		call(call, listener)
	}

	fun end(trip: Trip, listener: ApiRequestListener?) {
		val tripObject = JsonObject()
		tripObject.addProperty("vehicle", trip.vehicle.id)
		tripObject.add("endLocation", JsonParser.parseString(Gson().toJson(trip.endLocation)))
		val body = JsonObject()
		body.add("trip", tripObject)
		val call: Call<ResponseBody?> = handler.end(body = body)!!
		call(call, listener)
	}

}
