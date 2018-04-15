package com.aidongxiang.business.model

import com.aiitec.openapi.json.JSON
import com.aiitec.openapi.model.Entity

/**
 *
 * @author Anthony
 * createTime 2018/1/14.
 * @version 1.0
 */
class Article : Entity(){

    var id : Long= -1
    var title : String ?= null
    var abstract : String ?= null
    var imagePath : String ?= null
    var timestamp : String ?= null
    var categoryName : String ?= null
    var content : String ?= null
    fun a(){
        JSON.combinationType
    }

}