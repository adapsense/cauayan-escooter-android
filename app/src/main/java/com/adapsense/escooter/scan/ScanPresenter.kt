package com.adapsense.escooter.scan

import com.adapsense.escooter.api.ApiRequestListener
import com.adapsense.escooter.api.calls.VehiclesApiCall
import com.adapsense.escooter.api.models.objects.Vehicle
import com.adapsense.escooter.api.models.responses.ErrorResponse
import com.adapsense.escooter.api.models.responses.VehicleResponse
import com.adapsense.escooter.api.models.responses.VehiclesResponse
import com.adapsense.escooter.util.AppLogger
import com.adapsense.escooter.util.CacheUtil
import com.google.gson.Gson
import java.util.*

class ScanPresenter(private val scanView: ScanContract.View) : ScanContract.Presenter {

    private var vehicles = LinkedList<Vehicle>()

    init {
        scanView.setPresenter(this)
    }

    override fun start() {
        scanView.setupViews()
    }

    override fun searchVehicle(code: String) {
        VehiclesApiCall.search(code, object : ApiRequestListener {
            override fun onSuccess(obj: Any) {
                val vehicleResponse = Gson().fromJson(obj.toString(), VehicleResponse::class.java)
                if(vehicleResponse.success && vehicleResponse.vehicle != null) {
                    updateVehicle(vehicleResponse.vehicle!!)
                } else {
                    scanView.showError("Scooter Not Found", "The scanned QR code is invalid.")
                }
            }

            override fun onError(obj: Any) {
                AppLogger.printRetrofitError(obj)
                scanView.showError("Scooter Not Found", "The scanned QR code is invalid.")
            }

        })
    }

    override fun updateVehicle(vehicle: Vehicle) {
        VehiclesApiCall.updateRider(CacheUtil.instance!!.user!!, vehicle, object : ApiRequestListener {
            override fun onSuccess(obj: Any) {
                val vehicleResponse = Gson().fromJson(obj.toString(), VehicleResponse::class.java)
                if(vehicleResponse.success && vehicleResponse.vehicle != null) {
                    getVehicles(vehicleResponse.vehicle!!)
                } else {
                    scanView.showError("Scooter Error", if(vehicleResponse.error != null) vehicleResponse.error!! else "An unexpected error occurred.")
                }
            }

            override fun onError(obj: Any) {
                AppLogger.printRetrofitError(obj)
                val errorResponse = Gson().fromJson(obj.toString(), ErrorResponse::class.java)
                scanView.showError("Scooter Error", if(errorResponse.error != null) errorResponse.error!! else "An unexpected error occurred.")
            }

        })
    }

    override fun getVehicles(vehicle: Vehicle) {
        vehicles.clear()
        VehiclesApiCall.get(object : ApiRequestListener {
            override fun onSuccess(obj: Any) {
                val vehiclesResponse = Gson().fromJson(obj.toString(), VehiclesResponse::class.java)
                if(vehiclesResponse.success) {
                    vehicles.addAll(vehiclesResponse.vehicles!!)
                }
                scanView.showVehicle(vehicles, vehicle)
            }

            override fun onError(obj: Any) {
                AppLogger.printRetrofitError(obj)
                scanView.showVehicle(vehicles, vehicle)
            }

        })
    }

}
