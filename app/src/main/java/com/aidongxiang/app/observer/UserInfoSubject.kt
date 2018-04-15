package com.aidongxiang.app.observer

import com.aidongxiang.business.model.User

/**
 * 用户信息订阅者
 * @author Anthony
 * createTime 2018/04/12.
 * @version 1.0
 */
class UserInfoSubject private constructor() : IUserInfoChangeObserver {
    override fun update(user: User) {
        observers.forEach { o -> o.update(user) }
    }

    override fun logout() {
        for (o in observers) o.logout()
    }


    companion object {
        private var userInfoSubject: UserInfoSubject? = null
        fun getInstance(): UserInfoSubject {
            if (userInfoSubject == null) {
                userInfoSubject = UserInfoSubject()
            }
            return userInfoSubject!!
        }
    }

    private val observers = ArrayList<IUserInfoChangeObserver>()

    fun registerObserver(o: IUserInfoChangeObserver) {
        observers.add(o)
    }

    fun removeObserver(o: IUserInfoChangeObserver) {
        if (observers.indexOf(o) >= 0) {
            observers.remove(o)
        }
    }

}