package com.adapsense.escooter.splash

import com.adapsense.escooter.base.BasePresenter
import com.adapsense.escooter.base.BaseView

interface SplashContract {

    interface View : BaseView<Presenter?> {

        fun initializeScan()

        fun checkCameraPermission()

        fun showScanStart()

        fun showScanError()

        fun showAuth(login: Boolean)

        fun showError(title: String, message: String)

    }

    interface Presenter : BasePresenter {

        fun searchVehicle(code: String)

    }

}
