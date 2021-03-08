package com.adapsense.escooter.vehicles

import com.adapsense.escooter.BuildConfig
import com.adapsense.escooter.api.ApiRequestListener
import com.adapsense.escooter.api.calls.TripsApiCall
import com.adapsense.escooter.api.calls.VehiclesApiCall
import com.adapsense.escooter.api.models.objects.*
import com.adapsense.escooter.api.models.responses.*
import com.adapsense.escooter.util.AppLogger
import com.adapsense.escooter.util.CacheUtil
import com.adapsense.escooter.vehicles.view.VehicleViewHolder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.collections.HashMap

class VehiclesPresenter(private val vehiclesView: VehiclesContract.View) : VehiclesContract.Presenter {

    private var vehicleTypes = LinkedList<VehicleType>()
    private var vehicleStatuses = LinkedList<VehicleStatus>()
    private var vehicles = LinkedList<Vehicle>()
    private var vehicleLogs = HashMap<String, VehicleLog>()
    private var vehicle: Vehicle? = null
    private var vehicleLog: VehicleLog? = null
    private var trip: Trip? = null
    private var riders = LinkedList<User>()

    private var mqttAndroidClient: MqttAndroidClient? = null

    init {
        vehiclesView.setPresenter(this)
    }

    override fun start() {
        vehiclesView.setupViews()
        getTypes()
        getStatuses()
    }

    override fun getTypes() {
        VehiclesApiCall.types(object : ApiRequestListener {
            override fun onSuccess(obj: Any) {
                val vehicleTypesResponse = Gson().fromJson(obj.toString(), VehicleTypesResponse::class.java)
                if(vehicleTypesResponse.success) {
                    vehicleTypes.clear()
                    vehicleTypes.addAll(vehicleTypesResponse.vehicleTypes!!)
                }
            }

            override fun onError(obj: Any) {
                AppLogger.printRetrofitError(obj)
            }

        })
    }

    override fun setVehicleTypes(vehicleTypes: LinkedList<VehicleType>) {
        this.vehicleTypes = vehicleTypes
    }

    override fun getVehicleTypes(): LinkedList<VehicleType> {
        return vehicleTypes
    }

    override fun getStatuses() {
        VehiclesApiCall.statuses(object : ApiRequestListener {
            override fun onSuccess(obj: Any) {
                val vehicleStatusesResponse = Gson().fromJson(obj.toString(), VehicleStatusesResponse::class.java)
                if(vehicleStatusesResponse.success) {
                    vehicleStatuses.clear()
                    vehicleStatuses.addAll(vehicleStatusesResponse.vehicleStatuses!!)
                }
            }

            override fun onError(obj: Any) {
                AppLogger.printRetrofitError(obj)
            }

        })
    }

    override fun setVehicleStatuses(vehicleStatuses: LinkedList<VehicleStatus>) {
        this.vehicleStatuses = vehicleStatuses
    }

    override fun getVehicleStatuses(): LinkedList<VehicleStatus> {
        return vehicleStatuses
    }

    override fun getVehicleStatus(vehicle: Vehicle): Int {
        for(vehicleStatus in vehicleStatuses) {
            if(vehicleStatus.id == vehicle.vehicleStatus.id) {
                return vehicleStatuses.indexOf(vehicleStatus)
            }
        }
        return 0
    }

    override fun setVehicleStatus(position: Int) {
        vehicle?.vehicleStatus = vehicleStatuses[position]
    }

    override fun setVehicles(vehicles: LinkedList<Vehicle>) {
        this.vehicles = vehicles
    }

    override fun getVehicles() {
        VehiclesApiCall.get(object : ApiRequestListener {
            override fun onSuccess(obj: Any) {
                val vehiclesResponse = Gson().fromJson(obj.toString(), VehiclesResponse::class.java)
                if(vehiclesResponse.success) {
                    vehicles.clear()
                    vehicles.addAll(vehiclesResponse.vehicles!!)
                }
                vehiclesView.showVehicles(vehicles)
                getLatestLogs()
            }

            override fun onError(obj: Any) {
                AppLogger.printRetrofitError(obj)
                vehiclesView.showVehicles(vehicles)
            }

        })
    }

    override fun getRiderVehicles(): LinkedList<Vehicle> {
        return vehicles
    }

    override fun onBindVehicleViewAtPosition(position: Int, holder: VehicleViewHolder, selected: Boolean) {
        holder.setVehicle(vehicles[position], selected)
    }

    override fun getVehiclesCount(): Int {
        return vehicles.size
    }

    override fun selectVehicle(position: Int) {
        if(position != -1) {
            vehicle = vehicles[position]
            if(vehicle != null) {
                vehiclesView.selectVehicle(position, vehicle!!)
                if (vehicleLogs[vehicle!!.id] != null) {
                    vehiclesView.showVehicleLog(false, vehicleLogs[vehicle!!.id]!!)
                    vehiclesView.initializeMqtt()
                }
            }
        } else if(vehicleLogs.size > 0) {
            vehiclesView.showVehicleLogs(vehicleLogs)
        }
    }

    override fun selectVehicle(id: String) {
        if(vehicle != null && vehicle!!.id != id) {
            for (vehicle in vehicles) {
                if (vehicle.id == id) {
                    this.vehicle = vehicle
                    vehiclesView.selectVehicle(vehicles.indexOf(vehicle), this.vehicle!!)
                    if (vehicleLogs[this.vehicle!!.id] != null) {
                        vehiclesView.showVehicleLog(false, vehicleLogs[this.vehicle!!.id]!!)
                        vehiclesView.initializeMqtt()
                    }
                    break
                }
            }
        }
    }

    override fun deleteVehicle(vehicle: Vehicle) {
        VehiclesApiCall.delete(vehicle, object : ApiRequestListener {
            override fun onSuccess(obj: Any) {
                val vehicleResponse = Gson().fromJson(obj.toString(), VehicleResponse::class.java)
                when {
                    vehicleResponse.success -> {
                        vehiclesView.showMessage( "${vehicle.name} deleted.")
                        vehicles.remove(vehicle)
                        vehiclesView.showVehicles(vehicles)
                    }
                    vehicleResponse.error != null -> {
                        vehiclesView.showMessage(vehicleResponse.error!!)
                    }
                    else -> {
                        vehiclesView.showMessage("An error occurred while deleting the vehicle.")
                    }
                }
            }

            override fun onError(obj: Any) {
                AppLogger.printRetrofitError(obj)
                val errorResponse = Gson().fromJson(obj.toString(), ErrorResponse::class.java)
                if(errorResponse.error != null) {
                    vehiclesView.showMessage(errorResponse.error!!)
                } else {
                    vehiclesView.showMessage("An error occurred while deleting the vehicle.")
                }
            }

        })
    }

    override fun getLatestLogs() {
        if(vehicles.size > 0) {
            VehiclesApiCall.latestLogs(vehicles, object : ApiRequestListener {
                override fun onSuccess(obj: Any) {
                    val vehicleLogsResponse = Gson().fromJson(obj.toString(), VehicleLogsResponse::class.java)
                    if(vehicleLogsResponse.success) {
                        vehicleLogs.clear()
                        vehicleLogs = vehicleLogsResponse.vehicleLogs!!
                    }
                    if(vehicleLogs.size > 0) {
                        vehiclesView.showVehicleLogs(vehicleLogs)
                    }
                }

                override fun onError(obj: Any) {
                    AppLogger.printRetrofitError(obj)
                    vehiclesView.showVehicleLogs(vehicleLogs)
                }

            })
        } else if(vehicleLogs.size > 0) {
            vehiclesView.showVehicleLogs(vehicleLogs)
        }
    }

    override fun setVehicle(vehicle: Vehicle) {
        this.vehicle = vehicle
    }

    override fun getVehicle(): Vehicle? {
        return this.vehicle
    }

    override fun getVehicle(position: Int): Vehicle {
        return vehicles[position]
    }

    override fun updateVehicle(settings: Boolean, label: String, deleteImage: Boolean, imageFile: File?) {

        if(vehicle != null) {

            var valid = true

            if (label.isEmpty()) {
                valid = false
                vehiclesView.showErrorLabel("Scooter name is required")
            } else {
                vehicle!!.label = label
                vehiclesView.showErrorLabel(null)
            }

            if (deleteImage) {
                vehicle!!.image = ""
            }

            if (valid) {

                if (imageFile == null) {
                    VehiclesApiCall.update(vehicle!!, object : ApiRequestListener {
                        override fun onSuccess(obj: Any) {
                            val vehicleResponse =
                                    Gson().fromJson(obj.toString(), VehicleResponse::class.java)
                            if (vehicleResponse.success && vehicleResponse.vehicle != null) {
                                val vehicle = vehicleResponse.vehicle!!
                                if (!settings) {
                                    vehiclesView.updateVehicle(vehicle)
                                } else {
                                    vehiclesView.updateSettingsVehicle(vehicle)
                                }
                            } else {
                                vehiclesView.showMessage(if (vehicleResponse.error != null) vehicleResponse.error!! else "An unexpected error occurred.")
                            }
                        }

                        override fun onError(obj: Any) {
                            AppLogger.printRetrofitError(obj)
                            val errorResponse =
                                    Gson().fromJson(obj.toString(), ErrorResponse::class.java)
                            vehiclesView.showMessage(if (errorResponse.error != null) errorResponse.error!! else "An unexpected error occurred.")
                        }

                    })
                } else {
                    VehiclesApiCall.update(vehicle!!, imageFile, object : ApiRequestListener {
                        override fun onSuccess(obj: Any) {
                            val vehicleResponse =
                                    Gson().fromJson(obj.toString(), VehicleResponse::class.java)
                            if (vehicleResponse != null && vehicleResponse.success && vehicleResponse.vehicle != null) {
                                val vehicle = vehicleResponse.vehicle!!
                                vehiclesView.updateSettingsVehicle(vehicle)
                            } else {
                                vehiclesView.showMessage(if (vehicleResponse.error != null) vehicleResponse.error!! else "An unexpected error occurred.")
                            }
                        }

                        override fun onError(obj: Any) {
                            AppLogger.printRetrofitError(obj)
                            val errorResponse =
                                    Gson().fromJson(obj.toString(), ErrorResponse::class.java)
                            if (errorResponse.error != null) {
                                vehiclesView.showMessage(errorResponse.error!!)
                            } else {
                                vehiclesView.showMessage("An unexpected error occurred.")
                            }
                        }

                    })
                }

            }

        }

    }

    override fun updateVehicles(vehicle: Vehicle) {
        for(item in vehicles) {
            if(item.id == vehicle.id) {
                vehicles[vehicles.indexOf(item)] = vehicle
                vehiclesView.showVehicles(vehicles)
                break
            }
        }
    }

    override fun getLatestLog() {
        if(vehicle != null) {
            VehiclesApiCall.latestLog(vehicle!!, object : ApiRequestListener {

                override fun onSuccess(obj: Any) {
                    val vehicleLogResponse =
                            Gson().fromJson(obj.toString(), VehicleLogResponse::class.java)
                    if (vehicleLogResponse.success && vehicleLogResponse.vehicleLog != null) {
                        vehicleLog = vehicleLogResponse.vehicleLog
                        vehiclesView.showVehicleLog(false, vehicleLog!!)
                    }
                    vehiclesView.initializeMqtt()
                }

                override fun onError(obj: Any) {
                    AppLogger.printRetrofitError(obj)
                    vehiclesView.initializeMqtt()
                }

            })
        }
    }

    override fun initializeMqtt(client: MqttAndroidClient, connectOptions: MqttConnectOptions) {
        try {
            if(vehicle != null) {
                disconnectMqtt()
                this.mqttAndroidClient = client
                this.mqttAndroidClient?.apply {
                    connect(connectOptions).actionCallback = object :
                            IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            AppLogger.info("Connected: ${BuildConfig.MQTT_BROKER}")
                            val locationTopic =
                                    "location/${if (vehicle!!.topic.isNotEmpty()) vehicle!!.topic else vehicle!!.id}"
                            AppLogger.info("Subscribe: $locationTopic")
                            subscribe(
                                    locationTopic, 1
                            ) { _, message ->
                                AppLogger.info("Subscribe: $locationTopic")
                                AppLogger.info(String(message.payload, StandardCharsets.UTF_8))
                                val vehicleLogs = Gson().fromJson<List<VehicleLog>>(
                                        String(
                                                message.payload,
                                                StandardCharsets.UTF_8
                                        ), object : TypeToken<List<VehicleLog>>() {}.type
                                )
                                if (vehicleLogs.isNotEmpty()) {
                                    vehicleLog?.apply {
                                        lat = vehicleLogs[vehicleLogs.size - 1].lat
                                        long = vehicleLogs[vehicleLogs.size - 1].long
                                    }
                                    vehiclesView.showVehicleLog(true, vehicleLogs[vehicleLogs.size - 1])
                                }
                            }

                            val lockTopic =
                                    "lock/${if (vehicle!!.topic.isNotEmpty()) vehicle!!.topic else vehicle!!.id}"
                            AppLogger.info("Subscribe: $lockTopic")
                            subscribe(
                                    lockTopic, 1
                            ) { _, message ->
                                AppLogger.info("Subscribe: $lockTopic")
                                AppLogger.info(String(message.payload, StandardCharsets.UTF_8))
                                vehicleLog?.apply {
                                    lockStatus = "L"
                                }
                                vehiclesView.showLockVehicle(true)
                            }

                            val unlockTopic =
                                    "unlock/${if (vehicle!!.topic.isNotEmpty()) vehicle!!.topic else vehicle!!.id}"
                            AppLogger.info("Subscribe: $unlockTopic")
                            subscribe(
                                    unlockTopic, 1
                            ) { _, message ->
                                AppLogger.info("Subscribe: $unlockTopic")
                                AppLogger.info(String(message.payload, StandardCharsets.UTF_8))
                                vehicleLog?.apply {
                                    if (lockStatus == "E") {
                                        vehiclesView.showEmergencyAlarmVehicle(false)
                                    } else {
                                        vehiclesView.showLockVehicle(false)
                                    }
                                    lockStatus = "U"
                                }
                            }

                            val alarmTopic =
                                    "alarm/${if (vehicle!!.topic.isNotEmpty()) vehicle!!.topic else vehicle!!.id}"
                            AppLogger.info("Subscribe: $alarmTopic")
                            subscribe(
                                    alarmTopic, 1
                            ) { _, message ->
                                AppLogger.info("Subscribe: $alarmTopic")
                                AppLogger.info(String(message.payload, StandardCharsets.UTF_8))
                                vehicleLog?.apply {
                                    lockStatus = "E"
                                }
                                vehiclesView.showEmergencyAlarmVehicle(true)
                            }
                        }

                        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                            AppLogger.info("Connect Failure: ${exception!!.message}")
                            exception.printStackTrace()
                            vehiclesView.showVehicle(vehicle)
                            getLatestLog()
                        }

                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun disconnectMqtt() {
        if(mqttAndroidClient != null && mqttAndroidClient!!.isConnected) {
            mqttAndroidClient!!.apply {
                disconnect().actionCallback = object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        AppLogger.info("Disconnected: ${BuildConfig.MQTT_BROKER}")
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        AppLogger.info("Disconnect Failure: ${exception!!.message}")
                        exception.printStackTrace()
                    }
                }
            }
        }
    }

    override fun getVehicleLog(): VehicleLog? {
        return vehicleLog
    }

    override fun setLockVehicle(lock: Boolean) {
        if(vehicle != null) {
            val topic =
                    "${if (lock) "lock" else "unlock"}/${if (vehicle!!.topic.isNotEmpty()) vehicle!!.topic else vehicle!!.id}"
            val message =
                    if (lock) VehicleMessagePayload.LOCK.value else VehicleMessagePayload.UNLOCK.value
            AppLogger.info("Publish: $topic")
            AppLogger.info(message)
            mqttAndroidClient?.apply {
                publish(topic, MqttMessage(message.toByteArray(StandardCharsets.UTF_8)))
            }
            val vehicleMessage = VehicleMessage()
            vehicleMessage.user = CacheUtil.instance!!.user!!
            vehicleMessage.vehicle = vehicle!!
            vehicleMessage.topic = topic
            vehicleMessage.payload = message
            VehiclesApiCall.message(vehicleMessage, object : ApiRequestListener {
                override fun onSuccess(obj: Any) {

                }

                override fun onError(obj: Any) {
                    AppLogger.printRetrofitError(obj)
                }

            })
            if (!lock) {
                trip = Trip()
                trip?.apply {
                    vehicle = this@VehiclesPresenter.vehicle!!
                    vehicleLog?.let {
                        startLocation = Point(it.lat, it.long)
                    }
                    TripsApiCall.start(this, object : ApiRequestListener {
                        override fun onSuccess(obj: Any) {
                            val tripResponse =
                                    Gson().fromJson(obj.toString(), TripResponse::class.java)
                            if (tripResponse.success && tripResponse.trip != null) {
                                trip = tripResponse.trip
                            }
                        }

                        override fun onError(obj: Any) {
                            AppLogger.printRetrofitError(obj)
                        }

                    })
                }
            } else {
                if (trip == null) {
                    trip = Trip()
                    trip!!.vehicle = vehicle!!
                }
                vehicleLog?.let {
                    trip!!.endLocation = Point(it.lat, it.long)
                }
                TripsApiCall.end(trip!!, object : ApiRequestListener {
                    override fun onSuccess(obj: Any) {

                    }

                    override fun onError(obj: Any) {
                        AppLogger.printRetrofitError(obj)
                    }

                })
            }
        }
    }

    override fun setEmergencyAlarmVehicle(on: Boolean) {
        if(vehicle != null) {
            val topic =
                    "${if (on) "alarm" else "unlock"}/${if (vehicle!!.topic.isNotEmpty()) vehicle!!.topic else vehicle!!.id}"
            val message =
                    if (on) VehicleMessagePayload.ALARM.value else VehicleMessagePayload.UNLOCK.value
            AppLogger.info("Publish: $topic")
            AppLogger.info(message)
            mqttAndroidClient?.apply {
                publish(topic, MqttMessage(message.toByteArray(StandardCharsets.UTF_8)))
            }
            val vehicleMessage = VehicleMessage()
            vehicleMessage.user = CacheUtil.instance!!.user!!
            vehicleMessage.vehicle = vehicle!!
            vehicleMessage.topic = topic
            vehicleMessage.payload = message
            VehiclesApiCall.message(vehicleMessage, object : ApiRequestListener {
                override fun onSuccess(obj: Any) {

                }

                override fun onError(obj: Any) {
                    AppLogger.printRetrofitError(obj)
                }

            })
        }
    }

    override fun editVehicle(vehicle: Vehicle) {
        this.vehicle = vehicle
        vehiclesView.showSettingsVehicle()
    }

    override fun setRiders(riders: LinkedList<User>) {
        this.riders.clear()
        for(rider in riders) {
            if(!rider.userType.isAdmin) {
                this.riders.add(rider)
            }
        }
    }

    override fun getRiders(): LinkedList<User> {
        return riders
    }

    override fun getRider(vehicle: Vehicle): Int {
        if(vehicle.rider != null) {
            for(rider in riders) {
                if(rider.id == vehicle.rider!!.id) {
                    return riders.indexOf(rider)
                }
            }
        }
        return 0
    }

    override fun setRider(position: Int) {
        if(vehicle != null) {
            vehicle!!.rider = riders[position]
        }
    }

    override fun showQr(vehicle: Vehicle) {
        vehiclesView.showQr(vehicle)
    }

    override fun addVehicle(
            name: String,
            serialNumber: String,
            brand: String,
            model: String,
            imageFile: File?
    ) {
        vehicle = Vehicle()
        var valid = true

        if(name.isEmpty()) {
            valid = false
            vehiclesView.showErrorLabel("Name is required")
        } else {
            vehicle!!.name = name
            vehiclesView.showErrorLabel(null)
        }

        if(serialNumber.isEmpty()) {
            valid = false
            vehiclesView.showErrorSerialNumber("Serial number is required")
        } else {
            vehicle!!.serialNumber = serialNumber
            vehiclesView.showErrorSerialNumber(null)
        }

        if(brand.isEmpty()) {
            valid = false
            vehiclesView.showErrorSerialNumber("Brand is required")
        } else {
            vehicle!!.brand = brand
            vehiclesView.showErrorBrand(null)
        }

        if(model.isEmpty()) {
            valid = false
            vehiclesView.showErrorModel("Model is required")
        } else {
            vehicle!!.model = model
            vehiclesView.showErrorModel(null)
        }

        if(valid) {
            vehicle!!.vehicleType = vehicleTypes[0]
            if(imageFile == null) {
                VehiclesApiCall.add(vehicle!!, object : ApiRequestListener {
                    override fun onSuccess(obj: Any) {
                        val vehicleResponse =
                                Gson().fromJson(obj.toString(), VehicleResponse::class.java)
                        if (vehicleResponse.success && vehicleResponse.vehicle != null) {
                            val vehicle = vehicleResponse.vehicle!!
                            vehicles.add(vehicle)
                            vehiclesView.showVehicles(vehicles)
                        } else {
                            vehiclesView.showMessage(if (vehicleResponse.error != null) vehicleResponse.error!! else "An unexpected error occurred.")
                        }
                    }

                    override fun onError(obj: Any) {
                        AppLogger.printRetrofitError(obj)
                        val errorResponse =
                                Gson().fromJson(obj.toString(), ErrorResponse::class.java)
                        vehiclesView.showMessage(if (errorResponse.error != null) errorResponse.error!! else "An unexpected error occurred.")
                    }

                })
            } else {
                VehiclesApiCall.add(vehicle!!, imageFile, object : ApiRequestListener {
                    override fun onSuccess(obj: Any) {
                        val vehicleResponse =
                                Gson().fromJson(obj.toString(), VehicleResponse::class.java)
                        if (vehicleResponse != null && vehicleResponse.success && vehicleResponse.vehicle != null) {
                            val vehicle = vehicleResponse.vehicle!!
                            vehicles.add(vehicle)
                            vehiclesView.showVehicles(vehicles)
                        } else {
                            vehiclesView.showMessage(if (vehicleResponse.error != null) vehicleResponse.error!! else "An unexpected error occurred.")
                        }
                    }

                    override fun onError(obj: Any) {
                        AppLogger.printRetrofitError(obj)
                        val errorResponse = Gson().fromJson(obj.toString(), ErrorResponse::class.java)
                        if(errorResponse.error != null) {
                            vehiclesView.showMessage(errorResponse.error!!)
                        } else {
                            vehiclesView.showMessage("An unexpected error occurred.")
                        }
                    }

                })
            }

        }
    }

}
