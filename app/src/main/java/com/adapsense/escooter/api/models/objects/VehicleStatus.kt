package com.adapsense.escooter.api.models.objects

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class VehicleStatus: Serializable {

    @SerializedName("_id")
    var id = ""

    @SerializedName("name")
    var name = ""

    @SerializedName("isAvailable")
    var isAvailable = false

    @SerializedName("isInUse")
    var isInUse = false

    @SerializedName("isDeleted")
    var isDeleted = false

    override fun toString(): String {
        return name
    }

}