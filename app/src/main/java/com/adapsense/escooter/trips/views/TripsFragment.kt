package com.adapsense.escooter.trips.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapsense.escooter.R
import com.adapsense.escooter.api.models.objects.Trip
import com.adapsense.escooter.base.BaseFragment
import com.adapsense.escooter.trips.TripsContract
import com.adapsense.escooter.trips.TripsPresenter
import com.adapsense.escooter.trips.TripsRecyclerViewAdapter
import com.adapsense.escooter.util.AppLogger
import kotlinx.android.synthetic.main.fragment_trips.*
import java.util.*

class TripsFragment: BaseFragment(), TripsContract.View {

    private lateinit var tripsPresenter: TripsContract.Presenter

    private lateinit var adapter: TripsRecyclerViewAdapter

    companion object {

        @JvmStatic
        fun newInstance() = TripsFragment()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        tripsPresenter = TripsPresenter(this)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_trips, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tripsPresenter.start()
        swipeRefreshLayout.isRefreshing = true
        tripsPresenter.getTrips()
    }

    override fun setPresenter(presenter: TripsContract.Presenter?) {
        tripsPresenter = presenter!!
    }

    override fun setupViews() {
        vehicle.setOnClickListener {
            if(vehicleSort.visibility == View.GONE || vehicleSort.tag == "ascending") {
                vehicleSort.tag = "descending"
                vehicleSortDescending.setColorFilter(ContextCompat.getColor(activity!!, R.color.secondary_text), android.graphics.PorterDuff.Mode.SRC_IN)
                vehicleSortAscending.setColorFilter(ContextCompat.getColor(activity!!, R.color.divider), android.graphics.PorterDuff.Mode.SRC_IN)
                tripsPresenter.sortVehicle("descending")
            } else {
                vehicleSort.tag = "ascending"
                vehicleSortDescending.setColorFilter(ContextCompat.getColor(activity!!, R.color.divider), android.graphics.PorterDuff.Mode.SRC_IN)
                vehicleSortAscending.setColorFilter(ContextCompat.getColor(activity!!, R.color.secondary_text), android.graphics.PorterDuff.Mode.SRC_IN)
                tripsPresenter.sortVehicle("ascending")
            }
            vehicleSort.visibility = View.VISIBLE
            unlockTimeSort.visibility = View.GONE
            lockTimeSort.visibility = View.GONE
            durationSort.visibility = View.GONE
        }

        unlockTime.setOnClickListener {
            if(unlockTimeSort.visibility == View.GONE || unlockTimeSort.tag == "ascending") {
                unlockTimeSort.tag = "descending"
                unlockTimeSortDescending.setColorFilter(ContextCompat.getColor(activity!!, R.color.secondary_text), android.graphics.PorterDuff.Mode.SRC_IN)
                unlockTimeSortAscending.setColorFilter(ContextCompat.getColor(activity!!, R.color.divider), android.graphics.PorterDuff.Mode.SRC_IN)
                tripsPresenter.sortUnlockTime("descending")
            } else {
                unlockTimeSort.tag = "ascending"
                unlockTimeSortDescending.setColorFilter(ContextCompat.getColor(activity!!, R.color.divider), android.graphics.PorterDuff.Mode.SRC_IN)
                unlockTimeSortAscending.setColorFilter(ContextCompat.getColor(activity!!, R.color.secondary_text), android.graphics.PorterDuff.Mode.SRC_IN)
                tripsPresenter.sortUnlockTime("ascending")
            }
            vehicleSort.visibility = View.GONE
            unlockTimeSort.visibility = View.VISIBLE
            lockTimeSort.visibility = View.GONE
            durationSort.visibility = View.GONE
        }

        lockTime.setOnClickListener {
            if(lockTimeSort.visibility == View.GONE || lockTimeSort.tag == "ascending") {
                lockTimeSort.tag = "descending"
                lockTimeSortDescending.setColorFilter(ContextCompat.getColor(activity!!, R.color.secondary_text), android.graphics.PorterDuff.Mode.SRC_IN)
                lockTimeSortAscending.setColorFilter(ContextCompat.getColor(activity!!, R.color.divider), android.graphics.PorterDuff.Mode.SRC_IN)
                tripsPresenter.sortLockTime("descending")
            } else {
                lockTimeSort.tag = "ascending"
                lockTimeSortDescending.setColorFilter(ContextCompat.getColor(activity!!, R.color.divider), android.graphics.PorterDuff.Mode.SRC_IN)
                lockTimeSortAscending.setColorFilter(ContextCompat.getColor(activity!!, R.color.secondary_text), android.graphics.PorterDuff.Mode.SRC_IN)
                tripsPresenter.sortLockTime("ascending")
            }
            vehicleSort.visibility = View.GONE
            unlockTimeSort.visibility = View.GONE
            lockTimeSort.visibility = View.VISIBLE
            durationSort.visibility = View.GONE
        }

        duration.setOnClickListener {
            if(durationSort.visibility == View.GONE || durationSort.tag == "ascending") {
                durationSort.tag = "descending"
                durationSortDescending.setColorFilter(ContextCompat.getColor(activity!!, R.color.secondary_text), android.graphics.PorterDuff.Mode.SRC_IN)
                durationSortAscending.setColorFilter(ContextCompat.getColor(activity!!, R.color.divider), android.graphics.PorterDuff.Mode.SRC_IN)
                tripsPresenter.sortDuration("descending")
            } else {
                durationSort.tag = "ascending"
                durationSortDescending.setColorFilter(ContextCompat.getColor(activity!!, R.color.divider), android.graphics.PorterDuff.Mode.SRC_IN)
                durationSortAscending.setColorFilter(ContextCompat.getColor(activity!!, R.color.secondary_text), android.graphics.PorterDuff.Mode.SRC_IN)
                tripsPresenter.sortDuration("ascending")
            }
            vehicleSort.visibility = View.GONE
            unlockTimeSort.visibility = View.GONE
            lockTimeSort.visibility = View.GONE
            durationSort.visibility = View.VISIBLE
        }

        swipeRefreshLayout.setOnRefreshListener {
            empty.visibility = View.GONE
            recyclerView.visibility = View.GONE
            tripsPresenter.getTrips()
        }

        adapter = TripsRecyclerViewAdapter(tripsPresenter)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = this@TripsFragment.adapter
        }

    }

    override fun showTrips(trips: LinkedList<Trip>) {
        swipeRefreshLayout.isRefreshing = false
        if(trips.size > 0) {
            empty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        } else {
            empty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
        adapter.notifyDataSetChanged()
    }

    override fun setTrip(trip: Trip) {

    }

}