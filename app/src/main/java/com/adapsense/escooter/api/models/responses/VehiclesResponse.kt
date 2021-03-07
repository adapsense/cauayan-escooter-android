package com.adapsense.escooter.api.models.responses

import com.adapsense.escooter.api.models.objects.Vehicle
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class VehiclesResponse: Serializable {

    @SerializedName("success")
    var success = false

    @SerializedName("vehicles")
    var vehicles: List<Vehicle>? = null

    @SerializedName("error")
    var error: String? = null

}