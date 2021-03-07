package com.adapsense.escooter.api.models.responses

import com.adapsense.escooter.api.models.objects.VehicleStatus
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class VehicleStatusesResponse: Serializable {

    @SerializedName("success")
    var success = false

    @SerializedName("vehicleStatuses")
    var vehicleStatuses: List<VehicleStatus>? = null

    @SerializedName("error")
    var error: String? = null

}