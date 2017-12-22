package com.aidongxiang.business.model

import com.aiitec.openapi.model.Entity

/**
 *
 * @author Anthony
 * createTime 2017/12/9.
 * @version 1.0
 */
class User : Entity(){
    var id = 0
    var name : String ?= null
    var imagePath : String ?= null
}