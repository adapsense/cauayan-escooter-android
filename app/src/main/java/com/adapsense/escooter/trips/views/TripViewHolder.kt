package com.adapsense.escooter.trips.views

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.adapsense.escooter.R
import com.adapsense.escooter.api.models.objects.Trip
import com.adapsense.escooter.trips.TripsContract
import com.adapsense.escooter.util.CacheUtil
import kotlinx.android.synthetic.main.item_trip.view.*
import java.text.SimpleDateFormat
import java.util.*

class TripViewHolder(private var tripsPresenter: TripsContract.Presenter, itemView: View?) : ViewHolder(itemView!!), TripsContract.View {

	init {
        setupViews()
    }

	override fun setPresenter(presenter: TripsContract.Presenter?) {
		tripsPresenter = presenter!!
	}

	override fun setupViews() {

	}

	override fun showTrips(trips: LinkedList<Trip>) {

	}

	override fun setTrip(trip: Trip) {

		itemView.vehicle.text = if(CacheUtil.instance!!.user!!.userType.isAdmin) trip.vehicle.name else trip.vehicle.label

		val fromSdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
		fromSdf.timeZone = TimeZone.getTimeZone("GMT+0")

		val toSdf = SimpleDateFormat("MMM d, yyyy\nh:mm a", Locale.ENGLISH)
		toSdf.timeZone = TimeZone.getDefault()

		if(trip.unlockTime != null) {
			val unlockTime = fromSdf.parse(trip.unlockTime!!)
			itemView.unlockTime.text = toSdf.format(unlockTime!!)
		} else {
			itemView.unlockTime.text = itemView.context.getString(R.string.empty)
		}

		if(trip.lockTime != null) {
			val lockTime = fromSdf.parse(trip.lockTime!!)
			itemView.lockTime.text = toSdf.format(lockTime!!)
		} else {
			itemView.lockTime.text = itemView.context.getString(R.string.empty)
		}

		if(trip.unlockTime != null && trip.lockTime != null) {
			itemView.duration.text = tripsPresenter.getTripDuration(trip)
		} else {
			itemView.duration.text = itemView.context.getString(R.string.empty)
		}

	}

}
