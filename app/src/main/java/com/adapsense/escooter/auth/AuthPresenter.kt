package com.adapsense.escooter.auth

import com.adapsense.escooter.api.ApiRequestListener
import com.adapsense.escooter.api.calls.UsersApiCall
import com.adapsense.escooter.api.models.objects.User
import com.adapsense.escooter.api.models.responses.ErrorResponse
import com.adapsense.escooter.api.models.responses.UserResponse
import com.adapsense.escooter.util.AppLogger
import com.adapsense.escooter.util.CacheUtil
import com.adapsense.escooter.util.StringUtil
import com.google.gson.Gson

class AuthPresenter(private val authView: AuthContract.View) : AuthContract.Presenter {

    init {
        authView.setPresenter(this)
    }

    override fun start() {
        authView.setupViews()
    }

    override fun search(emailAddress: String) {
        var valid = true

        if(emailAddress.isEmpty()) {
            valid = false
            authView.showErrorEmailAddress("Email address is required")
        } else if (!StringUtil.isValidEmail(emailAddress)) {
            valid = false
            authView.showErrorEmailAddress("Email address is invalid")
        } else {
            authView.showErrorEmailAddress(null)
        }

        if (valid) {
            val user = User()
            user.email = emailAddress

            UsersApiCall.search(user, object : ApiRequestListener {
                override fun onSuccess(obj: Any) {
                    val userResponse = Gson().fromJson(obj.toString(), UserResponse::class.java)
                    if(userResponse.success && userResponse.user != null) {
                        authView.showLogin(true)
                    } else {
                        authView.showSignup()
                    }
                }

                override fun onError(obj: Any) {
                    AppLogger.printRetrofitError(obj)
                    authView.showSignup()
                }

            })
        }

    }

    override fun signInRider(emailAddress: String, password: String) {
        var valid = true

        if(emailAddress.isEmpty()) {
            valid = false
            authView.showErrorEmailAddress("Email address is required")
        } else if (!StringUtil.isValidEmail(emailAddress)) {
            valid = false
            authView.showErrorEmailAddress("Email address is invalid")
        } else {
            authView.showErrorEmailAddress(null)
        }

        when {
            password.isEmpty() -> {
                valid = false
                authView.showErrorPassword("Password is required")
            }
            password.length < 8 -> {
                valid = false
                authView.showErrorPassword("Password should have a minimum of 8 characters")
            }
            else -> {
                authView.showErrorPassword(null)
            }
        }

        if (valid) {
            val user = User()
            user.email = emailAddress
            user.password = password

            UsersApiCall.signinRider(user, CacheUtil.instance!!.vehicle!!, object : ApiRequestListener {
                override fun onSuccess(obj: Any) {
                    val userResponse = Gson().fromJson(obj.toString(), UserResponse::class.java)
                    when {
                        userResponse.success -> {
                            CacheUtil.instance!!.user = userResponse.user
                            CacheUtil.instance!!.token = userResponse.token
                            authView.showHome()
                        }
                        userResponse.error != null -> {
                            authView.showError("Error", userResponse.error!!)
                        }
                        else -> {
                            authView.showMessage("An unexpected error occurred.")
                        }
                    }
                }

                override fun onError(obj: Any) {
                    AppLogger.printRetrofitError(obj)
                    val errorResponse = Gson().fromJson(obj.toString(), ErrorResponse::class.java)
                    if(errorResponse.error != null) {
                        authView.showError("Error", errorResponse.error!!)
                    } else {
                        authView.showMessage("An unexpected error occurred.")
                    }
                }

            })
        }
    }

    override fun signUpRider(fullName: String, emailAddress: String, password: String) {
        var valid = true

        if (fullName.isEmpty()) {
            valid = false
            authView.showErrorEmailAddress("Full name is required")
        } else if (!StringUtil.isValidName(fullName)) {
            valid = false
            authView.showErrorEmailAddress("Full name is invalid")
        } else {
            authView.showErrorEmailAddress(null)
        }

        if (emailAddress.isEmpty()) {
            valid = false
            authView.showErrorEmailAddress("Email address is required")
        } else if (!StringUtil.isValidEmail(emailAddress)) {
            valid = false
            authView.showErrorEmailAddress("Email address is invalid")
        } else {
            authView.showErrorEmailAddress(null)
        }

        when {
            password.isEmpty() -> {
                valid = false
                authView.showErrorPassword("Password is required")
            }
            password.length < 8 -> {
                valid = false
                authView.showErrorPassword("Password should have a minimum of 8 characters")
            }
            else -> {
                authView.showErrorPassword(null)
            }
        }

        if (valid) {
            val user = User()
            user.fullName = fullName
            user.email = emailAddress
            user.password = password

            UsersApiCall.signupRider(user, CacheUtil.instance!!.vehicle!!, object : ApiRequestListener {
                override fun onSuccess(obj: Any) {
                    val userResponse = Gson().fromJson(obj.toString(), UserResponse::class.java)
                    when {
                        userResponse.success -> {
                            CacheUtil.instance!!.user = userResponse.user
                            CacheUtil.instance!!.token = userResponse.token
                            authView.showHome()
                        }
                        userResponse.error != null -> {
                            authView.showError("Error", userResponse.error!!)
                        }
                        else -> {
                            authView.showMessage("An unexpected error occurred.")
                        }
                    }
                }

                override fun onError(obj: Any) {
                    AppLogger.printRetrofitError(obj)
                    val errorResponse = Gson().fromJson(obj.toString(), ErrorResponse::class.java)
                    if(errorResponse.error != null) {
                        authView.showError("Error", errorResponse.error!!)
                    } else {
                        authView.showMessage("An unexpected error occurred.")
                    }
                }

            })
        }
    }

    override fun signInAdmin(emailAddress: String, password: String) {
        var valid = true

        if (emailAddress.isEmpty()) {
            valid = false
            authView.showErrorEmailAddress("Email address is required")
        } else if (!StringUtil.isValidEmail(emailAddress)) {
            valid = false
            authView.showErrorEmailAddress("Email address is invalid")
        } else {
            authView.showErrorEmailAddress(null)
        }

        when {
            password.isEmpty() -> {
                valid = false
                authView.showErrorPassword("Password is required")
            }
            password.length < 8 -> {
                valid = false
                authView.showErrorPassword("Password should have a minimum of 8 characters")
            }
            else -> {
                authView.showErrorPassword(null)
            }
        }

        if (valid) {
            val user = User()
            user.email = emailAddress
            user.password = password

            UsersApiCall.signinAdmin(user, object : ApiRequestListener {
                override fun onSuccess(obj: Any) {
                    val userResponse = Gson().fromJson(obj.toString(), UserResponse::class.java)
                    when {
                        userResponse.success -> {
                            CacheUtil.instance!!.user = userResponse.user
                            CacheUtil.instance!!.token = userResponse.token
                            authView.showHome()
                        }
                        userResponse.error != null -> {
                            authView.showError("Error", userResponse.error!!)
                        }
                        else -> {
                            authView.showMessage("An unexpected error occurred.")
                        }
                    }
                }

                override fun onError(obj: Any) {
                    AppLogger.printRetrofitError(obj)
                    val errorResponse = Gson().fromJson(obj.toString(), ErrorResponse::class.java)
                    if(errorResponse.error != null) {
                        authView.showError("Error", errorResponse.error!!)
                    } else {
                        authView.showMessage("An unexpected error occurred.")
                    }
                }

            })
        }
    }

}
