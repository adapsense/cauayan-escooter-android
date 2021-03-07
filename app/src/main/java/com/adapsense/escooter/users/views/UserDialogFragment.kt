package com.adapsense.escooter.users.views

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.adapsense.escooter.R
import com.adapsense.escooter.api.models.objects.*
import com.adapsense.escooter.base.BaseActivity
import com.adapsense.escooter.users.UsersContract
import com.adapsense.escooter.users.UsersPresenter
import kotlinx.android.synthetic.main.fragment_dialog_user.*
import java.util.*

class UserDialogFragment(private var usersPresenter: UsersContract.Presenter) : DialogFragment(), UsersContract.View {

    companion object {

        @JvmStatic
        fun newInstance(usersPresenter: UsersPresenter) = UserDialogFragment(usersPresenter)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dialog_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setupViews()
        setUser(usersPresenter.getUser())
    }

    override fun setPresenter(presenter: UsersContract.Presenter?) {
        usersPresenter = presenter!!
    }

    override fun setupViews() {

        fullNameTextInputEditText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                save.performClick()
                val inputMethodManager =
                    activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(fullNameTextInputEditText.windowToken, 0)
                return@OnEditorActionListener true
            }
            false
        })

        cancel.setOnClickListener {
            dismiss()
        }

        save.setOnClickListener {
            (activity as BaseActivity).showProgressDialog()
            usersPresenter.updateUser(fullNameTextInputEditText.text.toString().trim())
        }

    }

    override fun showUsers(users: LinkedList<User>) {

    }

    override fun setUser(user: User) {

        fullNameTextInputEditText.setText("")
        fullNameTextInputEditText.append(user.fullName)

        val adapter: ArrayAdapter<UserStatus> = ArrayAdapter<UserStatus>(
            activity!!,
            R.layout.spinner,
            usersPresenter.getUserStatuses()
        )
        adapter.setDropDownViewResource(R.layout.item_spinner)
        status.adapter = adapter
        status.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                usersPresenter.setUserStatus(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
        status.setSelection(usersPresenter.getUserStatus(user))

    }

    override fun editUser() {

    }

    override fun showErrorFullName(error: String?) {
        if(!error.isNullOrEmpty()) {
            (activity as BaseActivity).dismissDialogs()
        }
        fullNameTextInputEditText.error = error
    }

    override fun showMessage(message: String) {

    }

}