package com.adapsense.escooter.users

import com.adapsense.escooter.api.ApiRequestListener
import com.adapsense.escooter.api.calls.UsersApiCall
import com.adapsense.escooter.api.models.objects.User
import com.adapsense.escooter.api.models.objects.UserStatus
import com.adapsense.escooter.api.models.responses.ErrorResponse
import com.adapsense.escooter.api.models.responses.UserResponse
import com.adapsense.escooter.api.models.responses.UserStatusesResponse
import com.adapsense.escooter.api.models.responses.UsersResponse
import com.adapsense.escooter.users.views.UserViewHolder
import com.adapsense.escooter.util.AppLogger
import com.google.gson.Gson
import java.util.*

class UsersPresenter(private val usersView: UsersContract.View) : UsersContract.Presenter {

    private val userStatuses: LinkedList<UserStatus> = LinkedList()

    private var users: LinkedList<User> = LinkedList()

    private var user: User? = null

    init {
        usersView.setPresenter(this)
    }

    override fun start() {
        usersView.setupViews()
        getStatuses()
    }

    override fun getStatuses() {
        UsersApiCall.statuses(object : ApiRequestListener {
            override fun onSuccess(obj: Any) {
                val userStatusesResponse = Gson().fromJson(obj.toString(), UserStatusesResponse::class.java)
                if(userStatusesResponse.success) {
                    userStatuses.clear()
                    userStatuses.addAll(userStatusesResponse.userStatuses!!)
                }
            }

            override fun onError(obj: Any) {
                AppLogger.printRetrofitError(obj)
            }

        })
    }

    override fun getUserStatuses(): LinkedList<UserStatus> {
        return userStatuses
    }

    override fun getUserStatus(user: User): Int {
        for(userStatus in userStatuses) {
            if(userStatus.id == user.userStatus.id) {
                return userStatuses.indexOf(userStatus)
            }
        }
        return 0
    }

    override fun setUserStatus(position: Int) {
        user!!.userStatus = userStatuses[position]
    }

    override fun getUsers() {
        UsersApiCall.get(object : ApiRequestListener {
            override fun onSuccess(obj: Any) {
                val usersResponse = Gson().fromJson(obj.toString(), UsersResponse::class.java)
                if(usersResponse.success) {
                    users.clear()
                    users.addAll(usersResponse.users!!)
                }
                usersView.showUsers(users)
            }

            override fun onError(obj: Any) {
                AppLogger.printRetrofitError(obj)
                usersView.showUsers(users)
            }

        })
    }

    override fun getUser(): User {
        return user!!
    }

    override fun getUser(position: Int): User {
        return users[position]
    }

    override fun onBindUserViewAtPosition(position: Int, holder: UserViewHolder) {
        holder.setUser(users[position])
    }

    override fun getUsersCount(): Int {
        return this.users.size
    }

    override fun editUser(user: User) {
        this.user = user
        usersView.editUser()
    }

    override fun updateUser(fullName: String) {
        var valid = true

        if(fullName.isEmpty()) {
            valid = false
            usersView.showErrorFullName("Full name is required")
        } else {
            user!!.fullName = fullName
            usersView.showErrorFullName(null)
        }

        if(valid) {
            UsersApiCall.update(user!!, object : ApiRequestListener {
                override fun onSuccess(obj: Any) {
                    val userResponse = Gson().fromJson(obj.toString(), UserResponse::class.java)
                    when {
                        userResponse.success  -> {
                            val updatedUser = userResponse.user
                            if(updatedUser != null) {
                                for(user in users) {
                                    if(user.id == updatedUser.id) {
                                        users[users.indexOf(user)] = user
                                        usersView.showUsers(users)
                                    }
                                }
                            } else {
                                usersView.showMessage("An unexpected error occurred.")
                            }
                        }
                        userResponse.error != null -> {
                            usersView.showMessage(userResponse.error!!)
                        }
                        else -> {
                            usersView.showMessage("An unexpected error occurred.")
                        }
                    }
                }

                override fun onError(obj: Any) {
                    AppLogger.printRetrofitError(obj)
                    val errorResponse = Gson().fromJson(obj.toString(), ErrorResponse::class.java)
                    if(errorResponse.error != null) {
                        usersView.showMessage(errorResponse.error!!)
                    } else {
                        usersView.showMessage("An unexpected error occurred.")
                    }
                }
            })
        }
    }

    override fun deleteUser(user: User) {
        UsersApiCall.delete(user, object : ApiRequestListener {
            override fun onSuccess(obj: Any) {
                val userResponse = Gson().fromJson(obj.toString(), UserResponse::class.java)
                when {
                    userResponse.success -> {
                        usersView.showMessage( "${user.fullName} deleted.")
                        users.remove(user)
                        usersView.showUsers(users)
                    }
                    userResponse.error != null -> {
                        usersView.showMessage(userResponse.error!!)
                    }
                    else -> {
                        usersView.showMessage("An error occurred while deleting the vehicle.")
                    }
                }
            }

            override fun onError(obj: Any) {
                AppLogger.printRetrofitError(obj)
                val errorResponse = Gson().fromJson(obj.toString(), ErrorResponse::class.java)
                if(errorResponse.error != null) {
                    usersView.showMessage(errorResponse.error!!)
                } else {
                    usersView.showMessage("An error occurred while deleting the vehicle.")
                }
            }

        })
    }

}
