package com.aidongxiang.business.request

import com.aiitec.openapi.json.annotation.JSONField
import com.aiitec.openapi.model.RequestQuery

/**
 *
 * @author Anthony
 * createTime 2018/4/7.
 * @version 1.0
 */
class UserRegisterRequestQuery : RequestQuery(){
    var smscodeId = -1
    var mobile : String ?= null
    var headImageId : Long = -1
    var nickName : String ?= null
    @JSONField(isPassword = true)
    var password : String ?= null
    var openId : String ?= null
    var unionId : String ?= null
    var partner : String ?= null

}