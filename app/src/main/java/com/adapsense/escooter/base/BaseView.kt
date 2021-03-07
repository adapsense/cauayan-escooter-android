package com.adapsense.escooter.base

interface BaseView<T> {

    fun setPresenter(presenter: T)

    fun setupViews()

}
