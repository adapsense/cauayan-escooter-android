package com.adapsense.escooter.api.models.objects

import com.google.gson.annotations.SerializedName

class VehicleMessage {

    @SerializedName("_id")
    var id = ""

    @SerializedName("user")
    var user = User()

    @SerializedName("vehicle")
    var vehicle = Vehicle()

    @SerializedName("topic")
    var topic = ""

    @SerializedName("payload")
    var payload = ""

}