package com.adapsense.escooter.vehicles.view

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.SystemClock
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer.OnChronometerTickListener
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.adapsense.escooter.BuildConfig
import com.adapsense.escooter.R
import com.adapsense.escooter.api.models.objects.*
import com.adapsense.escooter.base.BaseFragment
import com.adapsense.escooter.dashboard.DashboardFragment
import com.adapsense.escooter.home.HomeActivity
import com.adapsense.escooter.vehicles.VehiclesContract
import com.adapsense.escooter.vehicles.VehiclesPresenter
import kotlinx.android.synthetic.main.fragment_vehicle.*
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class VehicleFragment : BaseFragment(), VehiclesContract.View {

    private lateinit var vehiclesPresenter: VehiclesContract.Presenter

    private var vehicleDialogFragment: VehicleDialogFragment? = null

    companion object {

        @JvmStatic
        fun newInstance() = VehicleFragment()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_vehicle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vehiclesPresenter.start()
        showVehicle(vehiclesPresenter.getVehicle())
    }

    override fun onResume() {
        super.onResume()
        vehiclesPresenter.getLatestLog()
    }

    override fun onPause() {
        super.onPause()
        vehiclesPresenter.disconnectMqtt()
    }

    override fun setPresenter(presenter: VehiclesContract.Presenter?) {
        vehiclesPresenter = presenter!!
    }

    override fun setupViews() {

        settings.setOnClickListener {
            if(vehicleDialogFragment != null) {
                vehicleDialogFragment!!.dismiss()
            }
            vehicleDialogFragment = VehicleDialogFragment(settings = true, add = false)
            vehicleDialogFragment!!.setVehicleTypes(LinkedList())
            vehicleDialogFragment!!.setVehicleStatuses(vehiclesPresenter.getVehicleStatuses())
            vehicleDialogFragment!!.setVehicles(vehiclesPresenter.getRiderVehicles())
            vehicleDialogFragment!!.setVehicle(vehiclesPresenter.getVehicle()!!, false)
            vehicleDialogFragment!!.isCancelable = false
            vehicleDialogFragment!!.show(
                    parentFragment!!.childFragmentManager,
                    VehicleDialogFragment::class.java.simpleName
            )
        }

        lock.setOnClickListener {
            val vehicleLog = vehiclesPresenter.getVehicleLog()
            vehicleLog?.let {
                vehiclesPresenter.setLockVehicle(it.lockStatus == "U")
                when(it.lockStatus) {
                    "L" -> {
                        var animationDrawable: AnimationDrawable?
                        lock.apply {
                            setBackgroundResource(R.drawable.unlock)
                            animationDrawable = background as AnimationDrawable
                        }
                        animationDrawable!!.start()
                    }
                    "U" -> {
                        var animationDrawable: AnimationDrawable?
                        lock.apply {
                            setBackgroundResource(R.drawable.lock)
                            animationDrawable = background as AnimationDrawable
                        }
                        animationDrawable!!.start()
                    }
                }
            }
        }

        emergencyAlarm.setOnClickListener {
            val vehicleLog = vehiclesPresenter.getVehicleLog()
            vehicleLog?.let {
                vehiclesPresenter.setEmergencyAlarmVehicle(it.lockStatus != "E")
                if (it.lockStatus == "E") {
                    (activity as HomeActivity).showAlertDialog("Emergency Alarm",
                            "Are you to turn OFF emergency alarm?",
                            "Turn OFF",
                            { dialog, _ ->
                                dialog!!.dismiss()
                                showEmergencyAlarmVehicle(false)
                            },
                            "Cancel",
                            { dialog, _ -> dialog!!.dismiss() })
                } else {
                    (activity as HomeActivity).showAlertDialog("Emergency Alarm",
                            "Are you to turn ON emergency alarm?",
                            "Turn ON",
                            { dialog, _ ->
                                dialog!!.dismiss()
                                showEmergencyAlarmVehicle(true)
                            },
                            "Cancel",
                            { dialog, _ -> dialog!!.dismiss() })
                }
            }
        }

        chronometer.onChronometerTickListener = OnChronometerTickListener {
            val time = SystemClock.elapsedRealtime() - it.base
            val h = (time / 3600000).toInt()
            val m = (time - h * 3600000).toInt() / 60000
            val s = (time - h * 3600000 - m * 60000).toInt() / 1000
            val hh = if (h < 10) "0$h" else h.toString() + ""
            val mm = if (m < 10) "0$m" else m.toString() + ""
            val ss = if (s < 10) "0$s" else s.toString() + ""
            it.text = "$hh:$mm:$ss"
        }

    }

    override fun setVehicleTypes(vehicleTypes: LinkedList<VehicleType>) {

    }

    override fun setVehicleStatuses(vehicleStatuses: LinkedList<VehicleStatus>) {

    }

    override fun setVehicles(vehicles: LinkedList<Vehicle>) {

    }

    override fun showVehicles(vehicles: LinkedList<Vehicle>) {

    }

    override fun showVehicleLogs(vehicleLogs: HashMap<String, VehicleLog>) {

    }

    override fun setVehicle(vehicle: Vehicle, selected: Boolean) {
        vehiclesPresenter = VehiclesPresenter(this)
        vehiclesPresenter.setVehicle(vehicle)
    }

    override fun showVehicle(vehicle: Vehicle?) {
        if(vehicle != null) {
            status?.let {
                it.text = "Connecting to\n${if (vehicle.label.isNotEmpty()) vehicle.label else "My Scooter"}..."
            }
            lock?.let {
                it.visibility = View.GONE
            }
            emergencyAlarm?.let {
                it.visibility = View.GONE
            }
            chronometerContainer?.let{
                it.visibility = View.GONE
            }
        }
    }

    override fun selectVehicle(position: Int, vehicle: Vehicle) {

    }

    override fun deleteVehicle(vehicle: Vehicle) {

    }

    override fun showVehicleLog(fromMqtt: Boolean, vehicleLog: VehicleLog) {

        activity!!.runOnUiThread {

            val vehicle = vehiclesPresenter.getVehicle()

            if(vehicle != null) {

                if (vehicleLog.time.isNotEmpty()) {
                    var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                    val timestamp = sdf.parse(vehicleLog.time)
                    sdf = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.ENGLISH)
                    time.text = "Last Update: ${sdf.format(timestamp!!)}"
                }

                temperature.text = vehicleLog.temperature
                humidity.text = vehicleLog.humidity
                iaq.text = vehicleLog.iaq
                pressure.text = vehicleLog.pressure
                altitude.text = vehicleLog.altitude

                if (fromMqtt) {
                    status.text =
                            "${if (vehicle.label.isNotEmpty()) vehicle.label else "My Scooter"} is ${vehicleLog.message}"
                }

                if (fromMqtt && lock.visibility == View.GONE) {
                    showLockVehicle(vehicleLog.lockStatus == "L")
                }

                if (fromMqtt && emergencyAlarm.visibility == View.GONE) {
                    showEmergencyAlarmVehicle(vehicleLog.lockStatus == "A")
                }

                if (fromMqtt && chronometerContainer.visibility == View.GONE) {
                    chronometer.text = "00:00:00"
                    chronometerContainer.visibility = View.VISIBLE
                    chronometer.base = SystemClock.elapsedRealtime()
                    chronometer.start()
                }

                (parentFragment as DashboardFragment).showVehicleLog(vehicle!!, vehicleLog)

            }
        }

    }

    override fun showLockVehicle(locked: Boolean) {
        activity!!.runOnUiThread {
            if(locked) {
                lock.apply {
                    setBackgroundResource(R.drawable.locked)
                    visibility = View.VISIBLE
                }
            } else {
                lock.apply {
                    setBackgroundResource(R.drawable.unlocked)
                    visibility = View.VISIBLE
                }
            }
        }
    }

    override fun showEmergencyAlarmVehicle(on: Boolean) {
        activity!!.runOnUiThread {
            if(on) {
                emergencyAlarm.apply {
                    setBackgroundColor(ContextCompat.getColor(activity!!, R.color.warning))
                    setTextColor(ContextCompat.getColor(activity!!, R.color.warning_text))
                    typeface = ResourcesCompat.getFont(activity!!, R.font.avenir_next_demi_bold)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    text = "Emergency Alarm ON"
                }
            } else {
                emergencyAlarm.apply {
                    setBackgroundColor(ContextCompat.getColor(activity!!, R.color.divider))
                    setTextColor(ContextCompat.getColor(activity!!, R.color.secondary_text))
                    typeface = ResourcesCompat.getFont(activity!!, R.font.avenir_next_regular)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
                    text = "Emergency Alarm OFF"
                }
            }
            emergencyAlarm.visibility = View.VISIBLE
        }
    }

    override fun initializeMqtt() {
        val fragmentActivity = activity
        if(fragmentActivity != null) {
            val connectOptions = MqttConnectOptions()
            connectOptions.userName = BuildConfig.MQTT_USERNAME
            connectOptions.password = BuildConfig.MQTT_PASSWORD.toCharArray()
            vehiclesPresenter.initializeMqtt(
                    MqttAndroidClient(
                            fragmentActivity,
                            BuildConfig.MQTT_BROKER,
                            MqttClient.generateClientId()
                    ), connectOptions
            )
        }
    }

    override fun showErrorLabel(error: String?) {

    }

    override fun showErrorSerialNumber(error: String?) {

    }

    override fun showErrorBrand(error: String?) {

    }

    override fun showErrorModel(error: String?) {

    }

    override fun showMessage(message: String) {

    }

    override fun updateVehicle(vehicle: Vehicle) {

    }

    override fun showSettingsVehicle() {

    }

    override fun showRider(vehicle: Vehicle?) {

    }

    override fun updateSettingsVehicle(vehicle: Vehicle) {
        if(vehicleDialogFragment !== null) {
            vehicleDialogFragment?.apply {
                updateSettingsVehicle(vehicle)
            }
        }
    }

    override fun setRiders(riders: LinkedList<User>) {

    }

    override fun showQr(vehicle: Vehicle?) {

    }

}