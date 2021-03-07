package com.adapsense.escooter.api.models.responses

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ErrorResponse: Serializable {

    @SerializedName("success")
    var success = false

    @SerializedName("error")
    var error: String? = null

}