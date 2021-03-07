package com.adapsense.escooter.about

import android.os.Bundle
import android.view.View
import com.adapsense.escooter.BuildConfig
import com.adapsense.escooter.R
import com.adapsense.escooter.base.BaseActivity
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.navigation.*

class AboutActivity : BaseActivity(), AboutContract.View {

    private lateinit var aboutPresenter: AboutContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        aboutPresenter = AboutPresenter(this)
        aboutPresenter.start()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
    }

    override fun setPresenter(presenter: AboutContract.Presenter?) {
        aboutPresenter = presenter!!
    }

    override fun setupViews() {

        back.visibility = View.VISIBLE

        back.setOnClickListener { onBackPressed() }

        titleText.text = "About"

        versionBuild.text = "Version: ${BuildConfig.VERSION_NAME} - Build: ${BuildConfig.VERSION_CODE}"

    }

}