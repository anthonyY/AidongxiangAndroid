package com.aidongxiang.business.model

import com.aiitec.openapi.model.Entity

/**
 *
 * @author Anthony
 * createTime 2017/12/22.
 * @version 1.0
 */
class Fans : Entity(){

    var id = -1L
    var fromId = -1L
    var name : String ?= null
    var imagePath : String ?= null
    var description : String ?= null
    var isFocus = -1

}