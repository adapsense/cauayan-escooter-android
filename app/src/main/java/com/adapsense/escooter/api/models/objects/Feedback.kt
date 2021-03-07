package com.adapsense.escooter.api.models.objects

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Feedback: Serializable {

    @SerializedName("_id")
    var id = ""

    @SerializedName("admin")
    var user: User? = null

    @SerializedName("content")
    var content = ""

    @SerializedName("image")
    var image = ""

}