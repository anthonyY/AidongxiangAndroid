package com.aidongxiang.business.model

import com.aiitec.openapi.model.Entity

/**
 *
 * @author Anthony
 * createTime 2017/12/3.
 * @version 1.0
 */
class LocalVideo : Entity(){
    var dirPath : String ?= null
    var thumbPath : String ?= null
    var path : String ?= null
    var duration : Long = 0
    var size : Long = 0
}