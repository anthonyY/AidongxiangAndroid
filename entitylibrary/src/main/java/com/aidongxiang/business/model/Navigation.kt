package com.aidongxiang.business.model

import com.aiitec.openapi.model.Entity

/**
 *
 * @author Anthony
 * createTime 2018/4/19.
 * @version 1.0
 */
class Navigation : Entity(){
    var id : Long = -1
    var icon : String ?= null
    var name : String ?= null
    var link : String ?= null

}