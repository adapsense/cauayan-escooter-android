package com.adapsense.escooter.analytics.views

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.adapsense.escooter.R
import com.adapsense.escooter.analytics.AnalyticsContract
import com.adapsense.escooter.analytics.AnalyticsPresenter
import com.adapsense.escooter.api.models.objects.AnalyticsType
import com.adapsense.escooter.api.models.objects.Trip
import com.adapsense.escooter.api.models.objects.User
import com.adapsense.escooter.api.models.objects.Vehicle
import com.adapsense.escooter.base.BaseFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.android.synthetic.main.fragment_analytics.*
import java.util.*

class AnalyticsFragment: BaseFragment(), AnalyticsContract.View {

    private lateinit var analyticsPresenter: AnalyticsContract.Presenter

    private var calendar: Calendar? = null

    companion object {

        @JvmStatic
        fun newInstance() = AnalyticsFragment()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        analyticsPresenter = AnalyticsPresenter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_analytics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        analyticsPresenter.start()
        analyticsPresenter.getTypes()
    }

    override fun setPresenter(presenter: AnalyticsContract.Presenter?) {
        analyticsPresenter = presenter!!
    }

    override fun setupViews() {
        generate.setOnClickListener {
            analyticsPresenter.setAnalytics(calendar!!.time)
        }
    }

    override fun showAnalyticsTypes(analyticsTypes: LinkedList<AnalyticsType>) {
        val adapter: ArrayAdapter<AnalyticsType> = ArrayAdapter<AnalyticsType>(
            activity!!,
            R.layout.spinner,
            analyticsTypes
        )
        adapter.setDropDownViewResource(R.layout.item_spinner)
        type.adapter = adapter
        type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                analyticsPresenter.setType(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
        typeContainer.visibility = View.VISIBLE
    }

    override fun showUsers(show: Boolean, users: LinkedList<User>) {
        if(show && users.size > 0) {
            val adapter: ArrayAdapter<User> = ArrayAdapter<User>(
                activity!!,
                R.layout.spinner,
                users
            )
            adapter.setDropDownViewResource(R.layout.item_spinner)
            user.adapter = adapter
            user.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
            userContainer.visibility = View.VISIBLE
        } else {
            userContainer.visibility = View.GONE
        }

    }

    override fun showVehicles(show: Boolean, vehicles: LinkedList<Vehicle>) {
        if(show && vehicles.size > 0) {
            val adapter: ArrayAdapter<Vehicle> = ArrayAdapter<Vehicle>(
                activity!!,
                R.layout.spinner,
                vehicles
            )
            adapter.setDropDownViewResource(R.layout.item_spinner)
            vehicle.adapter = adapter
            vehicle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    analyticsPresenter.setVehicle(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
            vehicleContainer.visibility = View.VISIBLE
        } else {
            vehicleContainer.visibility = View.GONE
        }
    }

    override fun showDate(show: Boolean) {
        if(show) {
            calendar = Calendar.getInstance()
            calendarView.maxDate = calendar!!.timeInMillis
            calendarView.setOnDateChangeListener { _, year, month, day ->
                calendar!![Calendar.YEAR] = year
                calendar!![Calendar.MONTH] = month
                calendar!![Calendar.DAY_OF_MONTH] = day
            }
            dateContainer.visibility = View.VISIBLE
        } else {
            dateContainer.visibility = View.GONE
        }
    }

    override fun showTrips(show: Boolean, trips: LinkedList<Trip>) {
        if(show && trips.size > 0) {
            val adapter: ArrayAdapter<Trip> = ArrayAdapter<Trip>(
                activity!!,
                R.layout.spinner,
                trips
            )
            adapter.setDropDownViewResource(R.layout.item_spinner)
            trip.adapter = adapter
            trip.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    analyticsPresenter.setTrip(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
            tripContainer.visibility = View.VISIBLE
        } else {
            tripContainer.visibility = View.GONE
        }
    }

    override fun showAnalyticsChart() {
        startActivity(Intent(activity, AnalyticsChartActivity::class.java))
        activity!!.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
    }

    override fun showAnalyticsMap() {
        startActivity(Intent(activity, AnalyticsMapActivity::class.java))
        activity!!.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
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

    }

    override fun showError(error: String) {

    }

}