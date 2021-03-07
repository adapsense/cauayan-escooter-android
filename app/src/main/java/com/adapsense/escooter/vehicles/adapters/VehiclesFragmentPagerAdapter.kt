package com.adapsense.escooter.vehicles.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.adapsense.escooter.api.models.objects.Vehicle
import com.adapsense.escooter.vehicles.view.VehicleFragment
import java.util.*

class VehiclesFragmentPagerAdapter(fragmentManager: FragmentManager, vehicles: LinkedList<Vehicle>): FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragments: LinkedList<Fragment> = LinkedList()

    init {
        for(vehicle in vehicles) {
            val vehicleFragment = VehicleFragment.newInstance()
            vehicleFragment.setVehicle(vehicle, false)
            fragments.add(vehicleFragment)
        }
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

}