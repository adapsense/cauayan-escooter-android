package com.adapsense.escooter.api.models.responses

import com.adapsense.escooter.api.models.objects.UserStatus
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UserStatusesResponse: Serializable {

    @SerializedName("success")
    var success = false

    @SerializedName("userStatuses")
    var userStatuses: List<UserStatus>? = null

    @SerializedName("error")
    var error: String? = null

}