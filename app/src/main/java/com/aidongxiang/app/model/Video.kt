package com.aidongxiang.app.model

import com.aiitec.openapi.model.Entity

/**
 *
 * @author Anthony
 * createTime 2017/11/11.
 * @version 1.0
 */
class Video : Entity(){

    var dirPath : String ?= null
    var thumbPath : String ?= null
    var path : String ?= null
    var name : String ?= null
    var duration : Long = 0
    var size : Long = 0
    var id : Long = 0



}