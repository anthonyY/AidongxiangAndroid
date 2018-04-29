package com.aidongxiang.business.model

import com.aiitec.openapi.db.annotation.Unique
import com.aiitec.openapi.model.Entity
import java.util.*

/**
 *
 * @author Anthony
 * createTime 2018/1/20.
 * @version 1.0
 */
class SearchText() : Entity(){

    @Unique
    var text : String ?= null
    var timestamp : Date ?= null
    constructor(text : String, timestamp : Date) : this(){
        this.text = text
        this.timestamp = timestamp
    }
}