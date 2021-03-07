package com.adapsense.escooter.api.models.objects

import com.google.gson.annotations.SerializedName

class Point(latitude: String?, longitude: String?) {

    @SerializedName("type")
    var type = "Point"

    @SerializedName("coordinates")
    var coordinates: Array<String?>? = arrayOf(longitude, latitude)

}