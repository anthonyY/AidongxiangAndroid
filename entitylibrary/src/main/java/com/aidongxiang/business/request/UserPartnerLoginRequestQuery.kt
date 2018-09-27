package com.aidongxiang.business.request

import com.aiitec.openapi.model.RequestQuery

/**
 *
 * @author Anthony
 * createTime 2018/6/6.
 * @version 1.0
 */
class UserPartnerLoginRequestQuery : RequestQuery(){

    var openId : String ?= null
    var unionId : String ?= null
    var nickname : String ?= null
    var imageUrl : String ?= null
    var sex = 1
    var partner = 1

}