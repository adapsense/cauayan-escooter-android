package com.adapsense.escooter.analytics

import com.adapsense.escooter.api.ApiRequestListener
import com.adapsense.escooter.api.calls.AnalyticsApiCall
import com.adapsense.escooter.api.calls.TripsApiCall
import com.adapsense.escooter.api.calls.UsersApiCall
import com.adapsense.escooter.api.calls.VehiclesApiCall
import com.adapsense.escooter.api.models.objects.AnalyticsType
import com.adapsense.escooter.api.models.objects.Trip
import com.adapsense.escooter.api.models.objects.User
import com.adapsense.escooter.api.models.objects.Vehicle
import com.adapsense.escooter.api.models.responses.*
import com.adapsense.escooter.util.AppLogger
import com.adapsense.escooter.util.CacheUtil
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class AnalyticsPresenter(private val analyticsView: AnalyticsContract.View) : AnalyticsContract.Presenter {

    private val analyticsTypes: LinkedList<AnalyticsType> = LinkedList()
    private var analyticsType: AnalyticsType? = null

    private var users: LinkedList<User> = LinkedList()
    private var user: User? = null

    private var vehicles = LinkedList<Vehicle>()
    private var vehicle: Vehicle? = null

    private var trips = LinkedList<Trip>()
    private var trip: Trip? = null

    init {
        analyticsView.setPresenter(this)
    }

    override fun start() {
        analyticsView.setupViews()
    }

    override fun getTypes() {
        AnalyticsApiCall.types(object : ApiRequestListener {
            override fun onSuccess(obj: Any) {
                val analyticsTypesResponse = Gson().fromJson(obj.toString(), AnalyticsTypesResponse::class.java)
                if(analyticsTypesResponse.success) {
                    analyticsTypes.clear()
                    analyticsTypes.addAll(analyticsTypesResponse.analyticsTypes!!)

                    if(!analyticsTypes.isEmpty()) {
                        analyticsView.showAnalyticsTypes(analyticsTypes)
                        setType(0)
                    }
                }
            }

            override fun onError(obj: Any) {
                AppLogger.printRetrofitError(obj)
            }

        })
    }

    override fun setType(position: Int) {
        analyticsType = analyticsTypes[position]

        if(users.isEmpty()) {
            getUsers()
        } else {
            analyticsView.showUsers(analyticsType!!.isUser, users)
        }

        if(vehicles.isEmpty()) {
            getVehicles()
        } else {
            analyticsView.showVehicles(analyticsType!!.isVehicle, vehicles)
        }

        analyticsView.showDate(analyticsType!!.isDate)

    }

    override fun getUsers() {
        UsersApiCall.get(object : ApiRequestListener {
            override fun onSuccess(obj: Any) {
                val usersResponse = Gson().fromJson(obj.toString(), UsersResponse::class.java)
                if(usersResponse.success) {
                    users.clear()
                    users.addAll(usersResponse.users!!)
                }
                analyticsView.showUsers(analyticsType!!.isUser, users)
                if(users.size > 0) {
                    setUser(0)
                }
            }

            override fun onError(obj: Any) {
                AppLogger.printRetrofitError(obj)
                users.clear()
                analyticsView.showUsers(analyticsType!!.isUser, users)
            }
        })
    }

    override fun setUser(position: Int) {
        user = users[position]
    }

    override fun getVehicles() {
        VehiclesApiCall.get(object : ApiRequestListener {
            override fun onSuccess(obj: Any) {
                val vehiclesResponse = Gson().fromJson(obj.toString(), VehiclesResponse::class.java)
                if(vehiclesResponse.success) {
                    vehicles.clear()
                    vehicles.addAll(vehiclesResponse.vehicles!!)
                }
                analyticsView.showVehicles(analyticsType!!.isVehicle, vehicles)
                if(vehicles.size > 0) {
                    setVehicle(0)
                }
            }

            override fun onError(obj: Any) {
                AppLogger.printRetrofitError(obj)
                vehicles.clear()
                analyticsView.showVehicles(analyticsType!!.isVehicle, vehicles)
            }
        })
    }

    override fun setVehicle(position: Int) {
        vehicle = vehicles[position]
        if(analyticsType!!.isTrip) {
            getTrips(vehicle!!)
        }
    }

    override fun getTrips(vehicle: Vehicle) {
        trips.clear()
        TripsApiCall.get(vehicle, object : ApiRequestListener {
            override fun onSuccess(obj: Any) {
                val tripsResponse = Gson().fromJson(obj.toString(), TripsResponse::class.java)
                if(tripsResponse.success) {
                    for(trip in tripsResponse.trips!!) {
                        if(trip.unlockTime != null && trip.lockTime != null) {
                            trips.add(trip)
                        }
                    }
                    trips.sortWith(Comparator { o1, o2 -> if(o1.unlockTime != null && o2.unlockTime != null) o2.unlockTime!!.compareTo(o1.unlockTime!!) else 0 })
                }
                analyticsView.showTrips(true, trips)
            }

            override fun onError(obj: Any) {
                AppLogger.printRetrofitError(obj)
                analyticsView.showTrips(false, trips)
            }
        })

    }

    override fun setTrip(position: Int) {
        trip = trips[position]
    }

    override fun setAnalytics(date: Date) {
        CacheUtil.instance!!.analyticsType = analyticsType
        CacheUtil.instance!!.analyticsUser = user
        CacheUtil.instance!!.analyticsVehicle = vehicle
        CacheUtil.instance!!.analyticsTrip = trip
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        CacheUtil.instance!!.analyticsDate = sdf.format(date)
        if(!analyticsType!!.isTrip) {
            analyticsView.showAnalyticsChart()
        } else if(trip != null) {
            analyticsView.showAnalyticsMap()
        } else {
            analyticsView.showError("Unable to generate analytics.")
        }
    }

    override fun getAnalytics() {
        analyticsType = CacheUtil.instance!!.analyticsType
        if(analyticsType!!.isVehicle) {
            vehicle = CacheUtil.instance!!.analyticsVehicle
            if(analyticsType!!.isDate) {
                AnalyticsApiCall.vehicleLog(analyticsType!!, vehicle!!, CacheUtil.instance!!.analyticsDate!!, object : ApiRequestListener {
                    override fun onSuccess(obj: Any) {
                        val analyticsResponse = Gson().fromJson(obj.toString(), AnalyticsResponse::class.java)
                        when {
                            analyticsResponse.success -> {
                                var sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                                val date = sdf.parse(analyticsResponse.date!!)
                                sdf = SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH)
                                analyticsView.showVehicleLogs(CacheUtil.instance!!.analyticsType!!, analyticsResponse.vehicle!!, sdf.format(date!!), analyticsResponse.unit!!, analyticsResponse.time!!, analyticsResponse.vehicleLogs!!)
                            }
                            analyticsResponse.error != null -> {
                                analyticsView.showError(analyticsResponse.error!!)
                            }
                            else -> {
                                analyticsView.showError("An unexpected error occurred.")
                            }
                        }
                    }

                    override fun onError(obj: Any) {
                        AppLogger.printRetrofitError(obj)
                        val errorResponse = Gson().fromJson(obj.toString(), ErrorResponse::class.java)
                        if(errorResponse.error != null) {
                            analyticsView.showError(errorResponse.error!!)
                        } else {
                            analyticsView.showError("An unexpected error occurred.")
                        }
                    }

                })
            } else if(analyticsType!!.isTrip) {
                AnalyticsApiCall.trip(analyticsType!!, CacheUtil.instance!!.analyticsTrip!!, object : ApiRequestListener {
                    override fun onSuccess(obj: Any) {
                        val analyticsResponse = Gson().fromJson(obj.toString(), AnalyticsResponse::class.java)
                        when {
                            analyticsResponse.success -> {

                                var unlockTime: String? = null
                                var lockTime: String? = null
                                var duration: String? = null

                                val trip = analyticsResponse.trip

                                val fromSdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
                                fromSdf.timeZone = TimeZone.getTimeZone("GMT+0")

                                val toSdf = SimpleDateFormat("MMMM d, yyyy h:mm a", Locale.ENGLISH)
                                toSdf.timeZone = TimeZone.getDefault()

                                if(trip!!.unlockTime != null) {
                                    val unlockTimeDate = fromSdf.parse(trip.unlockTime!!)
                                    unlockTime = toSdf.format(unlockTimeDate!!)
                                }

                                if(trip.lockTime != null) {
                                    val lockTimeDate = fromSdf.parse(trip.lockTime!!)
                                    lockTime = toSdf.format(lockTimeDate!!)
                                }

                                if(trip.unlockTime != null && trip.lockTime != null) {
                                   duration = getTripDuration(trip)
                                }

                                val latLngs = LinkedList<LatLng>()
                                val builder = LatLngBounds.Builder()

                                for(route in analyticsResponse.route!!) {
                                    val latLng = LatLng(route[0], route[1])
                                    latLngs.add(latLng)
                                    builder.include(latLng)
                                }
                                analyticsView.showTrip(trip.vehicle.name, unlockTime, lockTime, duration, latLngs, builder.build())
                            }
                            analyticsResponse.error != null -> {
                                analyticsView.showError(analyticsResponse.error!!)
                            }
                            else -> {
                                analyticsView.showError("An unexpected error occurred.")
                            }
                        }
                    }

                    override fun onError(obj: Any) {
                        AppLogger.printRetrofitError(obj)
                        val errorResponse = Gson().fromJson(obj.toString(), ErrorResponse::class.java)
                        if(errorResponse.error != null) {
                            analyticsView.showError(errorResponse.error!!)
                        } else {
                            analyticsView.showError("An unexpected error occurred.")
                        }
                    }

                })
            }
        }
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

}
