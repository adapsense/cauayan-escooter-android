package com.adapsense.escooter.api.calls

import com.adapsense.escooter.api.ApiConnector
import com.adapsense.escooter.api.ApiRequestListener
import com.adapsense.escooter.api.handlers.MessagesHandler
import com.adapsense.escooter.api.models.objects.Message
import com.adapsense.escooter.api.models.objects.Thread
import com.adapsense.escooter.api.models.objects.User
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.ResponseBody
import retrofit2.Call

object MessagesApiCall : BaseCall() {
	private val handler: MessagesHandler = ApiConnector.instance!!.createService(MessagesHandler::class.java)

	fun get(user: User, listener: ApiRequestListener?) {
		val threadObject = JsonObject()
		threadObject.addProperty("rider", user.id)
		val body = JsonObject()
		body.add("thread", threadObject)
		val call: Call<ResponseBody?> = handler.get(body = body)!!
		call(call, listener)
	}

	fun send(thread: Thread, message: Message, listener: ApiRequestListener?) {
		val threadObject = JsonObject()
		threadObject.addProperty("_id", thread.id)
		val messageObject = JsonObject()
		messageObject.addProperty("sender", message.sender!!.id)
		messageObject.addProperty("content", message.content)
		val body = JsonObject()
		body.add("thread", threadObject)
		body.add("message", messageObject)
		val call: Call<ResponseBody?> = handler.send(body = body)!!
		call(call, listener)
	}

	fun seen(message: Message, listener: ApiRequestListener?) {
		val body = JsonObject()
		body.add("message", JsonParser.parseString(Gson().toJson(message)))
		val call: Call<ResponseBody?> = handler.seen(body = body)!!
		call(call, listener)
	}

}
