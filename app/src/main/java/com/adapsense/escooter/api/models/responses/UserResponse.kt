package com.adapsense.escooter.api.models.responses

import com.adapsense.escooter.api.models.objects.User
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UserResponse: Serializable {

    @SerializedName("success")
    var success = false

    @SerializedName("user")
    var user: User? = null

    @SerializedName("token")
    var token: String? = null

    @SerializedName("error")
    var error: String? = null

}