package com.adapsense.escooter.api.models.responses

import com.adapsense.escooter.api.models.objects.Message
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class MessagesResponse: Serializable {

    @SerializedName("success")
    var success = false

    @SerializedName("messages")
    var messages: List<Message>? = null

    @SerializedName("error")
    var error: String? = null

}