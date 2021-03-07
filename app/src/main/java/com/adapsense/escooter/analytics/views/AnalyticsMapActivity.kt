package com.adapsense.escooter.analytics.views

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.adapsense.escooter.R
import com.adapsense.escooter.analytics.AnalyticsContract
import com.adapsense.escooter.analytics.AnalyticsPresenter
import com.adapsense.escooter.api.models.objects.AnalyticsType
import com.adapsense.escooter.api.models.objects.Trip
import com.adapsense.escooter.api.models.objects.User
import com.adapsense.escooter.api.models.objects.Vehicle
import com.adapsense.escooter.base.BaseActivity
import com.adapsense.escooter.util.CacheUtil
import com.adapsense.escooter.util.ViewUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.activity_analytics_map.*
import kotlinx.android.synthetic.main.navigation.*
import java.util.*

class AnalyticsMapActivity : BaseActivity(), AnalyticsContract.View {

    private lateinit var analyticsPresenter: AnalyticsContract.Presenter

    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics_map)
        analyticsPresenter = AnalyticsPresenter(this)
    }

    override fun onResume() {
        super.onResume()
        showProgressDialog()
        analyticsPresenter.start()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right)
    }

    override fun setPresenter(presenter: AnalyticsContract.Presenter?) {
        analyticsPresenter = presenter!!
    }

    override fun setupViews() {
        back.visibility = View.VISIBLE
        back.setOnClickListener {
            onBackPressed()
        }
        titleText.text = CacheUtil.instance!!.analyticsType!!.name

        val supportMapFragment = supportFragmentManager.findFragmentById(R.id.analyticsMap) as SupportMapFragment?
        supportMapFragment?.getMapAsync { map ->
            run {
                googleMap = map
                googleMap!!.uiSettings.isMapToolbarEnabled = false
                googleMap!!.apply {
                    uiSettings.isMapToolbarEnabled = false
                    setOnMapLoadedCallback {
                        analyticsPresenter.getAnalytics()
                    }
                }
            }
        }

    }


    override fun showAnalyticsTypes(analyticsTypes: LinkedList<AnalyticsType>) {

    }

    override fun showUsers(show: Boolean, users: LinkedList<User>) {

    }

    override fun showVehicles(show: Boolean, vehicles: LinkedList<Vehicle>) {

    }

    override fun showDate(show: Boolean) {

    }

    override fun showTrips(show: Boolean, trips: LinkedList<Trip>) {

    }

    override fun showAnalyticsChart() {

    }

    override fun showAnalyticsMap() {

    }

    override fun showVehicleLogs(
        analyticsType: AnalyticsType,
        vehicle: Vehicle,
        date: String,
        unit: String,
        time: List<String>,
        vehicleLogs: List<String>
    ) {

    }

    override fun showTrip(
        vehicleValue: String,
        unlockTimeValue: String?,
        lockTimeValue: String?,
        durationValue: String?,
        route: LinkedList<LatLng>,
        bounds: LatLngBounds
    ) {
        vehicle.text = vehicleValue

        if(unlockTimeValue != null) {
            unlockTime.text = unlockTimeValue
            unlockTimeContainer.visibility = View.VISIBLE
        } else {
            unlockTimeContainer.visibility = View.GONE
        }

        if(lockTimeValue != null) {
            lockTime.text = lockTimeValue
            lockTimeContainer.visibility = View.VISIBLE
        } else {
            lockTimeContainer.visibility = View.GONE
        }

        if(durationValue != null) {
            duration.text = durationValue
            durationContainer.visibility = View.VISIBLE
        } else {
            durationContainer.visibility = View.GONE
        }

        if(route.size >= 2) {

            val startMarkerOptions = MarkerOptions().apply {
                position(route[0])
                icon(
                    ViewUtil.vectorDrawableToBitmapDescriptor(
                        this@AnalyticsMapActivity,
                        R.drawable.marker_start
                    )
                )
            }
            googleMap!!.addMarker(startMarkerOptions)

            val endMarkerOptions = MarkerOptions().apply {
                position(route[route.size - 1])
                icon(
                    ViewUtil.vectorDrawableToBitmapDescriptor(
                        this@AnalyticsMapActivity,
                        R.drawable.marker_end
                    )
                )
            }
            googleMap!!.addMarker(endMarkerOptions)

            val polylineOptions = PolylineOptions()
            polylineOptions.color(ContextCompat.getColor(this, R.color.warning))
            polylineOptions.addAll(route)
            googleMap!!.addPolyline(polylineOptions)

            googleMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        }

        dismissDialogs()
    }

    override fun showError(error: String) {

    }

}