package com.adapsense.escooter.users.views

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.adapsense.escooter.api.models.objects.User
import com.adapsense.escooter.base.BaseActivity
import com.adapsense.escooter.users.UsersContract
import kotlinx.android.synthetic.main.item_user.view.*
import java.util.*

class UserViewHolder(private var usersPresenter: UsersContract.Presenter, itemView: View?) : ViewHolder(itemView!!), UsersContract.View {

	init {
        setupViews()
    }

	override fun setPresenter(presenter: UsersContract.Presenter?) {
		usersPresenter = presenter!!
	}

	override fun setupViews() {

	}

	override fun showUsers(users: LinkedList<User>) {

	}

	override fun setUser(user: User) {
		itemView.edit.setOnClickListener {
			itemView.swipeRevealLayout.close(true)
			usersPresenter.editUser(user)
		}

		itemView.deleteContainer.visibility = if(user.userStatus.isDeleted) View.GONE else View.VISIBLE

		itemView.delete.setOnClickListener {
			itemView.swipeRevealLayout.close(true)
			(itemView.context as BaseActivity).showAlertDialog("Delete User", "Are you sure you want to delete ${user.fullName}?", "Delete",
				{ dialog, _ ->
					dialog!!.dismiss()
					(itemView.context as BaseActivity).showProgressDialog()
					usersPresenter.deleteUser(user)
				}, "Cancel"
			) { dialog, _ -> dialog!!.dismiss() }
		}

		itemView.container.setOnClickListener {
			usersPresenter.editUser(user)
		}

		itemView.fullName.text = user.fullName
		itemView.email.text = user.email
		itemView.type.text = user.userType.name
		itemView.status.text = user.userStatus.name

	}

	override fun editUser() {

	}

	override fun showErrorFullName(error: String?) {

	}

	override fun showMessage(message: String) {

	}

}
