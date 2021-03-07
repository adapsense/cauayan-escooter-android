package com.adapsense.escooter.api.models.objects

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class VehicleLog: Serializable {

    @SerializedName("_id")
    var id = ""

    @SerializedName("vehicle")
    var vehicle = Vehicle()

    @SerializedName("time")
    var time = "\\u2014"

    @SerializedName("lat")
    var lat: String? = null

    @SerializedName("long")
    var long: String? = null

    @SerializedName("lockStatus", alternate = [ "lock_status" ])
    var lockStatus = "\\u2014"

    @SerializedName("message")
    var message = "\\u2014"

    @SerializedName("closing", alternate = [ "Closing" ])
    var closing = false

    @SerializedName("temperature", alternate = [ "Temperature" ])
    var temperature = "\\u2014"

    @SerializedName("iaq", alternate = [ "IAQ" ])
    var iaq = "\\u2014"

    @SerializedName("humidity", alternate = [ "Humidity" ])
    var humidity = "\\u2014"

    @SerializedName("pressure", alternate = [ "Pressure" ])
    var pressure = "\\u2014"

    @SerializedName("altitude", alternate = [ "Altitude" ])
    var altitude = "\\u2014"

}