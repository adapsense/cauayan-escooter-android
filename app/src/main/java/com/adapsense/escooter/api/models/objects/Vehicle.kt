package com.adapsense.escooter.api.models.objects

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Vehicle: Serializable {

    @SerializedName("_id")
    var id = ""

    @SerializedName("admin")
    var admin: User? = null

    @SerializedName("rider")
    var rider: User? = null

    @SerializedName("vehicleType")
    var vehicleType = VehicleType()

    @SerializedName("vehicleStatus")
    var vehicleStatus = VehicleStatus()

    @SerializedName("name")
    var name = ""

    @SerializedName("label")
    var label = ""

    @SerializedName("code")
    var code = ""

    @SerializedName("qr")
    var qr = ""

    @SerializedName("topic")
    var topic = ""

    @SerializedName("serialNumber")
    var serialNumber = ""

    @SerializedName("brand")
    var brand = ""

    @SerializedName("model")
    var model = ""

    @SerializedName("image")
    var image = ""

    override fun toString(): String {
        return name
    }

}