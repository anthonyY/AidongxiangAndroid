package com.aidongxiang.business.model

import com.aiitec.openapi.model.Entity

/**
 *
 * @author Anthony
 * createTime 2018/1/14.
 * @version 1.0
 */
class Ad : Entity(){
    
    var id = -1
    var name : String ?= null
    var link : String ?= null
    var startTime : String ?= null
    var endTime : String ?= null
    var imagePath : String ?= null

}