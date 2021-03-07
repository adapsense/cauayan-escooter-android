package com.adapsense.escooter.api.models.objects

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UserType: Serializable {

    @SerializedName("_id")
    var id = ""

    @SerializedName("name")
    var name = ""

    @SerializedName("isAdmin")
    var isAdmin = true

}