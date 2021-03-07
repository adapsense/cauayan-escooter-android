package com.adapsense.escooter.trips

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.adapsense.escooter.R
import com.adapsense.escooter.trips.views.TripViewHolder

class TripsRecyclerViewAdapter(private val tripsPresenter: TripsContract.Presenter) : Adapter<TripViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        return TripViewHolder(tripsPresenter, LayoutInflater.from(parent.context).inflate(R.layout.item_trip, parent, false))
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        tripsPresenter.onBindTripViewAtPosition(position, holder)
    }

    override fun getItemCount(): Int {
        return tripsPresenter.getTripsCount()
    }

}
