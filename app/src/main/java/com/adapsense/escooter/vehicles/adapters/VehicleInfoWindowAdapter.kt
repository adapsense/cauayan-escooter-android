package com.adapsense.escooter.vehicles.adapters

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.adapsense.escooter.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class VehicleInfoWindowAdapter(private val layoutInflater: LayoutInflater) : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(p0: Marker?): View? {
        return null
    }

    override fun getInfoContents(marker: Marker?): View {
        val view = layoutInflater.inflate(R.layout.info_window_vehicle, null)

        view.findViewById<TextView>(R.id.label).text = marker!!.title

        return view
    }
}