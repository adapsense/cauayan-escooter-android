package com.adapsense.escooter.dashboard

import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.viewpager.widget.ViewPager
import com.adapsense.escooter.R
import com.adapsense.escooter.api.models.objects.User
import com.adapsense.escooter.api.models.objects.Vehicle
import com.adapsense.escooter.api.models.objects.VehicleLog
import com.adapsense.escooter.base.BaseFragment
import com.adapsense.escooter.home.HomeActivity
import com.adapsense.escooter.maps.LatLngInterpolator
import com.adapsense.escooter.util.CacheUtil
import com.adapsense.escooter.util.ViewUtil
import com.adapsense.escooter.vehicles.adapters.VehicleInfoWindowAdapter
import com.adapsense.escooter.vehicles.adapters.VehiclesFragmentPagerAdapter
import com.adapsense.escooter.vehicles.view.VehicleDialogFragment
import com.adapsense.escooter.vehicles.view.VehiclesFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_dashboard.*
import java.util.*
import kotlin.collections.HashMap

class DashboardFragment: BaseFragment(), DashboardContract.View {

    private var dashboardPresenter: DashboardContract.Presenter? = null

    private lateinit var dashboardAdapter: DashboardFragmentPagerAdapter
    private lateinit var vehiclesAdapter: VehiclesFragmentPagerAdapter

    private var googleMap: GoogleMap? = null

    private val vehicleMarkers = HashMap<String, Marker>()

    companion object {

        @JvmStatic
        fun newInstance() = DashboardFragment()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        dashboardPresenter = DashboardPresenter(this)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HomeActivity).showProgressDialog()
        dashboardPresenter?.let {
            it.start()
            it.getUser()
        }
    }

    override fun setPresenter(presenter: DashboardContract.Presenter?) {
        dashboardPresenter = presenter!!
    }

    override fun setupViews() {

        scan.setOnClickListener {
            (activity as HomeActivity).showScan()
        }

    }

    override fun showAdmin() {

        (activity as HomeActivity).dismissDialogs()

        adminContainer.visibility = View.VISIBLE
        riderContainer.visibility = View.GONE

        dashboardAdapter = DashboardFragmentPagerAdapter(childFragmentManager)

        adminViewPager.adapter = dashboardAdapter
        adminViewPager.offscreenPageLimit = 3

        tabLayout.setupWithViewPager(adminViewPager)

    }

    override fun showRider() {

        (activity as HomeActivity).dismissDialogs()

        adminContainer.visibility = View.GONE
        riderContainer.visibility = View.VISIBLE

        scan.visibility = View.GONE
        if(vehiclesProgress != null) {
            vehiclesProgress.visibility = View.VISIBLE
        }

        val supportMapFragment = childFragmentManager.findFragmentById(R.id.riderMap) as SupportMapFragment?
        supportMapFragment?.getMapAsync { map ->
            run {
                googleMap = map
                googleMap!!.uiSettings.isMapToolbarEnabled = false
                googleMap!!.apply {
                    uiSettings.isMapToolbarEnabled = false
                    setInfoWindowAdapter(VehicleInfoWindowAdapter(layoutInflater))
                    setOnMapLoadedCallback {
                        dashboardPresenter?.let {
                            it.getVehicles()
                        }
                    }
                }
            }
        }

    }

    override fun showVehicles(vehicles: LinkedList<Vehicle>) {

        if(vehiclesProgress != null) {
            vehiclesProgress.visibility = View.GONE
        }

        googleMap!!.clear()

        vehicleMarkers.clear()

        (activity as HomeActivity).hideScan(vehicles.size == 0)

        if(vehicles.size > 0) {
            scan.visibility = View.GONE

            vehiclesAdapter = VehiclesFragmentPagerAdapter(childFragmentManager, vehicles)

            riderViewPager.visibility = View.VISIBLE
            riderViewPager.adapter = vehiclesAdapter
            riderViewPager.offscreenPageLimit = vehicles.size - 1

            dotsIndicator.setViewPager(riderViewPager)
            dotsIndicator.visibility = if(vehicles.size > 1) View.VISIBLE else View.GONE

            riderViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                ) {
                    dashboardPresenter?.let {
                        if (position < vehicleMarkers.size) {
                            vehicleMarkers[it.getVehicle(position).id]?.let { marker ->
                                showVehicleMarker(marker)
                            }
                        }
                    }
                }

                override fun onPageSelected(position: Int) {
                    dashboardPresenter?.let {
                        if (position < vehicleMarkers.size) {
                            vehicleMarkers[it.getVehicle(position).id]?.let { marker ->
                                showVehicleMarker(marker)
                            }
                        }
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {

                }

            })

        } else {
            scan.visibility = View.VISIBLE
            riderViewPager.visibility = View.GONE
        }

    }

    override fun showVehicle(vehicle: Vehicle) {
        dashboardPresenter?.let {
            val vehicleDialogFragment = VehicleDialogFragment(settings = false, add = false)
            vehicleDialogFragment.setVehicleTypes(LinkedList())
            vehicleDialogFragment.setVehicleStatuses(LinkedList())
            vehicleDialogFragment.setVehicles(it.getRiderVehicles())
            vehicleDialogFragment.setVehicle(vehicle, false)
            vehicleDialogFragment.isCancelable = false
            vehicleDialogFragment.show(
                    activity!!.supportFragmentManager,
                    VehicleDialogFragment::class.java.simpleName
            )
        }
    }

    override fun updateVehicle(settings: Boolean, vehicle: Vehicle) {
        dashboardPresenter?.let {
            it.updateVehicle(settings, vehicle)
        }
        if(CacheUtil.instance!!.user!!.userType.isAdmin) {
            (dashboardAdapter.getItem(0) as VehiclesFragment).updateVehicle(vehicle)
        }
    }

    override fun showVehicleLog(vehicle: Vehicle, vehicleLog: VehicleLog) {

        var lat = vehicleLog.lat?.toDoubleOrNull()
        var long = vehicleLog.long?.toDoubleOrNull()

        if(vehicleLog.lat == "NaN" || vehicleLog.long == "NaN" || lat == null || long == null) {
            lat = 0.0
            long = 0.0
        }

        if(lat != 0.0 && long != 0.0) {

            if (vehicleMarkers[vehicle.id] == null) {

                val markerOptions = MarkerOptions().apply {
                    position(LatLng(lat, long))
                    icon(
                            ViewUtil.vectorDrawableToBitmapDescriptor(
                                    activity,
                                    R.drawable.marker_scooter
                            )
                    )
                    title(if (vehicle.label.isNotEmpty()) vehicle.label else vehicle.name)

                }

                vehicleMarkers[vehicle.id] = googleMap!!.addMarker(markerOptions)

                if (vehicleMarkers.size == 1) {
                    showVehicleMarker(vehicleMarkers[vehicle.id]!!)
                }

            } else {

                val marker = vehicleMarkers[vehicle.id]!!

                if (marker.position.latitude != lat || marker.position.longitude != long) {

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
                            googleMap!!.animateCamera(CameraUpdateFactory.newLatLng(position))
                            if (t < 1) {
                                handler.postDelayed(this, 16)
                            } else {
                                marker.showInfoWindow()
                            }
                        }
                    })

                }
            }
        }

    }

    override fun showVehicleMarker(marker: Marker) {
        googleMap?.let {
            it.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                            marker.position,
                            15f
                    )
            )
            marker.showInfoWindow()
        }
    }

    override fun setRiders(riders: LinkedList<User>) {
        if(CacheUtil.instance!!.user!!.userType.isAdmin) {
            (dashboardAdapter.getItem(0) as VehiclesFragment).setRiders(riders)
        }
    }

}