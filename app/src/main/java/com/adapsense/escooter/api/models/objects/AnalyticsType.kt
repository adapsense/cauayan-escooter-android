package com.adapsense.escooter.api.models.objects

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class AnalyticsType: Serializable {

    @SerializedName("_id")
    var id = ""

    @SerializedName("name")
    var name = ""

    @SerializedName("description")
    var description = ""

    @SerializedName("url")
    var url = ""

    @SerializedName("isUser")
    var isUser = false

    @SerializedName("isVehicle")
    var isVehicle = false

    @SerializedName("isTrip")
    var isTrip = false

    @SerializedName("isDate")
    var isDate = false

    @SerializedName("isTime")
    var isTime = false

    override fun toString(): String {
        return name
    }

}