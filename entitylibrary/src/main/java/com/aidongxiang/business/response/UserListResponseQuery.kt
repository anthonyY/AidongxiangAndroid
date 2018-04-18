package com.aidongxiang.business.response

import com.aidongxiang.business.model.User
import com.aiitec.openapi.model.ListResponseQuery

/**
 *
 * @author Anthony
 * createTime 2018/4/17.
 * @version 1.0
 */
class UserListResponseQuery : ListResponseQuery(){
    var users : ArrayList<User> ?= null
}