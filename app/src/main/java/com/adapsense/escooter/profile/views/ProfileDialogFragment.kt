package com.adapsense.escooter.profile.views

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.adapsense.escooter.R
import com.adapsense.escooter.base.BaseActivity
import com.adapsense.escooter.profile.ProfileContract
import kotlinx.android.synthetic.main.fragment_dialog_profile.*
import kotlinx.android.synthetic.main.fragment_dialog_profile.fullNameTextInputEditText

class ProfileDialogFragment(private val changePassword: Boolean, private var profilePresenter: ProfileContract.Presenter) : DialogFragment(), ProfileContract.View {

    companion object {

        @JvmStatic
        fun newInstance(changePassword: Boolean, profilePresenter: ProfileContract.Presenter) = ProfileDialogFragment(changePassword, profilePresenter)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dialog_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setupViews()
        profilePresenter.getUser()
    }

    override fun setPresenter(presenter: ProfileContract.Presenter?) {
        profilePresenter = presenter!!
    }

    override fun setupViews() {

        title.text = if(!changePassword) "Edit Profile" else "Change Password"
        fullNameTextInputLayout.visibility = if(!changePassword) View.VISIBLE else View.GONE
        passwordContainer.visibility = if(!changePassword) View.GONE else View.VISIBLE

        fullNameTextInputEditText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                save.performClick()
                val inputMethodManager = activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(fullNameTextInputEditText.windowToken, 0)
                return@OnEditorActionListener true
            }
            false
        })

        confirmPasswordTextInputEditText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                save.performClick()
                val inputMethodManager = activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(confirmPasswordTextInputEditText.windowToken, 0)
                return@OnEditorActionListener true
            }
            false
        })

        save.setOnClickListener {
            (activity as BaseActivity).showProgressDialog()
            if(!changePassword) {
                profilePresenter.editUser(fullNameTextInputEditText.text.toString().trim())
            } else {
                profilePresenter.changePassword(oldPasswordTextInputEditText.text.toString().trim(), newPasswordTextInputEditText.text.toString().trim(), confirmPasswordTextInputEditText.text.toString().trim())
            }
        }

    }

    override fun showUser(userFullName: String, userEmailAddress: String) {
        fullNameTextInputEditText.setText("")
        fullNameTextInputEditText.append(userFullName)
        if(!changePassword) {
            fullNameTextInputEditText.requestFocus()
        } else {
            oldPasswordTextInputEditText.requestFocus()
        }
    }

    override fun showEditProfile() {

    }

    override fun showErrorFullName(error: String?) {
        if(!error.isNullOrEmpty()) {
            (activity as BaseActivity).dismissDialogs()
        }
        fullNameTextInputLayout.error = error
    }

    override fun showErrorOldPassword(error: String?) {
        if(!error.isNullOrEmpty()) {
            (activity as BaseActivity).dismissDialogs()
        }
        oldPasswordTextInputLayout.error = error
    }

    override fun showErrorNewPassword(error: String?) {
        if(!error.isNullOrEmpty()) {
            (activity as BaseActivity).dismissDialogs()
        }
        newPasswordTextInputLayout.error = error
    }

    override fun showErrorConfirmPassword(error: String?) {
        if(!error.isNullOrEmpty()) {
            (activity as BaseActivity).dismissDialogs()
        }
        confirmPasswordTextInputLayout.error = error
    }

    override fun showError(error: String?) {

    }

    override fun showSplash() {

    }

}