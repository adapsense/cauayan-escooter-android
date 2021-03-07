package com.adapsense.escooter.about

class AboutPresenter(private val aboutView: AboutContract.View) : AboutContract.Presenter {

    init {
        aboutView.setPresenter(this)
    }

    override fun start() {
       aboutView.setupViews()
    }

}
