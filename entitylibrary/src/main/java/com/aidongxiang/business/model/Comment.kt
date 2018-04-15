package com.aidongxiang.business.model

import com.aiitec.openapi.db.annotation.Unique
import com.aiitec.openapi.model.Entity

/**
 *
 * @author Anthony
 * createTime 2017/12/7.
 * @version 1.0
 */
class Comment : Entity(){

    @Unique
    var id : Long = 0
    var praiseNum = 0
    var isPraise = 0
    var name : String ?= null
    var timestamp : String ?= null
    var content : String ?= null
    var user : User ?= null
}