package com.adapsense.escooter.api.models.responses

import com.adapsense.escooter.api.models.objects.Message
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class MessageResponse: Serializable {

    @SerializedName("success")
    var success = false

    @SerializedName("message")
    var message: Message? = null

    @SerializedName("error")
    var error: String? = null

}