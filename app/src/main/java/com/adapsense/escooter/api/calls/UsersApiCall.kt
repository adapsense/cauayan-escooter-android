package com.adapsense.escooter.api.calls

import com.adapsense.escooter.api.ApiConnector
import com.adapsense.escooter.api.ApiRequestListener
import com.adapsense.escooter.api.handlers.UsersHandler
import com.adapsense.escooter.api.models.objects.User
import com.adapsense.escooter.api.models.objects.Vehicle
import com.google.gson.*
import okhttp3.ResponseBody
import retrofit2.Call

object UsersApiCall : BaseCall() {
	private val handler: UsersHandler = ApiConnector.instance!!.createService(UsersHandler::class.java)

	fun get(listener: ApiRequestListener?) {
		val call: Call<ResponseBody?> = handler.get()!!
		call(call, listener)
	}

	fun search(user: User, listener: ApiRequestListener?) {
		val userBody = JsonObject()
		userBody.addProperty("email", user.email)
		val body = JsonObject()
		body.add("user", userBody)
		val call: Call<ResponseBody?> = handler.search(body = body)!!
		call(call, listener)
	}

	fun signupRider(user: User, vehicle: Vehicle, listener: ApiRequestListener?) {

		val userBody = JsonObject()
		userBody.addProperty("fullName", user.fullName)
		userBody.addProperty("email", user.email)
		userBody.addProperty("password", user.password)

		val vehicleBody = JsonObject()
		vehicleBody.addProperty("_id", vehicle.id)
		val userGroupBody = JsonObject()
		userGroupBody.addProperty("_id", vehicle.admin!!.userGroup.id)
		val adminBody = JsonObject()
		adminBody.add("userGroup", userGroupBody)
		vehicleBody.add("admin", adminBody)

		val body = JsonObject()
		body.add("user", userBody)
		body.add("vehicle", vehicleBody)
		val call: Call<ResponseBody?> = handler.signup(isAdmin = false, body = body)!!
		call(call, listener)
	}

	fun signinRider(user: User, vehicle: Vehicle, listener: ApiRequestListener?) {

		val userBody = JsonObject()
		userBody.addProperty("email", user.email)
		userBody.addProperty("password", user.password)

		val vehicleBody = JsonObject()
		vehicleBody.addProperty("_id", vehicle.id)

		val body = JsonObject()
		body.add("user", userBody)
		body.add("vehicle", vehicleBody)
		val call: Call<ResponseBody?> = handler.signin(isAdmin = false, isMobile = true, body = body)!!
		call(call, listener)
	}

	fun signinAdmin(user: User, listener: ApiRequestListener?) {

		val userBody = JsonObject()
		userBody.addProperty("email", user.email)
		userBody.addProperty("password", user.password)

		val body = JsonObject()
		body.add("user", userBody)
		val call: Call<ResponseBody?> = handler.signin( isAdmin = true, isMobile = true, body = body)!!
		call(call, listener)
	}

	fun update(user: User, listener: ApiRequestListener?) {

		val userBody = JsonObject()
		userBody.addProperty("_id", user.id)
		userBody.addProperty("fullName", user.fullName)
		userBody.addProperty("userStatus", user.userStatus.id)

		val body = JsonObject()
		body.add("user", userBody)
		val call: Call<ResponseBody?> = handler.update(body = body)!!
		call(call, listener)
	}

	fun changePassword(user: User, oldPassword: String, newPassword: String, listener: ApiRequestListener?) {

		val userBody = JsonObject()
		userBody.addProperty("_id", user.id)
		userBody.addProperty("oldPassword", oldPassword)
		userBody.addProperty("newPassword", newPassword)

		val body = JsonObject()
		body.add("user", userBody)
		val call: Call<ResponseBody?> = handler.changePassword(body = body)!!
		call(call, listener)
	}

	fun delete(user: User, listener: ApiRequestListener?) {
		val call: Call<ResponseBody?> = handler.delete(user.id)!!
		call(call, listener)
	}

	fun statuses(listener: ApiRequestListener?) {
		val call: Call<ResponseBody?> = handler.statuses()!!
		call(call, listener)
	}

}
