package com.adapsense.escooter.scan

import com.adapsense.escooter.api.models.objects.Vehicle
import com.adapsense.escooter.base.BasePresenter
import com.adapsense.escooter.base.BaseView
import java.util.*

interface ScanContract {

    interface View : BaseView<Presenter?> {

        fun checkCameraPermission()

        fun showScanStart()

        fun showScanError()

        fun showVehicle(vehicles: LinkedList<Vehicle>, vehicle: Vehicle)

        fun showError(title: String, message: String)

    }

    interface Presenter : BasePresenter {

        fun searchVehicle(code: String)

        fun updateVehicle(vehicle: Vehicle)

        fun getVehicles(vehicle: Vehicle)

    }

}
