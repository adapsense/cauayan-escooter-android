package com.adapsense.escooter.api.models.responses

import com.adapsense.escooter.api.models.objects.VehicleLog
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class VehicleLogResponse: Serializable {

    @SerializedName("success")
    var success = false

    @SerializedName("vehicleLog")
    var vehicleLog: VehicleLog? = null

    @SerializedName("error")
    var error: String? = null

}