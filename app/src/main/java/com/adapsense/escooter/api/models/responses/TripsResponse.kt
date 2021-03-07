package com.adapsense.escooter.api.models.responses

import com.adapsense.escooter.api.models.objects.Trip
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TripsResponse: Serializable {

    @SerializedName("success")
    var success = false

    @SerializedName("trips")
    var trips: List<Trip>? = null

    @SerializedName("error")
    var error: String? = null

}