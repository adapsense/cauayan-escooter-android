package com.adapsense.escooter.users

import com.adapsense.escooter.api.models.objects.User
import com.adapsense.escooter.api.models.objects.UserStatus
import com.adapsense.escooter.api.models.objects.Vehicle
import com.adapsense.escooter.base.BasePresenter
import com.adapsense.escooter.base.BaseView
import com.adapsense.escooter.users.views.UserViewHolder
import java.util.*

interface UsersContract {

    interface View : BaseView<Presenter?> {

        fun showUsers(users: LinkedList<User>)

        fun setUser(user: User)

        fun editUser()

        fun showErrorFullName(error: String?)

        fun showMessage(message: String)

    }

    interface Presenter : BasePresenter {

        fun getStatuses()

        fun getUserStatuses(): LinkedList<UserStatus>

        fun getUserStatus(user: User): Int

        fun setUserStatus(position: Int)

        fun getUsers()

        fun getUser(): User

        fun getUser(position: Int): User

        fun onBindUserViewAtPosition(position: Int, holder: UserViewHolder)

        fun getUsersCount(): Int

        fun editUser(user: User)

        fun updateUser(fullName: String)

        fun deleteUser(user: User)

    }

}
