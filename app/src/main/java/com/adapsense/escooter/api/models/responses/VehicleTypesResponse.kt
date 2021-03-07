package com.adapsense.escooter.api.models.responses

import com.adapsense.escooter.api.models.objects.VehicleType
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class VehicleTypesResponse: Serializable {

    @SerializedName("success")
    var success = false

    @SerializedName("vehicleTypes")
    var vehicleTypes: List<VehicleType>? = null

    @SerializedName("error")
    var error: String? = null

}