package com.adapsense.escooter.menu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.adapsense.escooter.R
import com.adapsense.escooter.about.AboutActivity
import com.adapsense.escooter.base.BaseFragment
import com.adapsense.escooter.feedback.FeedbackActivity
import com.adapsense.escooter.messages.views.MessagesActivity
import com.adapsense.escooter.util.CacheUtil
import kotlinx.android.synthetic.main.fragment_menu.*

class MenuFragment: BaseFragment(), MenuContract.View {

    private lateinit var menuPresenter: MenuContract.Presenter

    companion object {

        @JvmStatic
        fun newInstance() = MenuFragment()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        menuPresenter = MenuPresenter(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        menuPresenter.start()
    }

    override fun setPresenter(presenter: MenuContract.Presenter?) {
        menuPresenter = presenter!!
    }

    override fun setupViews() {

        supportContainer.visibility = if(CacheUtil.instance!!.user!!.userType.isAdmin) View.GONE else View.VISIBLE

        supportContainer.setOnClickListener {
            startActivity(Intent(activity, MessagesActivity::class.java))
            activity!!.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        }

        feedbackContainer.setOnClickListener {
            startActivity(Intent(activity, FeedbackActivity::class.java))
            activity!!.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        }

        aboutContainer.setOnClickListener {
            startActivity(Intent(activity, AboutActivity::class.java))
            activity!!.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        }

    }

}