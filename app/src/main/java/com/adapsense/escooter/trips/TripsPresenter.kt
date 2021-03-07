package com.adapsense.escooter.trips

import com.adapsense.escooter.api.ApiRequestListener
import com.adapsense.escooter.api.calls.TripsApiCall
import com.adapsense.escooter.api.models.objects.Trip
import com.adapsense.escooter.api.models.responses.TripsResponse
import com.adapsense.escooter.trips.views.TripViewHolder
import com.adapsense.escooter.util.AppLogger
import com.adapsense.escooter.util.CacheUtil
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class TripsPresenter(private val tripsView: TripsContract.View) : TripsContract.Presenter {

    private val trips: LinkedList<Trip> = LinkedList()

    private var vehicle = ""
    private var unlockTime = "descending"
    private var lockTime = ""
    private var duration = ""

    init {
        tripsView.setPresenter(this)
    }

    override fun start() {
        tripsView.setupViews()
    }

    override fun getTrips() {
        trips.clear()
        TripsApiCall.get(if(CacheUtil.instance!!.user!!.userType.isAdmin) null else CacheUtil.instance!!.user, object : ApiRequestListener {
            override fun onSuccess(obj: Any) {
                val tripsResponse = Gson().fromJson(obj.toString(), TripsResponse::class.java)
                if(tripsResponse.success) {
                    trips.addAll(tripsResponse.trips!!)
                }
                sortTrips()
            }

            override fun onError(obj: Any) {
                AppLogger.printRetrofitError(obj)
                sortTrips()

            }
        })
    }

    override fun sortTrips() {
        when {
            vehicle == "descending" -> {
                trips.sortWith(Comparator { o1, o2 -> if(CacheUtil.instance!!.user!!.userType.isAdmin) o2.vehicle.name.compareTo(o1.vehicle.name) else o2.vehicle.label.compareTo(o1.vehicle.label) })
            }
            vehicle == "ascending" -> {
                trips.sortWith(Comparator { o1, o2 -> if(CacheUtil.instance!!.user!!.userType.isAdmin) o1.vehicle.name.compareTo(o2.vehicle.name) else o1.vehicle.label.compareTo(o2.vehicle.label) })
            }
            unlockTime == "descending" -> {
                trips.sortWith(Comparator { o1, o2 -> if(o1.unlockTime != null && o2.unlockTime != null) o2.unlockTime!!.compareTo(o1.unlockTime!!) else 0 })
            }
            unlockTime == "ascending" -> {
                trips.sortWith(Comparator { o1, o2 -> if(o1.unlockTime != null && o2.unlockTime != null) o1.unlockTime!!.compareTo(o2.unlockTime!!) else 0 })
            }
            lockTime == "descending" -> {
                trips.sortWith(Comparator { o1, o2 -> if(o1.lockTime != null && o2.lockTime != null) o2.lockTime!!.compareTo(o1.lockTime!!) else 0 })
            }
            lockTime == "ascending" -> {
                trips.sortWith(Comparator { o1, o2 -> if(o1.lockTime != null && o2.lockTime != null) o1.lockTime!!.compareTo(o2.lockTime!!) else 0 })
            }
            duration == "descending" -> {
                trips.sortWith(Comparator { o1, o2 -> if(o1.unlockTime != null && o1.lockTime != null && o2.unlockTime != null && o2.lockTime != null) {
                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH)
                    sdf.timeZone = TimeZone.getTimeZone("GMT+0")
                    val unlockTime1 = sdf.parse(o1.unlockTime!!)
                    val lockTime1 = sdf.parse(o1.lockTime!!)
                    val time1 = lockTime1!!.time - unlockTime1!!.time
                    val unlockTime2 = sdf.parse(o2.unlockTime!!)
                    val lockTime2 = sdf.parse(o2.lockTime!!)
                    val time2 = lockTime2!!.time - unlockTime2!!.time
                    time2.compareTo(time1)
                } else 0 })
            }
            duration == "ascending" -> {
                trips.sortWith(Comparator { o1, o2 -> if(o1.unlockTime != null && o1.lockTime != null && o2.unlockTime != null && o2.lockTime != null) {
                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH)
                    sdf.timeZone = TimeZone.getTimeZone("GMT+0")
                    val unlockTime1 = sdf.parse(o1.unlockTime!!)
                    val lockTime1 = sdf.parse(o1.lockTime!!)
                    val time1 = lockTime1!!.time - unlockTime1!!.time
                    val unlockTime2 = sdf.parse(o2.unlockTime!!)
                    val lockTime2 = sdf.parse(o2.lockTime!!)
                    val time2 = lockTime2!!.time - unlockTime2!!.time
                    time1.compareTo(time2)
                } else 0 })
            }
        }
        tripsView.showTrips(trips)
    }

    override fun onBindTripViewAtPosition(position: Int, holder: TripViewHolder) {
        holder.setTrip(trips[position])
    }

    override fun getTripsCount(): Int {
        return trips.size
    }

    override fun getTripDuration(trip: Trip): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("GMT+0")
        val unlockTime = sdf.parse(trip.unlockTime!!)
        val lockTime = sdf.parse(trip.lockTime!!)
        val time = lockTime!!.time - unlockTime!!.time
        val h = (time / 3600000).toInt()
        val m = (time - h * 3600000).toInt() / 60000
        val s = (time - h * 3600000 - m * 60000).toInt() / 1000
        val hh = if (h < 10) "0$h" else h.toString() + ""
        val mm = if (m < 10) "0$m" else m.toString() + ""
        val ss = if (s < 10) "0$s" else s.toString() + ""
        return "$hh:$mm:$ss"
    }

    override fun sortVehicle(sort: String) {
        vehicle = sort
        unlockTime = ""
        lockTime = ""
        duration = ""
        sortTrips()
    }

    override fun sortUnlockTime(sort: String) {
        vehicle = ""
        unlockTime = sort
        lockTime = ""
        duration = ""
        sortTrips()
    }

    override fun sortLockTime(sort: String) {
        vehicle = ""
        unlockTime = ""
        lockTime = sort
        duration = ""
        sortTrips()
    }

    override fun sortDuration(sort: String) {
        vehicle = ""
        unlockTime = ""
        lockTime = ""
        duration = sort
        sortTrips()
    }

}
