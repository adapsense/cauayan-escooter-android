package com.adapsense.escooter.api.models.responses

import com.adapsense.escooter.api.models.objects.Trip
import com.adapsense.escooter.api.models.objects.Vehicle
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class AnalyticsResponse: Serializable {

    @SerializedName("success")
    var success = false

    @SerializedName("vehicle")
    var vehicle: Vehicle? = null

    @SerializedName("date")
    var date: String? = null

    @SerializedName("unit")
    var unit: String? = null

    @SerializedName("time")
    var time: List<String>? = null

    @SerializedName("vehicleLogs")
    var vehicleLogs: List<String>? = null

    @SerializedName("trip")
    var trip: Trip? = null

    @SerializedName("route")
    var route: Array<Array<Double>>? = null

    @SerializedName("error")
    var error: String? = null

}