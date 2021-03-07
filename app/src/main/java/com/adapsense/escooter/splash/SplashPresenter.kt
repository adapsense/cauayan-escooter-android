package com.adapsense.escooter.splash

import com.adapsense.escooter.api.ApiRequestListener
import com.adapsense.escooter.api.calls.VehiclesApiCall
import com.adapsense.escooter.api.models.responses.VehicleResponse
import com.adapsense.escooter.util.AppLogger
import com.adapsense.escooter.util.CacheUtil
import com.google.gson.Gson

class SplashPresenter(private val splashView: SplashContract.View) : SplashContract.Presenter {

    init {
        splashView.setPresenter(this)
    }

    override fun start() {
        splashView.setupViews()
    }

    override fun searchVehicle(code: String) {
        VehiclesApiCall.search(code, object : ApiRequestListener {
            override fun onSuccess(obj: Any) {
                val vehicleResponse = Gson().fromJson(obj.toString(), VehicleResponse::class.java)
                if(vehicleResponse.success && vehicleResponse.vehicle != null) {
                    val vehicle = vehicleResponse.vehicle!!
                    CacheUtil.instance!!.vehicle = vehicle
                    splashView.showAuth(vehicle.rider != null)
                } else {
                    splashView.showError("Scooter Not Found", "The scanned QR code is invalid.")
                }
            }

            override fun onError(obj: Any) {
                AppLogger.printRetrofitError(obj)
                splashView.showError("Scooter Not Found", "The scanned QR code is invalid.")
            }

        })
    }


}
