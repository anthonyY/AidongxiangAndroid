package com.aidongxiang.business.request

import com.aiitec.openapi.json.annotation.JSONField
import com.aiitec.openapi.model.RequestQuery

/**
 *
 * @author Anthony
 * createTime 2018/4/7.
 * @version 1.0
 */
class MobileAppealSubmitRequestQuery : RequestQuery(){
    var mobile : String ?= null
    var newMobile : String ?= null
    @JSONField(isPassword = true)
    var password : String ?= null
    var registerTime : String ?= null

}