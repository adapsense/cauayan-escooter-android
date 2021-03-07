package com.adapsense.escooter.api.models.responses

import com.adapsense.escooter.api.models.objects.Thread
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ThreadsResponse: Serializable {

    @SerializedName("success")
    var success = false

    @SerializedName("threads")
    var threads: List<Thread>? = null

    @SerializedName("error")
    var error: String? = null

}