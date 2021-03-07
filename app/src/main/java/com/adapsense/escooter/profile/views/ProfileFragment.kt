package com.adapsense.escooter.profile.views

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.adapsense.escooter.R
import com.adapsense.escooter.base.BaseFragment
import com.adapsense.escooter.home.HomeActivity
import com.adapsense.escooter.profile.ProfileContract
import com.adapsense.escooter.profile.ProfilePresenter
import com.adapsense.escooter.splash.SplashActivity
import com.adapsense.escooter.util.AppLogger
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment: BaseFragment(), ProfileContract.View {

    private lateinit var profilePresenter: ProfileContract.Presenter

    private var profileDialogFragment: ProfileDialogFragment? = null

    companion object {

        @JvmStatic
        fun newInstance() = ProfileFragment()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        profilePresenter = ProfilePresenter(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profilePresenter.start()
        profilePresenter.getUser()
    }

    override fun setPresenter(presenter: ProfileContract.Presenter?) {

    }

    override fun setupViews() {

        changePassword.setOnClickListener {
            if(profileDialogFragment != null) {
                profileDialogFragment!!.dismiss()
            }
            profileDialogFragment = ProfileDialogFragment.newInstance(true, profilePresenter)
            profileDialogFragment!!.show(activity!!.supportFragmentManager, ProfileDialogFragment::class.java.simpleName)
        }

        logOut.setOnClickListener {
            (activity as HomeActivity).showAlertDialog("Logout", "Are you sure you want to logout?", "Logout",
                { dialog, _ ->
                    dialog!!.dismiss()
                    (activity as HomeActivity).showProgressDialog()
                    profilePresenter.logOut()
                }, "Cancel",
                { dialog, _ -> dialog!!.dismiss() })
        }
    }

    override fun showUser(userFullName: String, userEmailAddress: String) {
        (activity as HomeActivity).dismissDialogs()
        fullName.text = userFullName
        emailAddress.text = userEmailAddress
        if(profileDialogFragment != null) {
            profileDialogFragment!!.showUser(userFullName, userEmailAddress)
        }
    }

    override fun showEditProfile() {
        if(profileDialogFragment != null) {
            profileDialogFragment!!.dismiss()
        }
        profileDialogFragment = ProfileDialogFragment.newInstance(false, profilePresenter)
        profileDialogFragment!!.show(activity!!.supportFragmentManager, ProfileDialogFragment::class.java.simpleName)
    }

    override fun showErrorFullName(error: String?) {
        profileDialogFragment?.apply {
            showErrorFullName(error)
        }
    }

    override fun showErrorOldPassword(error: String?) {
        profileDialogFragment?.apply {
            showErrorOldPassword(error)
        }
    }

    override fun showErrorNewPassword(error: String?) {
        profileDialogFragment?.apply {
            showErrorNewPassword(error)
        }
    }

    override fun showErrorConfirmPassword(error: String?) {
        profileDialogFragment?.apply {
            showErrorConfirmPassword(error)
        }
    }

    override fun showError(error: String?) {
        profileDialogFragment?.apply {
            dismiss()
        }
        (activity as HomeActivity).showAlertDialog("", error)
    }

    override fun showSplash() {
        (activity as HomeActivity).dismissDialogs()
        activity!!.startActivity(Intent(activity!!, SplashActivity::class.java))
        activity!!.finish()
    }

}