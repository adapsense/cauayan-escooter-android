package com.adapsense.escooter.api.models.objects

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Thread: Serializable {

    @SerializedName("_id")
    var id = ""

    @SerializedName("rider")
    var rider: User? = null

    @SerializedName("messages")
    var messages: List<Message>? = null

    @SerializedName("updatedAt")
    var updatedAt: String? = null

}