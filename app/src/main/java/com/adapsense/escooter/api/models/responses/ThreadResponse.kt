package com.adapsense.escooter.api.models.responses

import com.adapsense.escooter.api.models.objects.Thread
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ThreadResponse: Serializable {

    @SerializedName("success")
    var success = false

    @SerializedName("thread")
    var thread: Thread? = null

    @SerializedName("error")
    var error: String? = null

}