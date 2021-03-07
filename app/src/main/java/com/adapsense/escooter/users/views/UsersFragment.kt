package com.adapsense.escooter.users.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapsense.escooter.R
import com.adapsense.escooter.api.models.objects.User
import com.adapsense.escooter.base.BaseActivity
import com.adapsense.escooter.base.BaseFragment
import com.adapsense.escooter.dashboard.DashboardFragment
import com.adapsense.escooter.home.HomeActivity
import com.adapsense.escooter.users.UsersContract
import com.adapsense.escooter.users.UsersPresenter
import com.adapsense.escooter.users.UsersRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_users.*
import java.util.*

class UsersFragment: BaseFragment(), UsersContract.View {

    private lateinit var usersPresenter: UsersContract.Presenter

    private var userDialogFragment: UserDialogFragment? = null

    private lateinit var adapter: UsersRecyclerViewAdapter

    companion object {

        @JvmStatic
        fun newInstance() = UsersFragment()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        usersPresenter = UsersPresenter(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        usersPresenter.start()
        swipeRefreshLayout.isRefreshing = true
        usersPresenter.getUsers()
    }


    override fun setPresenter(presenter: UsersContract.Presenter?) {
        usersPresenter = presenter!!
    }

    override fun setupViews() {
        swipeRefreshLayout.setOnRefreshListener {
            empty.visibility = View.GONE
            recyclerView.visibility = View.GONE
            usersPresenter.getUsers()
        }

        adapter = UsersRecyclerViewAdapter(usersPresenter)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = this@UsersFragment.adapter
        }
    }

    override fun showUsers(users: LinkedList<User>) {
        (activity as HomeActivity).dismissDialogs()
        if(userDialogFragment != null) {
            userDialogFragment!!.dismiss()
        }
        swipeRefreshLayout.isRefreshing = false
        if(users.size > 0) {
            empty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        } else {
            empty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
        adapter.notifyDataSetChanged()
        (parentFragment as DashboardFragment).setRiders(users)
    }

    override fun setUser(user: User) {

    }

    override fun editUser() {
        if(userDialogFragment != null) {
            userDialogFragment!!.dismiss()
        }
        userDialogFragment = UserDialogFragment(usersPresenter)
        userDialogFragment!!.show(parentFragment!!.childFragmentManager, UserDialogFragment::class.java.simpleName)
    }

    override fun showErrorFullName(error: String?) {
        if(userDialogFragment != null) {
            userDialogFragment!!.showErrorFullName(error)
        }
    }

    override fun showMessage(message: String) {
        (activity as BaseActivity).dismissDialogs()
        Toast.makeText(activity!!, message, Toast.LENGTH_SHORT).show()
    }

}