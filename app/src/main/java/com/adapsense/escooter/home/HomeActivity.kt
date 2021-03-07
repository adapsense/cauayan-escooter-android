package com.adapsense.escooter.home

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.adapsense.escooter.R
import com.adapsense.escooter.base.BaseActivity
import com.adapsense.escooter.dashboard.DashboardFragment
import com.adapsense.escooter.api.models.objects.Vehicle
import com.adapsense.escooter.profile.views.ProfileFragment
import com.adapsense.escooter.scan.ScanActivity
import com.adapsense.escooter.util.CacheUtil
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.navigation.*

class HomeActivity : BaseActivity(), HomeContract.View {

    private lateinit var homePresenter: HomeContract.Presenter

    private lateinit var adapter: HomeFragmentPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        homePresenter = HomePresenter(this)
        homePresenter.start()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun setPresenter(presenter: HomeContract.Presenter?) {
        homePresenter = presenter!!
    }

    override fun setupViews() {

        titleText.text = "Dashboard"
        actionIcon.visibility = if(CacheUtil.instance!!.user!!.userType.isAdmin) View.INVISIBLE else View.VISIBLE

        actionIcon.setOnClickListener {
            showScan()
        }

        actionText.setOnClickListener {
            (adapter.getItem(2) as ProfileFragment).showEditProfile()
        }

        adapter = HomeFragmentPagerAdapter(supportFragmentManager)

        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 3
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }
            override fun onPageSelected(position: Int) {
                setTitle(position)
            }
            override fun onPageScrollStateChanged(state: Int) {}
        })

        bottomNavigationView.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.dashboard -> viewPager.currentItem = 0
                R.id.history -> viewPager.currentItem = 1
                R.id.profile -> viewPager.currentItem = 2
                R.id.menu -> viewPager.currentItem = 3
            }
            false
        }

    }

    override fun setTitle(position: Int) {
        bottomNavigationView.menu.getItem(position).isChecked = true
        when(position) {
            0 -> titleText.text = "Dashboard"
            1 -> titleText.text = "History"
            2 -> titleText.text = "Profile"
            3 -> titleText.text = "Menu"
        }
        actionIcon.visibility = if(position == 0) View.VISIBLE else View.INVISIBLE
        actionText.visibility = if(position == 2) View.VISIBLE else View.INVISIBLE
    }

    override fun showScan() {
        startActivity(Intent(this, ScanActivity::class.java))
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
    }

    override fun hideScan(hide: Boolean) {
        actionIcon.visibility = if(!hide) View.VISIBLE else View.INVISIBLE
    }

    override fun updateVehicle(settings: Boolean, vehicle: Vehicle) {
        (adapter.getItem(0) as DashboardFragment).updateVehicle(settings, vehicle)
    }

}