package com.adapsense.escooter.api.models.responses

import com.adapsense.escooter.api.models.objects.User
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UsersResponse: Serializable {

    @SerializedName("success")
    var success = false

    @SerializedName("users")
    var users: List<User>? = null

    @SerializedName("error")
    var error: String? = null

}