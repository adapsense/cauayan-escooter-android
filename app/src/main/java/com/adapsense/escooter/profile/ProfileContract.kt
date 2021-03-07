package com.adapsense.escooter.profile

import com.adapsense.escooter.base.BasePresenter
import com.adapsense.escooter.base.BaseView

interface ProfileContract {

    interface View : BaseView<Presenter?> {

        fun showUser(userFullName: String, userEmailAddress: String)

        fun showEditProfile()

        fun showErrorFullName(error: String?)

        fun showErrorOldPassword(error: String?)

        fun showErrorNewPassword(error: String?)

        fun showErrorConfirmPassword(error: String?)

        fun showError(error: String?)

        fun showSplash()

    }

    interface Presenter : BasePresenter {

        fun getUser()

        fun editUser(fullName: String)

        fun changePassword(oldPassword: String, newPassword: String, confirmPassword: String)

        fun logOut()

    }

}
