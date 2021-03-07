package com.adapsense.escooter.home

import com.adapsense.escooter.base.BasePresenter
import com.adapsense.escooter.base.BaseView
import com.adapsense.escooter.api.models.objects.Vehicle

interface HomeContract {

    interface View : BaseView<Presenter?> {

        fun setTitle(position: Int)

        fun showScan()

        fun hideScan(hide: Boolean)

        fun updateVehicle(settings: Boolean, vehicle: Vehicle)

    }

    interface Presenter : BasePresenter {



    }

}
