package com.adapsense.escooter.api.models.responses

import com.adapsense.escooter.api.models.objects.VehicleLog
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class VehicleLogsResponse: Serializable {

    @SerializedName("success")
    var success = false

    @SerializedName("vehicleLogs")
    var vehicleLogs: HashMap<String, VehicleLog>? = null

    @SerializedName("error")
    var error: String? = null

}