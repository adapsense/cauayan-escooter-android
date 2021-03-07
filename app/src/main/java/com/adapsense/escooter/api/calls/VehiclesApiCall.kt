package com.adapsense.escooter.api.calls

import com.adapsense.escooter.api.ApiConnector
import com.adapsense.escooter.api.ApiRequestListener
import com.adapsense.escooter.api.handlers.VehiclesHandler
import com.adapsense.escooter.api.models.objects.User
import com.adapsense.escooter.api.models.objects.Vehicle
import com.adapsense.escooter.api.models.objects.VehicleMessage
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.File

object VehiclesApiCall : BaseCall() {
	private val handler: VehiclesHandler = ApiConnector.instance!!.createService(VehiclesHandler::class.java)

	fun get(listener: ApiRequestListener?) {
		val call: Call<ResponseBody?> = handler.get()!!
		call(call, listener)
	}

	fun search(code: String, listener: ApiRequestListener?) {
		val call: Call<ResponseBody?> = handler.search(code)!!
		call(call, listener)
	}

	fun add(vehicle: Vehicle, listener: ApiRequestListener?) {

		val vehicleObject = JsonObject()
		vehicleObject.addProperty("vehicleType", vehicle.vehicleType.id)
		vehicleObject.addProperty("name", vehicle.name)
		vehicleObject.addProperty("serialNumber", vehicle.serialNumber)
		vehicleObject.addProperty("brand", vehicle.brand)
		vehicleObject.addProperty("model", vehicle.model)

		val body = JsonObject()
		body.add("vehicle", vehicleObject)
		val call: Call<ResponseBody?> = handler.add(body = body)!!
		call(call, listener)
	}

	fun add(vehicle: Vehicle, image: File, listener: ApiRequestListener?) {

		val vehicleObject = JsonObject()
		vehicleObject.addProperty("vehicleType", vehicle.vehicleType.id)
		vehicleObject.addProperty("name", vehicle.name)
		vehicleObject.addProperty("serialNumber", vehicle.serialNumber)
		vehicleObject.addProperty("brand", vehicle.brand)
		vehicleObject.addProperty("model", vehicle.model)

		val vehicleRequestBody = Gson().toJson(vehicleObject).toRequestBody("text/plain".toMediaTypeOrNull())

		val map: HashMap<String, RequestBody> = HashMap()
		map["vehicle"] = vehicleRequestBody

		val imageRequestBody = image.asRequestBody("image/*".toMediaTypeOrNull())
		val imageMultipartBody: MultipartBody.Part = createFormData(
			"image",
			image.name,
			imageRequestBody
		)

		val call: Call<ResponseBody?> = handler.add(
			vehicleRequestBody,
			imageMultipartBody
		)!!
		call(call, listener)
	}

	fun update(vehicle: Vehicle, listener: ApiRequestListener?) {
		val body = JsonObject()
		body.add("vehicle", JsonParser.parseString(Gson().toJson(vehicle)))
		val call: Call<ResponseBody?> = handler.update(body = body)!!
		call(call, listener)
	}

	fun update(vehicle: Vehicle, image: File, listener: ApiRequestListener?) {

		val vehicleRequestBody = Gson().toJson(vehicle).toRequestBody("text/plain".toMediaTypeOrNull())

		val map: HashMap<String, RequestBody> = HashMap()
		map["vehicle"] = vehicleRequestBody

		val imageRequestBody = image.asRequestBody("image/*".toMediaTypeOrNull())
		val imageMultipartBody: MultipartBody.Part = createFormData(
			"image",
			image.name,
			imageRequestBody
		)

		val call: Call<ResponseBody?> = handler.update(
			vehicleRequestBody,
			imageMultipartBody
		)!!
		call(call, listener)
	}

	fun updateRider(user: User, vehicle: Vehicle, listener: ApiRequestListener?) {
		val vehicleObject = JsonObject()
		vehicleObject.addProperty("_id", vehicle.id)
		vehicleObject.addProperty("rider", user.id)
		val body = JsonObject()
		body.add("vehicle", vehicleObject)
		val call: Call<ResponseBody?> = handler.update(body = body)!!
		call(call, listener)
	}

	fun delete(vehicle: Vehicle, listener: ApiRequestListener?) {
		val call: Call<ResponseBody?> = handler.delete(vehicle.id)!!
		call(call, listener)
	}

	fun types(listener: ApiRequestListener?) {
		val call: Call<ResponseBody?> = handler.types()!!
		call(call, listener)
	}

	fun latestLog(vehicle: Vehicle, listener: ApiRequestListener?) {
		val vehicleObject = JsonObject()
		vehicleObject.addProperty("_id", vehicle.id)
		val body = JsonObject()
		body.add("vehicle", vehicleObject)
		val call: Call<ResponseBody?> = handler.latestLog(body = body)!!
		call(call, listener)
	}

	fun latestLogs(vehicles: List<Vehicle>, listener: ApiRequestListener?) {
		val vehiclesArray = JsonArray()
		for(vehicle in vehicles) {
			val vehicleObject = JsonObject()
			vehicleObject.addProperty("_id", vehicle.id)
			vehiclesArray.add(vehicleObject)
		}
		val body = JsonObject()
		body.add("vehicles", vehiclesArray)
		val call: Call<ResponseBody?> = handler.latestLog(body = body)!!
		call(call, listener)
	}

	fun message(vehicleMessage: VehicleMessage, listener: ApiRequestListener?) {
		val vehicleMessageObject = JsonObject()
		vehicleMessageObject.addProperty("user", vehicleMessage.user.id)
		vehicleMessageObject.addProperty("vehicle", vehicleMessage.vehicle.id)
		vehicleMessageObject.addProperty("topic", vehicleMessage.topic)
		vehicleMessageObject.addProperty("payload", vehicleMessage.payload)
		val body = JsonObject()
		body.add("vehicleMessage", vehicleMessageObject)
		val call: Call<ResponseBody?> = handler.message(body = body)!!
		call(call, listener)
	}

	fun statuses(listener: ApiRequestListener?) {
		val call: Call<ResponseBody?> = handler.statuses()!!
		call(call, listener)
	}

}
