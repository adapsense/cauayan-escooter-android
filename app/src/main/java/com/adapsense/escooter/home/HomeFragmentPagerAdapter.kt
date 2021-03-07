package com.adapsense.escooter.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.adapsense.escooter.dashboard.DashboardFragment
import com.adapsense.escooter.trips.views.TripsFragment
import com.adapsense.escooter.menu.MenuFragment
import com.adapsense.escooter.profile.views.ProfileFragment
import java.util.*

class HomeFragmentPagerAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragments: LinkedList<Fragment> = LinkedList()

    init {
        fragments.add(DashboardFragment.newInstance())
        fragments.add(TripsFragment.newInstance())
        fragments.add(ProfileFragment.newInstance())
        fragments.add(MenuFragment.newInstance())
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

}