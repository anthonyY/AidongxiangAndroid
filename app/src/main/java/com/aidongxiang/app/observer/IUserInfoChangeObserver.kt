package com.aidongxiang.app.observer

import com.aidongxiang.business.model.User

/**
 * 用户信息更新接口
 * @author Anthony
 * createTime 2017/12/17.
 * @version 1.0
 */
interface IUserInfoChangeObserver {

    fun update(user: User)
    fun logout()

}