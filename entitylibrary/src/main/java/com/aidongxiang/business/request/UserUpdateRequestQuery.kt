package com.aidongxiang.business.request

import com.aidongxiang.business.model.User
import com.aiitec.openapi.model.RequestQuery

/**
 *
 * @author Anthony
 * createTime 2018/4/13.
 * @version 1.0
 */
class UserUpdateRequestQuery : RequestQuery(){
    var user : User ?= null
}