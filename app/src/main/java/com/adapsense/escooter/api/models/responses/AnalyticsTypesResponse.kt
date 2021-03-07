package com.adapsense.escooter.api.models.responses

import com.adapsense.escooter.api.models.objects.AnalyticsType
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class AnalyticsTypesResponse: Serializable {

    @SerializedName("success")
    var success = false

    @SerializedName("analyticsTypes")
    var analyticsTypes: List<AnalyticsType>? = null

    @SerializedName("error")
    var error: String? = null

}