package com.adapsense.escooter.home

class HomePresenter(private val homeView: HomeContract.View) : HomeContract.Presenter {

    init {
        homeView.setPresenter(this)
    }

    override fun start() {
        homeView.setupViews()
    }

}
