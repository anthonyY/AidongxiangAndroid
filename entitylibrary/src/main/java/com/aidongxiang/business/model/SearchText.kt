package com.aidongxiang.business.model

import com.aiitec.openapi.db.annotation.Unique
import com.aiitec.openapi.model.Entity

/**
 *
 * @author Anthony
 * createTime 2018/1/20.
 * @version 1.0
 */
class SearchText() : Entity(){

    @Unique
    var text : String ?= null
    constructor(text : String) : this(){
        this.text = text
    }
}