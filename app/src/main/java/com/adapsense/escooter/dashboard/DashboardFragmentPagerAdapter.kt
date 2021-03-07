package com.adapsense.escooter.dashboard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.adapsense.escooter.analytics.views.AnalyticsFragment
import com.adapsense.escooter.threads.views.ThreadsFragment
import com.adapsense.escooter.vehicles.view.VehiclesFragment
import com.adapsense.escooter.users.views.UsersFragment
import java.util.*

class DashboardFragmentPagerAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragments: LinkedList<Fragment> = LinkedList()

    init {
        fragments.add(VehiclesFragment.newInstance())
        fragments.add(UsersFragment.newInstance())
        fragments.add(ThreadsFragment.newInstance())
        fragments.add(AnalyticsFragment.newInstance())
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position) {
            0 -> return "Scooters"
            1 -> return "Users"
            2 -> return "Messages"
            3 -> return "Analytics"
        }
        return null
    }

}