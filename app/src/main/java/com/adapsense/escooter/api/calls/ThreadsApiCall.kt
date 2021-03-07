package com.adapsense.escooter.api.calls

import com.adapsense.escooter.api.ApiConnector
import com.adapsense.escooter.api.ApiRequestListener
import com.adapsense.escooter.api.handlers.ThreadsHandler
import com.adapsense.escooter.api.models.objects.Thread
import com.adapsense.escooter.api.models.objects.User
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call

object ThreadsApiCall : BaseCall() {
	private val handler: ThreadsHandler = ApiConnector.instance!!.createService(ThreadsHandler::class.java)

	fun get(listener: ApiRequestListener?) {
		val call: Call<ResponseBody?> = handler.get()!!
		call(call, listener)
	}

	fun get(user: User, listener: ApiRequestListener?) {
		val threadObject = JsonObject()
		threadObject.addProperty("rider", user.id)
		val body = JsonObject()
		body.add("thread", threadObject)

		val call: Call<ResponseBody?> = handler.get(body = body)!!
		call(call, listener)
	}

	fun get(thread: Thread, listener: ApiRequestListener?) {
		val threadObject = JsonObject()
		threadObject.addProperty("_id", thread.id)
		val body = JsonObject()
		body.add("thread", threadObject)
		val call: Call<ResponseBody?> = handler.get(body = body)!!
		call(call, listener)
	}

	fun create(user: User, listener: ApiRequestListener?) {
		val threadObject = JsonObject()
		threadObject.addProperty("rider", user.id)
		val body = JsonObject()
		body.add("thread", threadObject)
		val call: Call<ResponseBody?> = handler.create(body = body)!!
		call(call, listener)
	}

}
