package com.adapsense.escooter.vehicles.view

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapsense.escooter.BuildConfig
import com.adapsense.escooter.R
import com.adapsense.escooter.api.models.objects.*
import com.adapsense.escooter.base.BaseFragment
import com.adapsense.escooter.home.HomeActivity
import com.adapsense.escooter.maps.LatLngInterpolator
import com.adapsense.escooter.util.AppLogger
import com.adapsense.escooter.util.ViewUtil
import com.adapsense.escooter.vehicles.VehiclesContract
import com.adapsense.escooter.vehicles.VehiclesPresenter
import com.adapsense.escooter.vehicles.adapters.VehicleInfoWindowAdapter
import com.adapsense.escooter.vehicles.adapters.VehiclesRecyclerViewAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_vehicle.*
import kotlinx.android.synthetic.main.fragment_vehicles.*
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import java.text.SimpleDateFormat
import java.util.*

class VehiclesFragment: BaseFragment(), VehiclesContract.View {

    private lateinit var vehiclesPresenter: VehiclesContract.Presenter

    private var vehicleDialogFragment: VehicleDialogFragment? = null

    private lateinit var adapter: VehiclesRecyclerViewAdapter

    private lateinit var googleMap: GoogleMap

    private val vehicleMarkers = HashMap<String, Marker>()

    companion object {

        @JvmStatic
        fun newInstance() = VehiclesFragment()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        vehiclesPresenter = VehiclesPresenter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vehicles, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vehiclesPresenter.start()
        swipeRefreshLayout.isRefreshing = true
    }

    override fun setPresenter(presenter: VehiclesContract.Presenter?) {
        vehiclesPresenter = presenter!!
    }

    override fun setupViews() {

        add.setOnClickListener {
            if(vehicleDialogFragment != null) {
                vehicleDialogFragment!!.dismiss()
            }
            vehicleDialogFragment = VehicleDialogFragment(settings = false, add = true)
            vehicleDialogFragment!!.setVehicleTypes(vehiclesPresenter.getVehicleTypes())
            vehicleDialogFragment!!.setVehicleStatuses(LinkedList())
            vehicleDialogFragment!!.setVehicles(vehiclesPresenter.getRiderVehicles())
            vehicleDialogFragment!!.setVehicle(Vehicle(), false)
            vehicleDialogFragment!!.setRiders(LinkedList<User>())
            vehicleDialogFragment!!.isCancelable = true
            vehicleDialogFragment!!.show(childFragmentManager, VehicleDialogFragment::class.java.simpleName)
        }

        swipeRefreshLayout.setOnRefreshListener {
            empty.visibility = View.GONE
            recyclerView.visibility = View.GONE
            vehiclesPresenter.getVehicles()
        }

        adapter = VehiclesRecyclerViewAdapter(vehiclesPresenter)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = this@VehiclesFragment.adapter
        }

        qr.visibility = View.VISIBLE

        qr.setOnClickListener {
            showQr(vehiclesPresenter.getVehicle()!!)
        }

        settings.setOnClickListener {
            showSettingsVehicle()
        }

        close.visibility = View.VISIBLE

        close.setOnClickListener {
            chronometer.stop()
            vehiclesPresenter.disconnectMqtt()
            adapter.setSelected(-1)
            vehicleContainer.visibility = View.GONE
        }

        chronometer.onChronometerTickListener = Chronometer.OnChronometerTickListener {
            val time = SystemClock.elapsedRealtime() - it.base
            val h = (time / 3600000).toInt()
            val m = (time - h * 3600000).toInt() / 60000
            val s = (time - h * 3600000 - m * 60000).toInt() / 1000
            val hh = if (h < 10) "0$h" else h.toString() + ""
            val mm = if (m < 10) "0$m" else m.toString() + ""
            val ss = if (s < 10) "0$s" else s.toString() + ""
            it.text = "$hh:$mm:$ss"
        }
        chronometer.text = "00:00:00"

        val supportMapFragment = childFragmentManager.findFragmentById(R.id.vehiclesMap) as SupportMapFragment?
        supportMapFragment?.getMapAsync { googleMap ->
            this@VehiclesFragment.googleMap = googleMap
            this@VehiclesFragment.googleMap.uiSettings.isMapToolbarEnabled = false

            this@VehiclesFragment.googleMap.setOnMapLoadedCallback {
                vehiclesPresenter.getVehicles()
            }
            this@VehiclesFragment.googleMap.setOnMarkerClickListener {
                vehiclesPresenter.selectVehicle(it.tag as String)
                false
            }
            this@VehiclesFragment.googleMap.setInfoWindowAdapter(
                VehicleInfoWindowAdapter(
                    layoutInflater
                )
            )
            this@VehiclesFragment.googleMap.setOnInfoWindowClickListener {
                vehiclesPresenter.selectVehicle(it.tag as String)
            }
        }

        mapListSegmentedButtonGroup.setOnPositionChangedListener {
            when(it) {
                0 -> {
                    addView.visibility = View.VISIBLE
                    listContainer.visibility = View.VISIBLE
                    val animSlideIn = AnimationUtils.loadAnimation(
                        parentFragment!!.activity,
                        R.anim.slide_out_right
                    )
                    animSlideIn.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {

                        }

                        override fun onAnimationEnd(animation: Animation?) {
                            add.visibility = View.VISIBLE
                        }

                        override fun onAnimationRepeat(animation: Animation?) {

                        }

                    })
                    animSlideIn.fillBefore = true
                    animSlideIn.fillAfter = true
                    addView.startAnimation(animSlideIn)
                    listContainer.startAnimation(animSlideIn)
                }
                1 -> {
                    val animSlideOut = AnimationUtils.loadAnimation(
                        parentFragment!!.activity,
                        R.anim.slide_out_left
                    )
                    animSlideOut.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {
                            if (adapter.getSelected() != -1) {
                                add.visibility = View.INVISIBLE
                            }
                        }

                        override fun onAnimationEnd(animation: Animation?) {
                            addView.visibility = View.GONE
                            listContainer.visibility = View.GONE
                        }

                        override fun onAnimationRepeat(animation: Animation?) {

                        }

                    })
                    animSlideOut.fillBefore = true
                    animSlideOut.fillAfter = false
                    addView.startAnimation(animSlideOut)
                    listContainer.startAnimation(animSlideOut)
                }
            }
        }

    }

    override fun setVehicleTypes(vehicleTypes: LinkedList<VehicleType>) {

    }

    override fun setVehicleStatuses(vehicleStatuses: LinkedList<VehicleStatus>) {

    }

    override fun setVehicles(vehicles: LinkedList<Vehicle>) {
        vehiclesPresenter.setVehicles(vehicles)
    }

    override fun showVehicles(vehicles: LinkedList<Vehicle>) {
        swipeRefreshLayout.isRefreshing = false
        if(vehicles.size > 0) {
            empty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        } else {
            empty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
        adapter.notifyDataSetChanged()
    }

    override fun showVehicleLogs(vehicleLogs: HashMap<String, VehicleLog>) {
        googleMap.clear()
        vehicleMarkers.clear()
        val builder = LatLngBounds.Builder()
        for(vehicleLog in vehicleLogs.values) {

            val lat = vehicleLog.lat?.toDoubleOrNull()
            val long = vehicleLog.long?.toDoubleOrNull()

            if(lat != null && long != null) {

                val position = LatLng(lat, long)
                builder.include(position)

                val markerOptions = MarkerOptions().apply {
                    position(position)
                    icon(
                        ViewUtil.vectorDrawableToBitmapDescriptor(
                            activity,
                            R.drawable.marker_scooter
                        )
                    )
                    title(vehicleLog.vehicle.name)
                }

                val marker = googleMap.addMarker(markerOptions)
                marker.tag = vehicleLog.vehicle.id
                vehicleMarkers[vehicleLog.vehicle.id] = marker

            }

        }

        if(vehicleMarkers.size > 1) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100))
        } else {
            vehicleMarkers.values.forEach { marker ->
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        marker.position,
                        15f
                    )
                )
                marker.showInfoWindow()
            }
        }

    }

    override fun setVehicle(vehicle: Vehicle, selected: Boolean) {

    }

    override fun showVehicle(vehicle: Vehicle?) {

    }

    override fun selectVehicle(position: Int, vehicle: Vehicle) {

        time.text = getString(R.string.last_update)
        temperature.text = getString(R.string.empty)
        humidity.text = getString(R.string.empty)
        iaq.text = getString(R.string.empty)
        pressure.text = getString(R.string.empty)
        altitude.text = getString(R.string.empty)

        status.text = "Connecting to\n${vehicle.name} ..."

        lock.visibility = View.GONE

        emergencyAlarm.visibility = View.GONE

        chronometerContainer.visibility = View.GONE

        adapter.setSelected(position)

        vehicleContainer.visibility = if (position == -1) View.GONE else View.VISIBLE

        if (position != -1) {
            if (mapListSegmentedButtonGroup.position == 1) {
                add.visibility = View.INVISIBLE
            } else {
                mapListSegmentedButtonGroup.setPosition(1, true)
            }
        }

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
                    status.text = "${vehicle.name} is ${vehicleLog.message}"
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

                if (fromMqtt) {
                    val marker = vehicleMarkers[vehicle.id]!!

                    val lat = vehicleLog.lat?.toDoubleOrNull()
                    val long = vehicleLog.long?.toDoubleOrNull()

                    if (lat != null && long != null && (marker.position.latitude != lat || marker.position.longitude != long)) {

                        marker.hideInfoWindow()

                        val latLngInterpolator: LatLngInterpolator = LatLngInterpolator.Spherical()

                        val startPosition: LatLng = marker.position
                        val handler = Handler()
                        val start = SystemClock.uptimeMillis()
                        val interpolator: Interpolator = LinearInterpolator()
                        val durationInMs = 3000f

                        handler.post(object : Runnable {
                            var elapsed: Long = 0
                            var t = 0f
                            var v = 0f
                            override fun run() {
                                elapsed = SystemClock.uptimeMillis() - start
                                t = elapsed / durationInMs
                                v = interpolator.getInterpolation(t)
                                val position = latLngInterpolator.interpolate(
                                    v,
                                    startPosition,
                                    LatLng(lat, long)
                                )
                                marker.position = position
                                googleMap.animateCamera(CameraUpdateFactory.newLatLng(position))
                                if (t < 1) {
                                    handler.postDelayed(this, 16)
                                } else {
                                    marker.showInfoWindow()
                                }
                            }
                        })

                    }
                } else {
                    for (vehicleId in vehicleMarkers.keys) {
                        val marker = vehicleMarkers[vehicleId]!!
                        marker.isVisible = vehicleId == vehicle.id
                        if (vehicleId == vehicle.id) {
                            googleMap.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    marker.position,
                                    15f
                                )
                            )
                            marker.showInfoWindow()
                        }
                    }
                }
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
        val connectOptions = MqttConnectOptions()
        connectOptions.userName = BuildConfig.MQTT_USERNAME
        connectOptions.password = BuildConfig.MQTT_PASSWORD.toCharArray()
        vehiclesPresenter.initializeMqtt(
            MqttAndroidClient(
                activity,
                BuildConfig.MQTT_BROKER,
                MqttClient.generateClientId()
            ), connectOptions
        )
    }

    override fun showErrorLabel(error: String?) {
        if(vehicleDialogFragment != null) {
            vehicleDialogFragment!!.showErrorLabel(error)
        }
    }

    override fun showErrorSerialNumber(error: String?) {
        if(vehicleDialogFragment != null) {
            vehicleDialogFragment!!.showErrorSerialNumber(error)
        }
    }

    override fun showErrorBrand(error: String?) {
        if(vehicleDialogFragment != null) {
            vehicleDialogFragment!!.showErrorBrand(error)
        }
    }

    override fun showErrorModel(error: String?) {
        if(vehicleDialogFragment != null) {
            vehicleDialogFragment!!.showErrorModel(error)
        }
    }

    override fun showMessage(message: String) {

    }

    override fun updateVehicle(vehicle: Vehicle) {
        vehiclesPresenter.updateVehicles(vehicle)
    }

    override fun showSettingsVehicle() {
        if(vehicleDialogFragment != null) {
            vehicleDialogFragment!!.dismiss()
        }
        vehicleDialogFragment = VehicleDialogFragment(settings = true, add = false)
        vehicleDialogFragment!!.setVehicleTypes(vehiclesPresenter.getVehicleTypes())
        vehicleDialogFragment!!.setVehicleStatuses(vehiclesPresenter.getVehicleStatuses())
        vehicleDialogFragment!!.setVehicles(vehiclesPresenter.getRiderVehicles())
        vehicleDialogFragment!!.setVehicle(vehiclesPresenter.getVehicle()!!, false)
        vehicleDialogFragment!!.setRiders(vehiclesPresenter.getRiders())
        vehicleDialogFragment!!.isCancelable = false
        vehicleDialogFragment!!.show(parentFragment!!.childFragmentManager, VehicleDialogFragment::class.java.simpleName)
    }

    override fun showRider(vehicle: Vehicle?) {

    }

    override fun updateSettingsVehicle(vehicle: Vehicle) {

    }

    override fun setRiders(riders: LinkedList<User>) {
        vehiclesPresenter.setRiders(riders)
    }

    override fun showQr(vehicle: Vehicle?) {
        if(vehicle != null) {
            val dialog = Dialog(activity!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_vehicle)

            dialog.findViewById<ImageButton>(R.id.close).setOnClickListener {
                dialog.dismiss()
            }

            dialog.findViewById<TextView>(R.id.name).text = vehicle.name

            val progress = dialog.findViewById<ProgressBar>(R.id.progress)
            val qr = dialog.findViewById<ImageView>(R.id.qr)

            Glide.with(activity!!).load(BuildConfig.UPLOADS_URL + vehicle.qr)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(RequestOptions.centerInsideTransform())
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable?>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progress.visibility = View.GONE
                        qr.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable?>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progress.visibility = View.GONE
                        qr.visibility = View.VISIBLE
                        return false
                    }

                }).into(qr)

            dialog.show()
        }
    }

}