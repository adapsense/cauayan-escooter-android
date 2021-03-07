package com.adapsense.escooter.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.adapsense.escooter.R
import com.adapsense.escooter.users.views.UserViewHolder
import com.chauthai.swipereveallayout.ViewBinderHelper
import kotlinx.android.synthetic.main.item_user.view.*

class UsersRecyclerViewAdapter(private val usersPresenter: UsersContract.Presenter) : Adapter<UserViewHolder>() {

    private val viewBinderHelper = ViewBinderHelper()

    init {
        viewBinderHelper.setOpenOnlyOne(true)
    }

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(usersPresenter, LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false))
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        viewBinderHelper.bind(holder.itemView.swipeRevealLayout, usersPresenter.getUser(position).id);
        usersPresenter.onBindUserViewAtPosition(position, holder)
    }

    override fun getItemCount(): Int {
        return usersPresenter.getUsersCount()
    }

}
