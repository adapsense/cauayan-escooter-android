package com.adapsense.escooter.dashboard

import com.adapsense.escooter.api.ApiRequestListener
import com.adapsense.escooter.api.calls.VehiclesApiCall
import com.adapsense.escooter.api.models.objects.Vehicle
import com.adapsense.escooter.api.models.responses.VehiclesResponse
import com.adapsense.escooter.util.AppLogger
import com.adapsense.escooter.util.CacheUtil
import com.google.gson.Gson
import java.util.*

class DashboardPresenter(private val dashboardView: DashboardContract.View) : DashboardContract.Presenter {

    private var vehicles = LinkedList<Vehicle>()

    init {
        dashboardView.setPresenter(this)
    }

    override fun start() {
        dashboardView.setupViews()
    }

    override fun getUser() {
        val user = CacheUtil.instance!!.user
        if(user!!.userType.isAdmin) {
            dashboardView.showAdmin()
        } else {
            dashboardView.showRider()
        }
    }

    override fun getVehicles() {
        vehicles.clear()
        VehiclesApiCall.get(object : ApiRequestListener {
            override fun onSuccess(obj: Any) {
                val vehiclesResponse = Gson().fromJson(obj.toString(), VehiclesResponse::class.java)
                if(vehiclesResponse.success) {
                    vehicles.addAll(vehiclesResponse.vehicles!!)
                }
                dashboardView.showVehicles(vehicles)
                if(!CacheUtil.instance!!.user!!.userType.isAdmin && vehicles.size > 0) {
                    val vehicle = vehicles[0]
                    if(vehicle.label.isEmpty()) {
                        dashboardView.showVehicle(vehicle)
                    }
                }
            }

            override fun onError(obj: Any) {
                AppLogger.printRetrofitError(obj)
                dashboardView.showVehicles(vehicles)
            }

        })

    }

    override fun getRiderVehicles(): LinkedList<Vehicle> {
        return vehicles
    }

    override fun getVehicle(position: Int): Vehicle {
        return vehicles[position]
    }

    override fun getVehiclesCount(): Int {
        return vehicles.size
    }

    override fun updateVehicle(settings: Boolean, vehicle: Vehicle) {
        var index = -1
        for (i in 0 until vehicles.size) {
            if (vehicles[i].id == vehicle.id) {
                index = i
                break
            }
        }
        if (index != -1) {
            vehicles[index] = vehicle
            if(!settings) {
                dashboardView.showVehicles(vehicles)
            }
        }
    }

}
