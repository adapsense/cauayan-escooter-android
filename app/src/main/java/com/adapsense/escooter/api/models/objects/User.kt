package com.adapsense.escooter.api.models.objects

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class User: Serializable {

    @SerializedName("_id")
    var id = ""

    @SerializedName("fullName")
    var fullName = ""

    @SerializedName("email")
    var email = ""

    @SerializedName("password")
    var password: String? = null

    @SerializedName("userType")
    var userType = UserType()

    @SerializedName("userGroup")
    var userGroup = UserGroup()

    @SerializedName("userStatus")
    var userStatus = UserStatus()

    override fun toString(): String {
        return fullName
    }

}