package com.adapsense.escooter.api.models.responses

import com.adapsense.escooter.api.models.objects.Vehicle
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class VehicleResponse: Serializable {

    @SerializedName("success")
    var success = false

    @SerializedName("vehicle")
    var vehicle: Vehicle? = null

    @SerializedName("error")
    var error: String? = null

}