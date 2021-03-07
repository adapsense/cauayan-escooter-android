package com.adapsense.escooter.api.models.objects

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UserStatus: Serializable {

    @SerializedName("_id")
    var id = ""

    @SerializedName("name")
    var name = ""

    @SerializedName("isDeleted")
    var isDeleted = false

    override fun toString(): String {
        return name
    }

}