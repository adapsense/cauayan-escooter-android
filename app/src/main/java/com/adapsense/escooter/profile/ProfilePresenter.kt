package com.adapsense.escooter.profile

import com.adapsense.escooter.api.ApiRequestListener
import com.adapsense.escooter.api.calls.UsersApiCall
import com.adapsense.escooter.api.models.responses.ErrorResponse
import com.adapsense.escooter.api.models.responses.UserResponse
import com.adapsense.escooter.util.AppLogger
import com.adapsense.escooter.util.CacheUtil
import com.adapsense.escooter.util.StringUtil
import com.google.gson.Gson

class ProfilePresenter(private val profileView: ProfileContract.View) : ProfileContract.Presenter {

    init {
        profileView.setPresenter(this)
    }

    override fun start() {
        profileView.setupViews()
    }

    override fun getUser() {
        val user = CacheUtil.instance!!.user
        profileView.showUser(user!!.fullName, user.email)
    }

    override fun editUser(fullName: String) {
        var valid = true

        if(fullName.isEmpty()) {
            valid = false
            profileView.showErrorFullName("Full name is required")
        } else if(StringUtil.isValidName(fullName)) {
            valid = false
            profileView.showErrorFullName("Full name is invalid")
        }

        if (valid) {
            val user = CacheUtil.instance!!.user
            user!!.fullName = fullName
            UsersApiCall.update(user, object : ApiRequestListener {
                override fun onSuccess(obj: Any) {
                    val userResponse = Gson().fromJson(obj.toString(), UserResponse::class.java)
                    when {
                        userResponse.success  -> {
                            val updatedUser = userResponse.user
                            if(updatedUser != null) {
                                CacheUtil.instance!!.user = updatedUser
                                CacheUtil.instance!!.token = userResponse.token
                                profileView.showUser(updatedUser.fullName, updatedUser.email)
                            } else {
                                profileView.showError("An unexpected error occurred.")
                            }
                        }
                        userResponse.error != null -> {
                            profileView.showError(userResponse.error!!)
                        }
                        else -> {
                            profileView.showError("An unexpected error occurred.")
                        }
                    }
                }

                override fun onError(obj: Any) {
                    AppLogger.printRetrofitError(obj)
                    val errorResponse = Gson().fromJson(obj.toString(), ErrorResponse::class.java)
                    if(errorResponse.error != null) {
                        profileView.showError(errorResponse.error!!)
                    } else {
                        profileView.showError("An unexpected error occurred.")
                    }
                }

            })
        }

    }

    override fun changePassword(oldPassword: String, newPassword: String, confirmPassword: String) {
        var valid = true

        if(oldPassword.isEmpty()) {
            valid = false
            profileView.showErrorOldPassword("Old password is required")
        }

        if(newPassword.isEmpty()) {
            valid = false
            profileView.showErrorNewPassword("New password is required")
        }

        if(confirmPassword != newPassword) {
            valid = false
            profileView.showErrorConfirmPassword("Passwords do not match")
        }

        if (valid) {
            UsersApiCall.changePassword(CacheUtil.instance!!.user!!, oldPassword, newPassword, object : ApiRequestListener {
                override fun onSuccess(obj: Any) {
                    val userResponse = Gson().fromJson(obj.toString(), UserResponse::class.java)
                    when {
                        userResponse.success -> {
                            val updatedUser = userResponse.user
                            if(updatedUser != null) {
                                CacheUtil.instance!!.user = updatedUser
                                CacheUtil.instance!!.token = userResponse.token
                                profileView.showUser(updatedUser.fullName, updatedUser.email)
                            } else {
                                profileView.showError("An unexpected error occurred.")
                            }
                        }
                        userResponse.error != null -> {
                            profileView.showError(userResponse.error!!)
                        }
                        else -> {
                            profileView.showError("An unexpected error occurred.")
                        }
                    }
                }

                override fun onError(obj: Any) {
                    AppLogger.printRetrofitError(obj)
                    val errorResponse = Gson().fromJson(obj.toString(), ErrorResponse::class.java)
                    if(errorResponse.error != null) {
                        profileView.showError(errorResponse.error!!)
                    } else {
                        profileView.showError("An unexpected error occurred.")
                    }
                }

            })
        }
    }

    override fun logOut() {
        CacheUtil.instance!!.token = null
        CacheUtil.instance!!.user = null
        profileView.showSplash()
    }

}
