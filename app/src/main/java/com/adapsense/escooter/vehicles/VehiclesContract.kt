package com.adapsense.escooter.vehicles

import com.adapsense.escooter.api.models.objects.*
import com.adapsense.escooter.base.BasePresenter
import com.adapsense.escooter.base.BaseView
import com.adapsense.escooter.vehicles.view.VehicleViewHolder
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import java.io.File
import java.util.*

interface VehiclesContract {

    interface View : BaseView<Presenter?> {

        fun setVehicleTypes(vehicleTypes: LinkedList<VehicleType>)

        fun setVehicleStatuses(vehicleStatuses: LinkedList<VehicleStatus>)

        fun setVehicles(vehicles: LinkedList<Vehicle>)

        fun showVehicles(vehicles: LinkedList<Vehicle>)

        fun showVehicleLogs(vehicleLogs: HashMap<String, VehicleLog>)

        fun setVehicle(vehicle: Vehicle, selected: Boolean)

        fun showVehicle(vehicle: Vehicle?)

        fun selectVehicle(position: Int, vehicle: Vehicle)

        fun deleteVehicle(vehicle: Vehicle)

        fun showVehicleLog(fromMqtt: Boolean, vehicleLog: VehicleLog)

        fun showLockVehicle(locked: Boolean)

        fun showEmergencyAlarmVehicle(on: Boolean)

        fun initializeMqtt()

        fun showErrorLabel(error: String?)

        fun showErrorSerialNumber(error: String?)

        fun showErrorBrand(error: String?)

        fun showErrorModel(error: String?)

        fun showMessage(message: String)

        fun updateVehicle(vehicle: Vehicle)

        fun showSettingsVehicle()

        fun showRider(vehicle: Vehicle?)

        fun updateSettingsVehicle(vehicle: Vehicle)

        fun setRiders(riders: LinkedList<User>)

        fun showQr(vehicle: Vehicle?)

    }

    interface Presenter : BasePresenter {

        fun getTypes()

        fun setVehicleTypes(vehicleTypes: LinkedList<VehicleType>)

        fun getVehicleTypes(): LinkedList<VehicleType>

        fun getStatuses()

        fun setVehicleStatuses(vehicleStatuses: LinkedList<VehicleStatus>)

        fun getVehicleStatuses(): LinkedList<VehicleStatus>

        fun getVehicleStatus(vehicle: Vehicle): Int

        fun setVehicleStatus(position: Int)

        fun setVehicles(vehicles: LinkedList<Vehicle>)

        fun getVehicles()

        fun getRiderVehicles(): LinkedList<Vehicle>

        fun onBindVehicleViewAtPosition(position: Int, holder: VehicleViewHolder, selected: Boolean)

        fun getVehiclesCount(): Int

        fun selectVehicle(position: Int)

        fun selectVehicle(id: String)

        fun deleteVehicle(vehicle: Vehicle)

        fun getLatestLogs()

        fun setVehicle(vehicle: Vehicle)

        fun getVehicle(): Vehicle?

        fun getVehicle(position: Int): Vehicle

        fun updateVehicle(settings: Boolean, label: String, deleteImage: Boolean, imageFile: File?)

        fun updateVehicles(vehicle: Vehicle)

        fun getLatestLog()

        fun initializeMqtt(client: MqttAndroidClient, connectOptions: MqttConnectOptions)

        fun disconnectMqtt()

        fun getVehicleLog(): VehicleLog?

        fun setLockVehicle(lock: Boolean)

        fun setEmergencyAlarmVehicle(on: Boolean)

        fun editVehicle(vehicle: Vehicle)

        fun setRiders(riders: LinkedList<User>)

        fun getRiders(): LinkedList<User>

        fun getRider(vehicle: Vehicle): Int

        fun setRider(position: Int)

        fun showQr(vehicle: Vehicle)

        fun addVehicle(name: String, serialNumber: String, brand: String, model: String, imageFile: File?)

    }

}
