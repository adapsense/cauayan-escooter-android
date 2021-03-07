package com.adapsense.escooter.analytics.views

import android.os.Bundle
import android.view.View
import com.adapsense.escooter.R
import com.adapsense.escooter.analytics.AnalyticsContract
import com.adapsense.escooter.analytics.AnalyticsPresenter
import com.adapsense.escooter.api.models.objects.AnalyticsType
import com.adapsense.escooter.api.models.objects.Trip
import com.adapsense.escooter.api.models.objects.User
import com.adapsense.escooter.api.models.objects.Vehicle
import com.adapsense.escooter.base.BaseActivity
import com.adapsense.escooter.util.CacheUtil
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.data.Set
import com.anychart.enums.Anchor
import com.anychart.enums.MarkerType
import com.anychart.enums.TooltipPositionMode
import com.anychart.graphics.vector.Stroke
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.android.synthetic.main.navigation.*
import java.util.*

class AnalyticsChartActivity : BaseActivity(), AnalyticsContract.View {

    private lateinit var analyticsPresenter: AnalyticsContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics_chart)
        analyticsPresenter = AnalyticsPresenter(this)
    }

    override fun onResume() {
        super.onResume()
        showProgressDialog()
        analyticsPresenter.start()
        analyticsPresenter.getAnalytics()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right)
    }

    override fun setPresenter(presenter: AnalyticsContract.Presenter?) {
        analyticsPresenter = presenter!!
    }

    override fun setupViews() {
        back.visibility = View.VISIBLE
        back.setOnClickListener {
            onBackPressed()
        }
        titleText.text = CacheUtil.instance!!.analyticsType!!.name
    }

    override fun showAnalyticsTypes(analyticsTypes: LinkedList<AnalyticsType>) {

    }

    override fun showUsers(show: Boolean, users: LinkedList<User>) {

    }

    override fun showVehicles(show: Boolean, vehicles: LinkedList<Vehicle>) {

    }

    override fun showDate(show: Boolean) {

    }

    override fun showTrips(show: Boolean, trips: LinkedList<Trip>) {

    }

    override fun showAnalyticsChart() {

    }

    override fun showAnalyticsMap() {

    }

    override fun showVehicleLogs(
        analyticsType: AnalyticsType,
        vehicle: Vehicle,
        date: String,
        unit: String,
        time: List<String>,
        vehicleLogs: List<String>
    ) {
        dismissDialogs()

        val anyChartView = findViewById<AnyChartView>(R.id.anyChartView)
        anyChartView.setProgressBar(findViewById(R.id.progress))

        val cartesian = AnyChart.line()

        cartesian.animation(true)

        cartesian.padding(10.0, 20.0, 5.0, 20.0)

        cartesian.crosshair().enabled(true)
        cartesian.crosshair()
            .yLabel(true)
            .yStroke(null as Stroke?, null, null, null as String?, null as String?)

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT)

        cartesian.title("${vehicle.name} ${analyticsType.name} for $date").padding(0, 0, 20, 0)

        cartesian.yAxis(0).title("${analyticsType.name} ${if (unit.isNotEmpty()) "in $unit" else ""}").labels().fontSize(10)
        cartesian.xAxis(0).labels().fontSize(10).padding(5.0, 5.0, 20.0, 5.0)

        val seriesData: MutableList<DataEntry> = ArrayList()
        for(timeItem in time) {
            seriesData.add(ValueDataEntry(timeItem, vehicleLogs[time.indexOf(timeItem)].toDouble()))
        }

        val set = Set.instantiate()
        set.data(seriesData)
        val series1Mapping = set.mapAs("{ x: 'x', value: 'value' }")

        val series1 = cartesian.line(series1Mapping)
        series1.name(vehicle.name)
        series1.stroke("#18A95C")
        series1.hovered().markers().enabled(true)
        series1.hovered().markers()
            .type(MarkerType.CIRCLE)
            .size(4.0)
        series1.tooltip()
            .position("right")
            .anchor(Anchor.LEFT_CENTER)
            .offsetX(5.0)
            .offsetY(5.0)

        anyChartView.setChart(cartesian)
    }

    override fun showTrip(
        vehicleValue: String,
        unlockTimeValue: String?,
        lockTimeValue: String?,
        durationValue: String?,
        route: LinkedList<LatLng>,
        bounds: LatLngBounds
    ) {

    }

    override fun showError(error: String) {
        dismissDialogs()
        showAlertDialog(
            "", error, "Ok"
        ) { dialog, _ ->
            dialog.dismiss()
            onBackPressed()
        }
    }
}
