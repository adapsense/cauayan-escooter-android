package com.adapsense.escooter.api.models.responses

import com.adapsense.escooter.api.models.objects.Feedback
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class FeedbackResponse: Serializable {

    @SerializedName("success")
    var success = false

    @SerializedName("feedback")
    var feedback: Feedback? = null

    @SerializedName("error")
    var error: String? = null

}