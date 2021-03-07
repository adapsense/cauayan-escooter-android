package com.adapsense.escooter.dashboard

import com.adapsense.escooter.api.models.objects.User
import com.adapsense.escooter.base.BasePresenter
import com.adapsense.escooter.base.BaseView
import com.adapsense.escooter.api.models.objects.Vehicle
import com.adapsense.escooter.api.models.objects.VehicleLog
import com.google.android.gms.maps.model.Marker
import java.util.*

interface DashboardContract {

    interface View : BaseView<Presenter?> {

        fun showAdmin()

        fun showRider()

        fun showVehicles(vehicles: LinkedList<Vehicle>)

        fun showVehicle(vehicle: Vehicle)

        fun updateVehicle(settings: Boolean, vehicle: Vehicle)

        fun showVehicleLog(vehicle: Vehicle, vehicleLog: VehicleLog)

        fun showVehicleMarker(marker: Marker)

        fun setRiders(riders: LinkedList<User>)

    }

    interface Presenter : BasePresenter {

        fun getUser()

        fun getVehicles()

        fun getRiderVehicles(): LinkedList<Vehicle>

        fun getVehiclesCount(): Int

        fun getVehicle(position: Int): Vehicle

        fun updateVehicle(settings: Boolean, vehicle: Vehicle)

    }

}
