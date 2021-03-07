package com.adapsense.escooter.auth

import com.adapsense.escooter.base.BasePresenter
import com.adapsense.escooter.base.BaseView

interface AuthContract {

    interface View : BaseView<Presenter?> {

        fun showLogin(isRider: Boolean)

        fun showAdmin()

        fun showSignup()

        fun showHome()

        fun showErrorFullName(error: String?)

        fun showErrorEmailAddress(error: String?)

        fun showErrorPassword(error: String?)

        fun showError(title: String, message: String)

        fun showMessage(message: String)

    }

    interface Presenter : BasePresenter {

        fun search(emailAddress: String)

        fun signInRider(emailAddress: String, password: String)

        fun signUpRider(fullName: String, emailAddress: String, password: String)

        fun signInAdmin(emailAddress: String, password: String)

    }

}
