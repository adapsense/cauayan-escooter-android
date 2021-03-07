package com.adapsense.escooter.trips

import com.adapsense.escooter.api.models.objects.Trip
import com.adapsense.escooter.base.BasePresenter
import com.adapsense.escooter.base.BaseView
import com.adapsense.escooter.trips.views.TripViewHolder
import java.util.*

interface TripsContract {

    interface View : BaseView<Presenter?> {

        fun showTrips(trips: LinkedList<Trip>)

        fun setTrip(trip: Trip)

    }

    interface Presenter : BasePresenter {

        fun getTrips()

        fun sortTrips()

        fun onBindTripViewAtPosition(position: Int, holder: TripViewHolder)

        fun getTripsCount(): Int

        fun getTripDuration(trip: Trip): String

        fun sortVehicle(sort: String)

        fun sortUnlockTime(sort: String)

        fun sortLockTime(sort: String)

        fun sortDuration(sort: String)

    }

}
