package com.aidongxiang.business.response

import com.aidongxiang.business.model.User
import com.aiitec.openapi.model.ResponseQuery

/**
 *
 * @author Anthony
 * createTime 2017/12/9.
 * @version 1.0
 */
class UserDetailsResponseQuery : ResponseQuery(){

    var user : User ?= null
}