package com.adapsense.escooter.analytics

import com.adapsense.escooter.api.models.objects.AnalyticsType
import com.adapsense.escooter.api.models.objects.Trip
import com.adapsense.escooter.api.models.objects.User
import com.adapsense.escooter.api.models.objects.Vehicle
import com.adapsense.escooter.base.BasePresenter
import com.adapsense.escooter.base.BaseView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import java.util.*

interface AnalyticsContract {

    interface View : BaseView<Presenter?> {

        fun showAnalyticsTypes(analyticsTypes: LinkedList<AnalyticsType>)

        fun showUsers(show: Boolean, users: LinkedList<User>)

        fun showVehicles(show: Boolean, vehicles: LinkedList<Vehicle>)

        fun showDate(show: Boolean)

        fun showTrips(show: Boolean, trips: LinkedList<Trip>)

        fun showAnalyticsChart()

        fun showAnalyticsMap()

        fun showVehicleLogs(analyticsType: AnalyticsType, vehicle: Vehicle, date: String, unit: String, time: List<String>, vehicleLogs: List<String>)

        fun showTrip(vehicleValue: String, unlockTimeValue: String?, lockTimeValue: String?, durationValue: String?, route: LinkedList<LatLng>, bounds: LatLngBounds)

        fun showError(error: String)

    }

    interface Presenter : BasePresenter {

        fun getTypes()

        fun setType(position: Int)

        fun getUsers()

        fun setUser(position: Int)

        fun getVehicles()

        fun setVehicle(position: Int)

        fun getTrips(vehicle: Vehicle)

        fun setTrip(position: Int)

        fun setAnalytics(date: Date)

        fun getAnalytics()

        fun getTripDuration(trip: Trip): String

    }

}
