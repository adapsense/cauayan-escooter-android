package com.adapsense.escooter.menu

class MenuPresenter(private val menuView: MenuContract.View) : MenuContract.Presenter {

    init {
        menuView.setPresenter(this)
    }

    override fun start() {
        menuView.setupViews()
    }

}
