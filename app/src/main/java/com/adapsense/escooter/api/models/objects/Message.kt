package com.adapsense.escooter.api.models.objects

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Message: Serializable {

    @SerializedName("_id")
    var id = ""

    @SerializedName("sender")
    var sender: User? = null

    @SerializedName("content")
    var content = ""

    @SerializedName("seen")
    var seen: String? = null

    @SerializedName("createdAt")
    var createdAt: String? = null

}