package com.adapsense.escooter.api.calls

import com.adapsense.escooter.api.ApiConnector
import com.adapsense.escooter.api.ApiRequestListener
import com.adapsense.escooter.api.handlers.FeedbacksHandler
import com.adapsense.escooter.api.models.objects.Feedback
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.File

object FeedbacksApiCall : BaseCall() {

	private val handler: FeedbacksHandler = ApiConnector.instance!!.createService(FeedbacksHandler::class.java)

	fun sendWithoutImage(feedback: Feedback, listener: ApiRequestListener?) {
		val feedbackObject = JsonObject()
		feedbackObject.addProperty("user", feedback.user!!.id)
		feedbackObject.addProperty("content", feedback.content)
		val body = JsonObject()
		body.add("feedback", feedbackObject)
		val call: Call<ResponseBody?> = handler.sendWithoutImage(body = body)!!
		call(call, listener)
	}

	fun sendWithImage(feedback: Feedback, image: File, listener: ApiRequestListener?) {

		val feedbackObject = JsonObject()
		feedbackObject.addProperty("user", feedback.user!!.id)
		feedbackObject.addProperty("content", feedback.content)

		val feedbackRequestBody = Gson().toJson(feedbackObject).toRequestBody("text/plain".toMediaTypeOrNull())

		val map: HashMap<String, RequestBody> = HashMap()
		map["feedback"] = feedbackRequestBody

		val imageRequestBody = image.asRequestBody("image/*".toMediaTypeOrNull())
		val imageMultipartBody: MultipartBody.Part = createFormData(
			"image",
			image.name,
			imageRequestBody
		)

		val call: Call<ResponseBody?> = handler.sendWithImage(
			feedbackRequestBody,
			imageMultipartBody
		)!!
		call(call, listener)
	}

}
