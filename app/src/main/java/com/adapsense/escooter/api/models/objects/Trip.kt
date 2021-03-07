package com.adapsense.escooter.api.models.objects

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class Trip: Serializable {

    @SerializedName("_id")
    var id = ""

    @SerializedName("vehicle")
    var vehicle = Vehicle()

    @SerializedName("unlockTime")
    var unlockTime: String? = null

    @SerializedName("lockTime")
    var lockTime: String? = null

    @SerializedName("startLocation")
    var startLocation: Point? = null

    @SerializedName("endLocation")
    var endLocation: Point? = null

    override fun toString(): String {
        val fromSdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        fromSdf.timeZone = TimeZone.getTimeZone("GMT+0")

        val toSdf = SimpleDateFormat("MMMM d, yyyy h:mm a", Locale.ENGLISH)
        toSdf.timeZone = TimeZone.getDefault()

        val unlockDate = fromSdf.parse(unlockTime!!)
        return toSdf.format(unlockDate!!)
    }

}